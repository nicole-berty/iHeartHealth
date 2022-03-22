package ie.ul.ihearthealth.ui.calendar

import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import ie.ul.ihearthealth.R

interface HasToolbar {
    val toolbar: Toolbar? // Return null to hide the toolbar
}

interface HasBackButton

abstract class BaseFragment(@LayoutRes layoutRes: Int) : Fragment(layoutRes) {
    val homeActivityToolbar: Toolbar
        get() = (requireActivity() as CalendarActivity).binding.homeToolbar

    override fun onStart() {
        super.onStart()
        if (this is HasToolbar) {
            homeActivityToolbar.makeGone()
            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
            homeActivityToolbar.setTitleTextColor(R.color.white)
        }

        if (this is HasBackButton) {
            val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
            actionBar?.title = if (titleRes != null) context?.getString(titleRes!!) else ""
            actionBar?.setDisplayHomeAsUpEnabled(false)
        }
    }

    override fun onStop() {
        super.onStop()
        if (this is HasToolbar) {
            homeActivityToolbar.makeVisible()
            homeActivityToolbar.setTitleTextColor(R.color.white)
            (requireActivity() as AppCompatActivity).setSupportActionBar(homeActivityToolbar)
        }

        if (this is HasBackButton) {
            val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
            actionBar?.title = context?.getString(R.string.app_name)
            actionBar?.setDisplayHomeAsUpEnabled(false)
        }
    }

    abstract val titleRes: Int?
}
