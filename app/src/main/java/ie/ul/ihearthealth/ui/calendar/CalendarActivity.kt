package ie.ul.ihearthealth.ui.calendar

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ie.ul.ihearthealth.MainActivity
import ie.ul.ihearthealth.R
import ie.ul.ihearthealth.databinding.HomeActivityBinding


class CalendarActivity : AppCompatActivity() {

    internal lateinit var binding: HomeActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.homeToolbar)

        supportFragmentManager.beginTransaction()
            .add(R.id.homeContainer, Example3Fragment(), Example3Fragment().javaClass.simpleName)
            .addToBackStack(Example3Fragment().javaClass.simpleName)
            .commit()
    }
}
