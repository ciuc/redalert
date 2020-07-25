package ro.antiprotv.sugar.ui

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import org.koin.core.KoinComponent
import org.koin.core.get
import ro.antiprotv.sugar.R
import java.lang.IllegalArgumentException

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class MainPagerAdapter(private val mContext: Context, fm: FragmentManager) : KoinComponent,
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> get<AlertListFragment>()
            1 -> get<ItemListFragment>()
            // 2 -> TODO: help_fragment_here
            else -> throw IllegalArgumentException("Unsupported item position: $position")
        }
    }

    override fun getPageTitle(position: Int): CharSequence = mContext.resources.getString(TAB_TITLES[position])

    override fun getCount() = TAB_TITLES.size       // total pages.

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(R.string.title_alert_list, R.string.title_items/*, R.string.title_help*/)
    }

}