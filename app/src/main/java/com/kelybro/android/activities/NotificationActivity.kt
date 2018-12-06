package com.kelybro.android.activities

import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.kelybro.android.R
import com.kelybro.android.adapters.MyNotificationAdapter
import com.kelybro.android.customviews.mUtitily
import com.kelybro.android.model.ModelNotificationDetail
import java.util.ArrayList

class NotificationActivity : AppCompatActivity() {
    lateinit var ma_toolbar: Toolbar
    lateinit var mRV: RecyclerView
    lateinit var mNA: MyNotificationAdapter
    lateinit var mDetails: ArrayList<ModelNotificationDetail>
    lateinit var mDate: ArrayList<String>
    lateinit var mDB: SQLiteDatabase
    lateinit var mSPF: SharedPreferences
    lateinit var mC: Cursor
    lateinit var mSRL: SwipeRefreshLayout
    private lateinit var mUD: mUtitily
    private var mAdView: AdView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nitification)
        ma_toolbar = findViewById<View>(R.id.ma_toolbar) as Toolbar
        setSupportActionBar(ma_toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle("Notification")
        mUD = mUtitily(applicationContext)
        mRV = findViewById(R.id.RecyclerView)
        mDetails = ArrayList()
        mDate = ArrayList()
        mDB = openOrCreateDatabase("AppDB", Context.MODE_PRIVATE, null)
        mSPF = getSharedPreferences("AppData", 0)
        mSRL = findViewById(R.id.SwipeRefresh)
        mSRL.setColorSchemeColors(resources.getColor(R.color.colorPrimaryDark), resources.getColor(R.color.colorAccent))
        mSRL.setOnRefreshListener {
            //mUD.getDataFromURL("notification",getContext(),"LimitStart=0&Mobile_No="+mSPF.getString("MOBILE_NO",""));
            LoadDataFromSQLite()
        }
        mNA = MyNotificationAdapter(applicationContext, mDetails, mDate)
        // Create a grid layout with two columns
        val layoutManager = GridLayoutManager(applicationContext, 1)
        mRV.layoutManager = layoutManager
        mRV.itemAnimator = DefaultItemAnimator()
        mRV.adapter = mNA
        //mUD.getDataFromURL("notification",getContext(),"LimitStart=0&Mobile_No="+mSPF.getString("MOBILE_NO",""));
        LoadDataFromSQLite()


        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, resources.getString(R.string.banner_ad_unit_id))
        mAdView = findViewById<View>(R.id.ad_view) as AdView

        // Start loading the ad in the background.
        mAdView!!.loadAd(AdRequest.Builder().build())
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.itemId
        val title = item.title
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        if (title == "Clear All") {
            mDB.execSQL("DROP TABLE IF EXISTS notification")
            mDB.execSQL(getString(R.string.notification))

            LoadDataFromSQLite()
        }
        return super.onOptionsItemSelected(item)
    }

    fun LoadDataFromSQLite() {
        mDetails.clear()
        mDate.clear()
        mDB.execSQL(getString(R.string.notification))
        mC = mDB.rawQuery("SELECT * FROM notification WHERE client_code='All' Order By notification_date DESC", null)
        if (mC.count > 0) {
            for (i in 0 until mC.count) {
                if (mC.moveToNext()) {
                    if (mC.getString(mC.getColumnIndex("title")) != "Chat NotificationMessageCount") {
                        mDetails.add(ModelNotificationDetail(mC.getString(mC.getColumnIndex("title")), mC.getString(mC.getColumnIndex("details")), mC.getString(mC.getColumnIndex("notification_date")), mC.getString(mC.getColumnIndex("type")), mC.getString(mC.getColumnIndex("client_code"))))
                        mDate.add(mUD.getNotificationDateDifference(mC.getString(mC.getColumnIndex("notification_date"))))
                    }
                }
            }
            mRV.visibility = View.VISIBLE
        } else {
            mRV.visibility = View.INVISIBLE
        }
        mC.close()
        mNA.notifyDataSetChanged()
        mSRL.isRefreshing = false
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.notifications, menu)
        return true

    }
}
