package com.kelybro.android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.MenuItemCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.*
import com.kelybro.android.fragment.*
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import kotlinx.android.synthetic.main.activity_main.*
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.kelybro.android.R
import com.kelybro.android.adapters.BusinessCardAdapter
import com.kelybro.android.eventbus.NotificationMessageCount
import com.testfairy.TestFairy
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONArray
import org.json.JSONObject


class MainActivity : AppCompatActivity() {


    private var ma_toolbar: Toolbar? = null
    private var mInterstitialAd: InterstitialAd? = null
    var notifCount: TextView? = null
    var chatCount: TextView? = null
    var mNotifCount = 0
    var mChatCount = 0
    var mPosition = 0
    lateinit var mDB: SQLiteDatabase
    lateinit var mC: Cursor
    lateinit var mSPF:SharedPreferences
    lateinit var mEDT:SharedPreferences.Editor
    private var mAlertDialog: AlertDialog? = null
    private var adapter: ViewPagerAdapter?=null
    var adapterBusiness: BusinessCardAdapter?=null


    private var sp_job_categories: Spinner? = null
    val JobCategoryList = ArrayList<String>()
    val JobCategoryListID = ArrayList<String>()
    var categoryAdapter: ArrayAdapter<String>?=null
    internal lateinit var mOBJECT: JSONObject
    internal lateinit var mArray: JSONArray
    internal lateinit var queue: RequestQueue
    var mCategoryId: String = "0"
    internal var pDialog: ProgressDialog? = null
    internal var LimitStart = 0
    internal var LoopStart = 1
    internal val BussinessCardList: MutableList<MutableList<String>>? = ArrayList()
    internal val ImageBusinessCard: MutableList<ArrayList<String>>? = ArrayList()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        TestFairy.begin(this, "fc1d4783151af74b1dc0072c4eb957905ea1df7d");


        mSPF = getSharedPreferences("AppData", 0)
        mEDT = mSPF.edit()
        mDB = openOrCreateDatabase("AppDB", Context.MODE_PRIVATE, null)
        ma_toolbar = findViewById<Toolbar>(R.id.ma_toolbar) as Toolbar
        setSupportActionBar(ma_toolbar)


        Fresco.initialize(this)
        configureTabLayout(view_pager)

        mInterstitialAd = InterstitialAd(this)
        // Defined in res/values/strings.xml
        mInterstitialAd!!.setAdUnitId(resources.getString(R.string.interstitial_ad_unit_id))
        mInterstitialAd!!.loadAd(AdRequest.Builder().build())

