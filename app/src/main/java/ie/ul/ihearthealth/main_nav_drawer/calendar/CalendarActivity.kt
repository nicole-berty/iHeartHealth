package ie.ul.ihearthealth.main_nav_drawer.calendar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ie.ul.ihearthealth.R
import ie.ul.ihearthealth.databinding.ActivityCalendarBinding

/**
 * An activity for the calendar, where users can create, edit, and delete events
 */
class CalendarActivity : AppCompatActivity() {

    internal lateinit var binding: ActivityCalendarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.homeToolbar)

        val sharedPref = getSharedPreferences("SharedPrefs", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("activity", "calendar")
        editor.apply()

        supportFragmentManager.beginTransaction()
            .add(R.id.homeContainer, CalendarFragment(), CalendarFragment().javaClass.simpleName)
            .addToBackStack(CalendarFragment().javaClass.simpleName)
            .commit()
    }
}
