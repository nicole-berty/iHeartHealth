package ie.ul.ihearthealth.ui.calendar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ie.ul.ihearthealth.R
import ie.ul.ihearthealth.databinding.ActivityCalendarBinding


class CalendarActivity : AppCompatActivity() {

    internal lateinit var binding: ActivityCalendarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.homeToolbar)

        supportFragmentManager.beginTransaction()
            .add(R.id.homeContainer, CalendarFragment(), CalendarFragment().javaClass.simpleName)
            .addToBackStack(CalendarFragment().javaClass.simpleName)
            .commit()
    }
}