        if (savedInstanceState == null) {
            val i = intent
            if(i.hasExtra("message")) {
                if (i.getStringExtra("message") == "Chat NotificationMessageCount") {
                    val i = Intent(applicationContext, ChatActivity::class.java)
                    startActivity(i)
                }
            }
            if(i.hasExtra("post_id")){
                if(i.getStringExtra("post_id") == ""){

                }
            }



        }
        RemoveFilter()



    }


    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }


    public override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    private fun configureTabLayout(pager:ViewPager) {

        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.ic_dashboard_black_24dp))
        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.ic_business_center_black_24dp))
        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.ic_move_to_inbox_black_24dp))
        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.ic_whatshot_black_24dp))
        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.ic_person_black_24dp))

        adapter = ViewPagerAdapter(supportFragmentManager)
        pager.adapter = adapter

        pager.addOnPageChangeListener(
                TabLayout.TabLayoutOnPageChangeListener(tab_layout))
        tab_layout.addOnTabSelectedListener(object :
                TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                pager.currentItem = tab.position
                mPosition = tab.position
                invalidateOptionsMenu()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }

        })
    }

    internal inner class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {

        override fun getItem(position: Int): Fragment? {
            var fragment: Fragment? = null

            when (position) {
                0 -> fragment = HomeFragment()
                1 -> fragment = BusinessCardFragment()
                2 -> fragment = JobPostFragment()
                3 -> fragment = BuySellFragment()
                4 -> fragment = ProfileFragment()
            }
            return fragment
        }

        override fun getCount(): Int {
            return 5
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(mPosition==0 || mPosition==4)
            menuInflater.inflate(R.menu.menu_items, menu)
        else
            menuInflater.inflate(R.menu.menu_filter_item,menu)

        LoadDataFromSQLite()
        val searchItem = menu?.findItem(R.id.action_search)
        val relativeLayoutSearch = MenuItemCompat.getActionView(searchItem) as RelativeLayout
        relativeLayoutSearch.setOnClickListener {
            val i = Intent(applicationContext, SearchActivity::class.java)
            startActivity(i)
        }
        val chatItem = menu?.findItem(R.id.action_chat)
        val relativeLayoutChat = MenuItemCompat.getActionView(chatItem) as RelativeLayout
        relativeLayoutChat.setOnClickListener {
            val i = Intent(applicationContext, ChatActivity::class.java)
            startActivity(i)
        }

        if(mChatCount>0) {
            //val relativeLayout = menu?.findItem(R.id.action_notification)?.actionView
            chatCount = relativeLayoutChat.findViewById<TextView>(R.id.hotlist_hot) as TextView
            chatCount!!.text = ""+mChatCount
        }else{
            //val relativeLayout = menu?.findItem(R.id.action_notification)?.actionView
            chatCount = relativeLayoutChat.findViewById<TextView>(R.id.hotlist_hot) as TextView
            chatCount!!.visibility = View.GONE
        }

        val notificationItem = menu?.findItem(R.id.action_notification)
        val relativeLayoutNotification = MenuItemCompat.getActionView(notificationItem) as RelativeLayout
        relativeLayoutNotification.setOnClickListener {
            val i = Intent(applicationContext, NotificationActivity::class.java)
            startActivity(i)
        }

        if(mNotifCount>0) {
            //val relativeLayout = menu?.findItem(R.id.action_notification)?.actionView
            notifCount = relativeLayoutNotification.findViewById<TextView>(R.id.hotlist_hot) as TextView
            notifCount!!.text = ""+mNotifCount
        }else{
            //val relativeLayout = menu?.findItem(R.id.action_notification)?.actionView
            notifCount = relativeLayoutNotification.findViewById<TextView>(R.id.hotlist_hot) as TextView
            notifCount!!.visibility = View.GONE
        }

        if(mPosition!=0 && mPosition!=4) {
            val filterItem = menu?.findItem(R.id.action_filter)
            val relativeLayoutFilter = MenuItemCompat.getActionView(filterItem) as RelativeLayout
            relativeLayoutFilter.setOnClickListener {
                val dialog = AlertDialog.Builder(this)
                dialog.setCancelable(true)

                if (mPosition == 1) {
                    val li = LayoutInflater.from(this)
                    val view = li.inflate(R.layout.dialog_filter_business_card,null)
                    dialog.setView(view)
                  //  val et_business_name = view.findViewById<EditText>(R.id.bc_et_business_name) as EditText
//                    val et_business_category = view.findViewById<EditText>(R.id.bc_et_business_category) as EditText
                    val bc_et_business_state = view.findViewById(R.id.bc_et_business_state) as EditText
                   // et_business_name.setText(mSPF.getString("filter_business_name",""))
                   // et_business_category.setText(mSPF.getString("filter_business_category",""))
                   // bc_et_business_state.setText(mSPF.getString("filter_business_city",""))
                    val tv_apply_filter = view.findViewById<EditText>(R.id.bc_tv_submit) as TextView
                    tv_apply_filter.setOnClickListener {
                        mEDT.putString("filter_business_category",mCategoryId)
                        mEDT.putString("filter_state",bc_et_business_state.text.toString())
                        mEDT.commit()
                        mAlertDialog?.dismiss()
                        view_pager.adapter = adapter

                        val intent = Intent("businessCard")
                        intent.putExtra("filter_business_category", mCategoryId)
                        intent.putExtra("filter_state", bc_et_business_state.text.toString())
                        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                        view_pager.setCurrentItem(mPosition)

                    }
                    // Creating adapter for spinner
                    categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, JobCategoryList)
                    // Drop down layout style - list view with radio button
                    categoryAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    sp_job_categories = view.findViewById(R.id.sp_bs_categories) as Spinner
                    sp_job_categories!!.setAdapter(categoryAdapter)
                    sp_job_categories!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                            if(JobCategoryListID[position] == "0")
                                mCategoryId=""
                            else
                                mCategoryId=JobCategoryListID[position]
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            // Another interface callback
                        }
                    }
                    BusinessCategoryVolleyRequest()


                }

                else if (mPosition == 2) {
                    val li = LayoutInflater.from(this)
                    val view = li.inflate(R.layout.dialog_filter_job,null)
                    dialog.setView(view)
                    val et_business_name = view.findViewById<EditText>(R.id.bc_et_business_name) as EditText
                    val et_business_category = view.findViewById<EditText>(R.id.bc_et_business_category) as EditText
                    et_business_name.setText(mSPF.getString("filter_job_name",""))
                    et_business_category.setText(mSPF.getString("filter_job_city",""))
                    val tv_apply_filter = view.findViewById<EditText>(R.id.btnSubmitFilterJob) as TextView
                    tv_apply_filter.setOnClickListener {
                        mEDT.putString("filter_job_name",et_business_name.text.toString())
                        mEDT.putString("filter_job_city",et_business_category.text.toString())
                        mEDT.commit()
                        mAlertDialog?.dismiss()
                        view_pager.adapter = adapter

                       // EventBus.getDefault().post(JobPostFilter(et_business_name.text.toString()))


                        val intent = Intent("jobPost")
                        intent.putExtra("filter_job_name", et_business_name.text.toString())
                        intent.putExtra("filter_job_city", et_business_category.text.toString())
                        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                        view_pager.setCurrentItem(mPosition)

                    }

                }

                else if (mPosition == 3) {
                    val li = LayoutInflater.from(this)
                    val view = li.inflate(R.layout.dialog_filter_buy_sell,null)
                    dialog.setView(view)
                    val et_business_name = view.findViewById<EditText>(R.id.bc_et_business_name) as EditText
//                    val et_business_category = view.findViewById<EditText>(R.id.bc_et_business_category) as EditText
                    val et_business_city = view.findViewById<EditText>(R.id.bc_et_business_city) as EditText
                    et_business_name.setText(mSPF.getString("filter_product_name",""))
//                    et_business_category.setText(mSPF.getString("filter_product_category",""))
                    et_business_city.setText(mSPF.getString("filter_product_city",""))
                    val tv_apply_filter = view.findViewById<EditText>(R.id.btnFilterBuySell) as TextView
                    tv_apply_filter.setOnClickListener {
                        mEDT.putString("filter_product_name",et_business_name.text.toString())
                        mEDT.putString("filter_product_category",mCategoryId)
                        mEDT.putString("filter_product_city",et_business_city.text.toString())
                        mEDT.commit()
                        mAlertDialog?.dismiss()
                        view_pager.adapter = adapter


                        val intent = Intent("buyerSeller")
                        intent.putExtra("filter_product_name", et_business_name.text.toString())
                        intent.putExtra("filter_product_category", mCategoryId)
                        intent.putExtra("filter_product_city", et_business_city.text.toString())
                        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                        view_pager.setCurrentItem(mPosition)

                    }
                    // Creating adapter for spinner
                    categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, JobCategoryList)
                    // Drop down layout style - list view with radio button
                    categoryAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    sp_job_categories = view.findViewById(R.id.sp_bs_categories) as Spinner
                    sp_job_categories!!.setAdapter(categoryAdapter)
                    sp_job_categories!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                            if(JobCategoryListID[position] == "0")
                                mCategoryId=""
                            else
                                mCategoryId=JobCategoryListID[position]
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            // Another interface callback
                        }
                    }
                    BusinessCategoryVolleyRequest()
                }


                mAlertDialog = dialog.show()

            }
        }

        return true

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: NotificationMessageCount) {
        if(event.count==0){

            val notiCount = notifCount!!.getText()

            val ca = Integer.parseInt(notiCount.toString())

            notifCount!!.text = ""+ca?.inc()

        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val Title = item!!.getTitle().toString()
        if (Title == "Search") {
            val i = Intent(applicationContext, SearchActivity::class.java)
            startActivity(i)
            return true
        }
        if (Title == "Chat") {
            val i = Intent(applicationContext, ChatActivity::class.java)
            startActivity(i)
            return true
        }
        if (Title == "Notification") {
            val i = Intent(applicationContext, NotificationActivity::class.java)
            startActivity(i)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        mInterstitialAd!!.show()
        super.onBackPressed()
    }

    fun LoadDataFromSQLite() {
        mChatCount=0
        mNotifCount=0
        mDB.execSQL(getString(R.string.notification))
        mC = mDB.rawQuery("SELECT * FROM notification WHERE client_code='All' Order By notification_date DESC", null)
        if (mC.count > 0) {
            for (i in 0 until mC.count) {
                if (mC.moveToNext()) {
                    if(mC.getString(mC.getColumnIndex("title")).equals("Chat NotificationMessageCount")){
                        mChatCount ++
                    }else{
                        mNotifCount ++
                    }
                }
            }

        }
        mC.close()
    }

    fun RemoveFilter(){
        mEDT.putString("filter_business_name","")
        mEDT.putString("filter_business_category","")
        mEDT.putString("filter_state","")
        mEDT.putString("filter_job_name","")
        mEDT.putString("filter_job_city","")
        mEDT.putString("filter_product_name","")
        mEDT.putString("filter_product_category","")
        mEDT.putString("filter_product_city","")
        mEDT.commit()
    }

    private fun BusinessCategoryVolleyRequest() {
        var mURL = ""
        if(mPosition==1)
            mURL = getString(R.string.server_url) + "BusinessCard/get_bussiness_cats"
        else
            mURL = getString(R.string.server_url) + "Product/get_product_categories"


        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
            Log.e("JobCategoryResponse", s)
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("success")) {
                    if(mOBJECT.getJSONArray("data")!=null) {
                        mArray = mOBJECT.getJSONArray("data")
                        JobCategoryList.clear()
                        JobCategoryList.add("Select Category")
                        JobCategoryListID.add("0")
                        for (i in 0 until mArray.length()) {
                            val mData = mArray.getJSONObject(i)
                            JobCategoryList.add(mData.getString("name"))
                            JobCategoryListID.add(mData.getString("id"))
                        }
                        //dataAdapter!!.notifyDataSetInvalidated()
                        sp_job_categories!!.setAdapter(categoryAdapter)
                        categoryAdapter
                    }
                } else {
                    Toast.makeText(this, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Errors", e.toString())
            }

        }, Response.ErrorListener { error -> Toast.makeText(this, error.message, Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()
                return map
            }
        }
        queue = Volley.newRequestQueue(this)
        queue.add(req)


    }

}


