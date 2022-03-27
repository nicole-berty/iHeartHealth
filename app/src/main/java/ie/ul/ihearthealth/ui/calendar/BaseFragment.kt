package ie.ul.ihearthealth.ui.calendar

import android.content.Context
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

    private var mContext: Context? = null

    // Initialise context from onAttach()
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
    override fun onStart() {
        super.onStart()
        if (this is HasToolbar) {
            homeActivityToolbar.makeGone()
            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
            homeActivityToolbar.setTitleTextColor(mContext?.resources!!.getColor(R.color.white))
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
            homeActivityToolbar.setTitleTextColor(resources.getColor(R.color.white))
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
