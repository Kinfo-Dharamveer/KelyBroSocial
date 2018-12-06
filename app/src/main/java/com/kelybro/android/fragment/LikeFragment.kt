package com.kelybro.android.fragment

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kelybro.android.R
import kotlinx.android.synthetic.main.fragment_like.view.*

/**
 * Created by Krishna on 09-01-2018.
 */
class LikeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view: View = inflater!!.inflate(R.layout.fragment_like, container, false)
        configureTabLayout(view)
        return view
    }

    private fun configureTabLayout(view: View) {
        view.fl_tab_layout.addTab( view.fl_tab_layout.newTab().setText("FOLLOWING"))
        view.fl_tab_layout.addTab( view.fl_tab_layout.newTab().setText("YOU"))


        val adapter = ViewPagerAdapters(childFragmentManager)
        view.fl_view_pager.adapter = adapter

        view.fl_view_pager.addOnPageChangeListener(
                TabLayout.TabLayoutOnPageChangeListener(view.fl_tab_layout))
        view.fl_tab_layout.addOnTabSelectedListener(object :
                TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                view.fl_view_pager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }

        })

    }

    internal inner class ViewPagerAdapters(manager: FragmentManager) : FragmentPagerAdapter(manager) {

        override fun getItem(position: Int): Fragment? {
            var fragment: Fragment? = null
            when (position) {
                0 -> fragment = FollowingFragment()
                1 -> fragment = YouFragment()

            }
            return fragment
        }

        override fun getCount(): Int {
            return 2
        }

//        override fun getPageTitle(position: Int): CharSequence? {
//            var title: String? = null
//            when (position) {
//                0 -> title = "FOLLOWING"
//                1 -> title = "YOU"
//            }
//            return title
//        }
    }
}