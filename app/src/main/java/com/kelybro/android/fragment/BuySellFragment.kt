package com.kelybro.android.fragment

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.kelybro.android.R
import kotlinx.android.synthetic.main.fragment_like.view.*

/**
 * Created by Krishna on 23-01-2018.
 */
class BuySellFragment : Fragment() {

    private var mAdView: AdView? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view: View = inflater!!.inflate(R.layout.fragment_buy_sell, container, false)

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(context, resources.getString(R.string.banner_ad_unit_id))
        mAdView = view.findViewById<View>(R.id.ad_view) as AdView

        // Start loading the ad in the background.
        mAdView!!.loadAd(AdRequest.Builder().build())

        configureTabLayout(view)
        return view
    }

    private fun configureTabLayout(view: View) {
        view.fl_tab_layout.addTab( view.fl_tab_layout.newTab().setText("SELLER"))
        view.fl_tab_layout.addTab( view.fl_tab_layout.newTab().setText("BUYER"))


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
                0 -> fragment = SellerFragment()
                1 -> fragment = BuyerFragment()

            }
            return fragment
        }

        override fun getCount(): Int {
            return 2
        }
    }
    /** Called when leaving the activity  */
    public override fun onPause() {
        if (mAdView != null) {
            mAdView!!.pause()
        }
        super.onPause()
    }

    /** Called when returning to the activity  */
    public override fun onResume() {
        super.onResume()
        if (mAdView != null) {
            mAdView!!.resume()
        }
    }

    /** Called before the activity is destroyed  */
    public override fun onDestroy() {
        if (mAdView != null) {
            mAdView!!.destroy()
        }
        super.onDestroy()
    }
}