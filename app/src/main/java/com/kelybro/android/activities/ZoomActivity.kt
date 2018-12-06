package com.kelybro.android.activities

import android.app.Activity
import android.graphics.drawable.Animatable
import android.net.Uri
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup

import com.kelybro.android.customviews.MultiTouchViewPager
import com.kelybro.android.zoomable.PhotoDraweeView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.imagepipeline.image.ImageInfo
import kotlinx.android.synthetic.main.activity_zoom.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.kelybro.android.R
import java.util.ArrayList

/**
 * Created by Krishna on 11-01-2018.
 */

class ZoomActivity : Activity() {

    internal var id: Int = 0
    internal var courentPosition: Int = 0
    internal lateinit var mDrawables: ArrayList<String>
    private var mAdView: AdView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(applicationContext)
        setContentView(R.layout.activity_zoom)
        id = intent.getIntExtra("ID",0)
        courentPosition = id

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, resources.getString(R.string.banner_ad_unit_id))
        mAdView = findViewById<View>(R.id.ad_view) as AdView

        // Start loading the ad in the background.
        mAdView!!.loadAd(AdRequest.Builder().build())

        mDrawables = ArrayList(intent.getStringArrayListExtra("Array"))


        val viewPager = findViewById<View>(R.id.zoom_view_pager) as MultiTouchViewPager
        za_iv_close.setOnClickListener {
            finish()
        }
        viewPager.adapter = DraweePagerAdapter()
        viewPager.currentItem = courentPosition
        viewPager.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                id = position
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

    }

    inner class DraweePagerAdapter : PagerAdapter() {

        override fun getCount(): Int {
            return mDrawables.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun instantiateItem(viewGroup: ViewGroup, position: Int): Any {
            val photoDraweeView = PhotoDraweeView(viewGroup.context)
            val controller = Fresco.newDraweeControllerBuilder()
            controller.setUri(Uri.parse(mDrawables[position]))
            controller.oldController = photoDraweeView.controller
            controller.controllerListener = object : BaseControllerListener<ImageInfo>() {
                override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, animatable: Animatable?) {
                    super.onFinalImageSet(id, imageInfo, animatable)
                    if (imageInfo == null) {
                        return
                    }
                    photoDraweeView.update(imageInfo.width, imageInfo.height)
                }
            }
            photoDraweeView.controller = controller.build()
            try {
                viewGroup.addView(photoDraweeView, ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return photoDraweeView
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
