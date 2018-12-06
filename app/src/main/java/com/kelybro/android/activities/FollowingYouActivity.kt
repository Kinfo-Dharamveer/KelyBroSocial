package com.kelybro.android.activities
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import com.kelybro.android.R
import com.kelybro.android.fragment.FollowingFragment
import com.kelybro.android.fragment.YouFragment
import kotlinx.android.synthetic.main.fragment_like.*

/**
 * Created by Krishna on 23-01-2018.
 */
class FollowingYouActivity: AppCompatActivity() {
    var CurrentTab:Int = 0
    var ProfileID:String = ""
    private lateinit var mSPF: SharedPreferences
    internal lateinit var mEDT: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_like)
        mSPF = getSharedPreferences("AppData", 0)
        mEDT = mSPF.edit()
        CurrentTab = intent.getIntExtra("CurrentTab",0)
        ProfileID = intent.getStringExtra("ProfileID")
        mEDT.putString("ProfileID", ProfileID)
        mEDT.commit()
        configureTabLayout()
    }
    private fun configureTabLayout() {
        fl_tab_layout.addTab(fl_tab_layout.newTab().setText("FOLLOWING"))
        fl_tab_layout.addTab(fl_tab_layout.newTab().setText("YOU"))


        val adapter = ViewPagerAdapters(supportFragmentManager)
        fl_view_pager.adapter = adapter
        fl_view_pager.addOnPageChangeListener(
                TabLayout.TabLayoutOnPageChangeListener(fl_tab_layout))

        fl_view_pager.currentItem=CurrentTab
        fl_tab_layout.addOnTabSelectedListener(object :
                TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                fl_view_pager.currentItem = tab.position
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
    }
}