package ie.ul.ihearthealth.ui.calendar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import ie.ul.ihearthealth.MainActivity
import ie.ul.ihearthealth.R
import ie.ul.ihearthealth.databinding.CalendarDayBinding
import ie.ul.ihearthealth.databinding.CalendarEventItemViewBinding
import ie.ul.ihearthealth.databinding.CalendarFragmentBinding
import ie.ul.ihearthealth.databinding.CalendarHeaderBinding
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*


data class Event(val id: String, val appointmentName: String, val appointmentTime: String, val date: LocalDate)

class CalendarEventsAdapter(val onClick: (Event) -> Unit) :
    RecyclerView.Adapter<CalendarEventsAdapter.CalendarEventsViewHolder>() {

    val events = mutableListOf<Event>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarEventsViewHolder {
        return CalendarEventsViewHolder(
            CalendarEventItemViewBinding.inflate(parent.context.layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(viewHolder: CalendarEventsViewHolder, position: Int) {
        viewHolder.bind(events[position])
    }

    override fun getItemCount(): Int = events.size

    inner class CalendarEventsViewHolder(private val binding: CalendarEventItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                onClick(events[bindingAdapterPosition])
            }
        }

        fun bind(event: Event) {
            binding.itemEventText.text = event.appointmentName + "\nAppointment Time: " + event.appointmentTime
        }
    }
}

class CalendarFragment : BaseFragment(R.layout.calendar_fragment), HasBackButton, EventDialogFragment.EventDialogListener {
    private val dialog: DialogFragment = EventDialogFragment()
    private val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser

    private val eventsAdapter = CalendarEventsAdapter {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.calendar_dialog_action_confirmation)
            .setPositiveButton(R.string.delete) { _, _ ->
                deleteEvent(it)
            }
            .setNegativeButton(R.string.modify) { _, _ ->
                (dialog as EventDialogFragment).isNewEvent = false
                dialog.eventId = it.id.replace(" ", "")
                dialog.eventName = it.appointmentName
                dialog.eventTime = it.appointmentTime
                dialog.eventDate = it.date
                dialog.oldEventDate = it.date
                dialog.event = it
                dialog.show(childFragmentManager, "EventDialogFragment")
            }
            .show()
    }

    override val titleRes: Int = R.string.calendar_title

    private var selectedDate: LocalDate? = null
    private val today = LocalDate.now()

    private val titleSameYearFormatter = DateTimeFormatter.ofPattern("MMMM")
    private val titleFormatter = DateTimeFormatter.ofPattern("MMM yyyy")
    private val selectionFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")
    private val events = mutableMapOf<LocalDate, List<Event>>()

    private lateinit var binding: CalendarFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        readFromDatabase()
        (dialog as EventDialogFragment).setListener(this)
        dialog.eventId = ""
        dialog.eventName = ""
        dialog.eventTime = ""
        dialog.eventDate = null
        dialog.oldEventDate = null
        dialog.isNewEvent = true

        binding = CalendarFragmentBinding.bind(view)

        binding.exThreeRv.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = eventsAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
        }

        val daysOfWeek = daysOfWeekFromLocale()
        val currentMonth = YearMonth.now()
        binding.exThreeCalendar.apply {
            setup(currentMonth.minusMonths(10), currentMonth.plusMonths(10), daysOfWeek.first())
            scrollToMonth(currentMonth)
        }

        if (savedInstanceState == null) {
            binding.exThreeCalendar.post {
                // Show today's events initially.
                selectDate(today)
            }
        }

        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val binding = CalendarDayBinding.bind(view)

            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        selectDate(day.date)
                    }
                }
            }
        }
        binding.exThreeCalendar.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.binding.exThreeDayText
                val dotView = container.binding.exThreeDotView

                textView.text = day.date.dayOfMonth.toString()

                if (day.owner == DayOwner.THIS_MONTH) {
                    textView.makeVisible()
                    when (day.date) {
                        today -> {
                            textView.setTextColorRes(R.color.white)
                            textView.setBackgroundResource(R.drawable.calendar_today_bg)
                            dotView.makeInVisible()
                        }
                        selectedDate -> {
                            textView.setTextColorRes(R.color.white)
                            textView.setBackgroundResource(R.drawable.calendar_selected_bg)
                            dotView.makeInVisible()
                        }
                        else -> {
                            textView.setTextColorRes(R.color.black)
                            textView.background = null
                            dotView.isVisible = events[day.date].orEmpty().isNotEmpty()
                        }
                    }
                } else {
                    textView.makeInVisible()
                    dotView.makeInVisible()
                }
            }
        }

        binding.exThreeCalendar.monthScrollListener = {
            homeActivityToolbar.title = if (it.year == today.year) {
                titleSameYearFormatter.format(it.yearMonth)
            } else {
                titleFormatter.format(it.yearMonth)
            }

            // Select the first day of the month when
            // we scroll to a new month.
            selectDate(it.yearMonth.atDay(1))
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = CalendarHeaderBinding.bind(view).legendLayout.root
        }
        binding.exThreeCalendar.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                // Setup each header day text if we have not done that already.
                if (container.legendLayout.tag == null) {
                    container.legendLayout.tag = month.yearMonth
                    container.legendLayout.children.map { it as TextView }.forEachIndexed { index, tv ->
                        tv.text = daysOfWeek[index].name.first().toString()
                        tv.setTextColorRes(R.color.white)
                    }
                }
            }
        }

        binding.exThreeAddButton.setOnClickListener {
            dialog.isNewEvent = true
            dialog.eventDate = selectedDate
            dialog.show(childFragmentManager, "EventDialogFragment")
        }
    }

    private fun loadEvent(id: String, name: String, time: String, date: LocalDate) {
        for ((date1, eventList) in events) {
            if(date1 == date) {
                for(event in eventList) {
                    var eventId = event.id.replace(" ", "")
                    var idNoSpace = id.replace(" ", "")
                    if(eventId == idNoSpace) {
                        events[date] = events[date].orEmpty().minus(event)
                    }
                }
            }
        }
        events[date] = events[date].orEmpty().plus(Event(id.replace(" ", ""), name, time, date))
    }

    private fun selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { binding.exThreeCalendar.notifyDateChanged(it) }
            binding.exThreeCalendar.notifyDateChanged(date)
            updateAdapterForDate(date)
        }
    }

    private fun saveEvent(name: String, time: String, date: LocalDate) : String {
        var id = UUID.randomUUID().toString()
        var exists = false

        if(events[date] != null) {
            events[date]!!.forEach {
                if(it.id == id) exists = true
            }
        }
        if(!exists) events[date] = events[date].orEmpty().plus(Event(id.replace(" ", ""), name, time, date))
        return id
    }

    private fun deleteEvent(event: Event) {
        val date = event.date
        events[date] = events[date].orEmpty().minus(event)
        deleteFromDatabase(event.id.replace(" ", ""), date)
    }

    private fun updateAdapterForDate(date: LocalDate) {
        eventsAdapter.apply {
            events.clear()
            events.addAll(this@CalendarFragment.events[date].orEmpty())
            notifyDataSetChanged()
        }
        binding.exThreeSelectedDateText.text = selectionFormatter.format(date)
    }

    override fun onStart() {
        super.onStart()
        homeActivityToolbar.setBackgroundColor(requireContext().getColorCompat(R.color.ihh_pink))
        requireActivity().window.statusBarColor = requireContext().getColorCompat(R.color.ihh_status_pink)
        homeActivityToolbar.setTitleTextColor(getResources().getColor(R.color.white))
    }

    override fun onStop() {
        super.onStop()
        homeActivityToolbar.setBackgroundColor(requireContext().getColorCompat(R.color.ihh_pink))
        homeActivityToolbar.setTitleTextColor(resources.getColor(R.color.white))
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        activity?.finish()
    }

    override fun onDialogPositiveClick(dialog: DialogFragment?) {
        val appointmentName = dialog!!.dialog!!.findViewById<EditText>(R.id.appointmentName)
        val appointmentTime = dialog.dialog!!.findViewById<TextView>(R.id.preview_picked_time_textView)
        val appointmentDate = dialog.dialog!!.findViewById<TextView>(R.id.appointment_date)

        if (appointmentName.text.toString().isEmpty() || appointmentTime.text.toString().isEmpty()) {
            Toast.makeText(context, "Please fill in the appointment name and time", Toast.LENGTH_LONG).show()
        } else {
            if((dialog as EventDialogFragment).isNewEvent) {
                val id = saveEvent(appointmentName.text.toString(), appointmentTime.text.toString(), dialog.eventDate)
                for ((k, v) in events) {
                    println("$k = $v")
                }
                val data = mutableMapOf<String, String>()
                val dataString = ("Appointment Name: " + appointmentName.text.toString() +
                        ";Appointment Time: " + appointmentTime.text.toString() + ";Appointment Date: " + appointmentDate.text.toString())
                data[id] = dataString
                writeToDatabase(data, dialog.eventDate)
            } else {

                val data = ("Appointment Name: " + appointmentName.text.toString() +
                        ";Appointment Time: " + appointmentTime.text.toString() + ";Appointment Date: " + appointmentDate.text.toString())
                updateDatabase(dialog.eventId, data, LocalDate.parse(appointmentDate.text.toString()))
                selectDate(dialog.eventDate)
                events[dialog.oldEventDate] = events[dialog.oldEventDate].orEmpty().minus(dialog.event)
            }
        }
    }

    override fun onDialogNegativeClick(dialog: DialogFragment?) {
    }

    private fun updateDatabase(id: String, data: String, eventDate: LocalDate) {
        val docRef = db.collection("calendar").document(user?.getEmail().toString())
        docRef
            .update(id, data)
            .addOnSuccessListener {
                Log.d("TAG", "DocumentSnapshot successfully updated!")
                selectDate(eventDate)
                updateAdapterForDate(eventDate)
                binding.exThreeCalendar.notifyCalendarChanged()
            }
            .addOnFailureListener { e -> Log.w("TAG", "Error updating document", e) }
    }

    private fun readFromDatabase() {
        val docRef = db.collection("calendar").document(user?.getEmail().toString())

        docRef.addSnapshotListener(EventListener { snapshot, e ->
            if (e != null) {
                Log.w("TAG", "Listen failed.", e)
                return@EventListener
            }
            if (snapshot != null && snapshot.exists()) {
                Log.d("TAG", "Current data: " + snapshot.data)
                var allAppointments = snapshot.data.toString().replace("{", "")
                allAppointments = allAppointments.replace("}", "")
                val splitAppointments = allAppointments.split(",")
                for (s in splitAppointments) {
                    val splitAppointment = s.split("=")
                    if(splitAppointment.size > 1) {
                        val details = splitAppointment[1].split(";").toTypedArray()
                        details[0] = details[0].replace("Appointment Name: ", "")
                        details[1] = details[1].replace("Appointment Time: ", "")
                        details[2] = details[2].replace("Appointment Date: ", "")
                        loadEvent(splitAppointment[0], details[0], details[1],LocalDate.parse(details[2]))
                        updateAdapterForDate(LocalDate.parse(details[2]))
                        binding.exThreeCalendar.notifyCalendarChanged()
                    }
                }
            } else {
                Log.d("TAG", "Current data: null")
            }
        })
    }

    private fun deleteFromDatabase(id: String, eventDate: LocalDate) {
        val docRef = db.collection("calendar").document(user?.getEmail().toString())

        val updates: MutableMap<String, Any> = HashMap()
        updates[id] = FieldValue.delete()

        docRef.update(updates).addOnCompleteListener {
            Toast.makeText(
                context,
                "Appointment deleted",
                Toast.LENGTH_LONG
            ).show()
            selectDate(eventDate)
            updateAdapterForDate(eventDate)
            binding.exThreeCalendar.notifyCalendarChanged()
        }
    }

    private fun writeToDatabase(data: Map<String, String>?, eventDate: LocalDate) {
        db.collection("calendar").document(user?.getEmail().toString())
            .set(data!!, SetOptions.merge())
            .addOnSuccessListener(OnSuccessListener<Void?> {
                Log.d("TAG", "DocumentSnapshot successfully written!")
                Toast.makeText(
                    activity,
                    "Appointment added successfully!",
                    Toast.LENGTH_LONG
                ).show()
                selectDate(eventDate)
                updateAdapterForDate(eventDate)
                binding.exThreeCalendar.notifyCalendarChanged()
            })
            .addOnFailureListener(OnFailureListener { e ->
                Log.w("TAG", "Error writing document", e)
                Toast.makeText(
                    activity,
                    "Sorry, that didn't work. Please try creating the appointment again.",
                    Toast.LENGTH_LONG
                ).show()
            })
    }

}
