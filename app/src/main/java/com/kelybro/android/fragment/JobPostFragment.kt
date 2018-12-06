package com.kelybro.android.fragment

import android.app.ProgressDialog
import android.content.*
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley.newRequestQueue
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.kelybro.android.adapters.JobPostAdapter
import com.kelybro.android.R
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Created by Krishna on 23-01-2018.
 */

class JobPostFragment : Fragment() {
    private var recyclerView: RecyclerView? = null
    internal lateinit var mOBJECT: JSONObject
    internal lateinit var sOBJECT: JSONObject
    internal lateinit var mArray: JSONArray
    internal lateinit var queue: RequestQueue
    private var jobPostDialog: AlertDialog? = null
    private var myFile: File? = null
    internal val JobPostList: MutableList<MutableList<String>>? = ArrayList()
    internal val ImageJobPost: MutableList<ArrayList<String>>? = ArrayList()
    private var addJobButton: FloatingActionButton? = null
    private var edt_company_name: EditText? = null
    private var sp_bs_categories: Spinner? = null
    private var edt_job_expirience: EditText? = null
    private var edt_job_name: EditText? = null
    private var edt_job_description: EditText? = null
    private var edt_job_salary: EditText? = null
    private var edt_job_location: EditText? = null
    private var btn_job_submit: Button? = null
    val PostcategoryList = ArrayList<String>()
    val PostcategoryListID = ArrayList<String>()
    private lateinit var mSPF: SharedPreferences
    internal lateinit var mEDT: SharedPreferences.Editor
    internal var LimitStart = 0
    internal var maxScroll = 0
    internal var pDialog: ProgressDialog? = null
    var adapter: JobPostAdapter? = null
    var dataAdapter: ArrayAdapter<String>? = null
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    var mJobCategoryId: String = "0"
    private var mAdView: AdView? = null
    val BusinessCategoryList = ArrayList<String>()


    private var bc_et_business_name: EditText? = null
    private var bc_et_business_Address: EditText? = null
    //    private var bc_et_business_city: EditText? = null
    private var ebc_et_ZipCode: EditText? = null
    //    private var bc_et_business_state: EditText? = null
//    private var bc_et_business_country: EditText? = null
    private var bc_et_business_phone: EditText? = null
    private var bc_et_business_website: EditText? = null
    private var bc_et_business_email: EditText? = null
    private var bc_et_business_description: EditText? = null
    private var bc_tv_submit: TextView? = null
    var mBusinessCaterodyID: String = ""
    val BusinessCategoryListID = ArrayList<String>()

    private var bc_spinner_city: Spinner? = null
    val CityList = ArrayList<String>()
    val CityListID = ArrayList<String>()
    var dataAdaptercity: ArrayAdapter<String>? = null

    private var bc_spinner_state: Spinner? = null
    val StateList = ArrayList<String>()
    val StateListID = ArrayList<String>()
    var dataAdapterState: ArrayAdapter<String>? = null

    private var bc_spinner_country: Spinner? = null
    val CoutnryList = ArrayList<String>()
    val CoutnryListID = ArrayList<String>()
    var dataAdapterCountry: ArrayAdapter<String>? = null
    private var BusinessCardPostDialog: AlertDialog? = null


    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            if (isResumed && LimitStart == 0) {
                MakeVolleyRequest()

            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view: View = inflater!!.inflate(R.layout.fragment_jobpost, container, false)

        mSPF = context.getSharedPreferences("AppData", 0)
        mEDT = mSPF.edit()


        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(context, resources.getString(R.string.banner_ad_unit_id))
        mAdView = view.findViewById<View>(R.id.ad_view) as AdView

        // Start loading the ad in the background.
        mAdView!!.loadAd(AdRequest.Builder().build())

        swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout) as SwipeRefreshLayout
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark)
        recyclerView = view?.findViewById<RecyclerView>(R.id.jobpost_recyclerView) as RecyclerView
        recyclerView!!.layoutManager = LinearLayoutManager(this!!.activity!!, LinearLayout.VERTICAL, false) as RecyclerView.LayoutManager?
        adapter = JobPostAdapter(JobPostList, context)
        recyclerView!!.adapter = adapter

    /*    recyclerView!!.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            internal var scrollDy = 0
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                maxScroll = recyclerView!!.computeVerticalScrollRange()
                scrollDy += dy
                val currentScroll = recyclerView!!.computeVerticalScrollOffset() + recyclerView.computeVerticalScrollExtent()
                if (currentScroll >= maxScroll && maxScroll != 0)
                    MakeVolleyRequest()
            }

        })*/

        LocalBroadcastManager.getInstance(context).registerReceiver(
                mMessageReceiverJobPost, IntentFilter("jobPost"))

        if (userVisibleHint && LimitStart == 0) {
            MakeVolleyRequest()
        }
        swipeRefreshLayout.setOnRefreshListener {
            JobPostList!!.clear()
            LimitStart = 0
            MakeVolleyRequest()
            swipeRefreshLayout.isRefreshing = false

        }

        addJobButton = view.findViewById<FloatingActionButton>(R.id.add_job_button) as FloatingActionButton

        addJobButton!!.setOnClickListener(View.OnClickListener {


            val builder = AlertDialog.Builder(context)
            builder.setTitle("Register Business")
            builder.setMessage("You must register your business first")
            builder.setPositiveButton("YES") { dialog, which ->

                val view: View = inflater!!.inflate(R.layout.add_business_card_post_layout, container,
                        false)

                bc_et_business_name = view.findViewById<EditText>(R.id.bc_et_business_name) as EditText
                bc_et_business_Address = view.findViewById<EditText>(R.id.bc_et_business_Address) as EditText
                ebc_et_ZipCode = view.findViewById<EditText>(R.id.ebc_et_ZipCode) as EditText
                bc_et_business_phone = view.findViewById<EditText>(R.id.bc_et_business_phone) as EditText
                bc_et_business_website = view.findViewById<EditText>(R.id.bc_et_business_website) as EditText
                bc_et_business_email = view.findViewById<EditText>(R.id.bc_et_business_email) as EditText
                bc_et_business_description = view.findViewById<EditText>(R.id.bc_et_business_description) as EditText
                bc_tv_submit = view.findViewById<TextView>(R.id.bc_tv_submit) as TextView


                dataAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, BusinessCategoryList)
                dataAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                sp_bs_categories = view.findViewById<Spinner>(R.id.sp_bs_categories) as Spinner
                sp_bs_categories!!.setAdapter(dataAdapter)
                BusinessCategoryVolleyRequest()
                sp_bs_categories!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                        mBusinessCaterodyID = BusinessCategoryListID[position]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {

                    }
                }

                dataAdaptercity = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, CityList)
                dataAdaptercity!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                bc_spinner_city = view.findViewById<Spinner>(R.id.bc_spinner_city) as Spinner
                bc_spinner_city!!.setAdapter(dataAdaptercity)
                CSCVolleyRequest("cities")

                dataAdapterState = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, StateList)
                dataAdapterState!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                bc_spinner_state = view.findViewById<Spinner>(R.id.bc_spinner_state) as Spinner
                bc_spinner_state!!.setAdapter(dataAdapterState)
                CSCVolleyRequest("states")

                dataAdapterCountry = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, CoutnryList)
                dataAdapterCountry!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                bc_spinner_country = view.findViewById<Spinner>(R.id.bc_spinner_country) as Spinner
                bc_spinner_country!!.setAdapter(dataAdapterCountry)
                CSCVolleyRequest("countries")

                bc_tv_submit!!.setOnClickListener {
                    //BusinessRegisterMakeVolleyRequest()

                    val mURL = getString(R.string.server_url) + "BusinessCard/registerBusiness"
                    val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
                        Log.e("BusinessResponse", s)
                        try {
                            mOBJECT = JSONObject(s)
                            if (mOBJECT.getBoolean("success")) {
                                sOBJECT = mOBJECT.getJSONObject("data")

                                mEDT.putString("business_id", sOBJECT.getString("business_id"))
                                mEDT.commit()

                                Toast.makeText(context, mOBJECT.getString("message"), Toast.LENGTH_LONG).show()
                                BusinessCardPostDialog!!.dismiss()

                                val builder = AlertDialog.Builder(context)
                                builder.setTitle("Job Post")
                                builder.setMessage("Now you can post your Job")
                                builder.setPositiveButton("YES") { dialog, which ->

                                    val view: View = inflater!!.inflate(R.layout.add_job_layout, container, false)
                                    val dialog = AlertDialog.Builder(context)
                                    dialog.setView(view)
                                    jobPostDialog = dialog.show()
                                    // Creating adapter for spinner
                                    dataAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, PostcategoryList)
                                    // Drop down layout style - list view with radio button
                                    dataAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    PostJobCategoriesMakeVolleyRequest()
                                    edt_job_name = view!!.findViewById<EditText>(R.id.edt_job_name) as EditText
                                    edt_job_description = view!!.findViewById<EditText>(R.id.edt_job_description) as EditText
                                    sp_bs_categories = view!!.findViewById<Spinner>(R.id.sp_bs_categories) as Spinner
                                    edt_job_expirience = view!!.findViewById<EditText>(R.id.edt_job_expirience) as EditText
                                    edt_job_salary = view!!.findViewById<EditText>(R.id.edt_job_salary) as EditText
                                    edt_job_location = view!!.findViewById<EditText>(R.id.edt_job_location) as EditText
                                    btn_job_submit = view!!.findViewById<Button>(R.id.btn_job_submit) as Button
                                    // attaching data adapter to spinner
                                    sp_bs_categories!!.setAdapter(dataAdapter)
                                    sp_bs_categories!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                                            mJobCategoryId = PostcategoryListID[position]
                                        }

                                        override fun onNothingSelected(parent: AdapterView<*>) {
                                            // Another interface callback
                                        }
                                    }
                                    btn_job_submit!!.setOnClickListener {
                                        sendJobPost()
                                    }


                                }


                                val dialog: AlertDialog = builder.create()
                                dialog.show()
                                //Hawk.put("success",mOBJECT.getBoolean("success"))
                            } else {
                                Toast.makeText(context, mOBJECT.getString("message"), Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Log.e("Errors", e.toString())
                        }

                    }, Response.ErrorListener { error -> Toast.makeText(context, error.message, Toast.LENGTH_LONG).show() }) {
                        override fun getParams(): Map<String, String> {
                            val map = HashMap<String, String>()

                            map["name"] = bc_et_business_name!!.text.toString()
                            map["bus_type"] = mBusinessCaterodyID
                            map["bus_address"] = bc_et_business_Address!!.text.toString()
                            map["city"] = bc_spinner_city!!.selectedItem.toString()
                            map["state"] = bc_spinner_state!!.selectedItem.toString()
                            map["country"] = bc_spinner_country!!.selectedItem.toString()
                            map["contact"] = bc_et_business_phone!!.text.toString()
                            map["website"] = bc_et_business_website!!.text.toString()
                            map["email"] = bc_et_business_email!!.text.toString()
                            map["postcode"] = ebc_et_ZipCode!!.text.toString()
                            map["description"] = bc_et_business_description!!.text.toString()
                            return map
                        }

                    }
                    queue = newRequestQueue(activity)
                    queue.add(req)
                    req.retryPolicy = DefaultRetryPolicy(10000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)


                }

                val dialog = AlertDialog.Builder(context)
                dialog.setView(view)
                BusinessCardPostDialog = dialog.show()
                /* }
                 else
                 {
                     val intent = Intent(context, YourBusinessCardActivity::class.java)
                     startActivityForResult(intent,999)
                 }*/

            }

            builder.setNegativeButton("NO") { dialog, which ->
                dialog.dismiss()
            }

            val dialog: AlertDialog = builder.create()
            dialog.show()

        })

        return view
    }


    /*   @Subscribe(threadMode = ThreadMode.MAIN)
       fun onEvent(event: JobPostFilter) {

           val filter_job_name = event.filter_job_name

           val mURL = getString(R.string.server_url) + "Job/get_job"
           showpDialog()
           val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
               Log.e("JobResponse", s)
               try {
                   mOBJECT = JSONObject(s)
                   if (mOBJECT.getBoolean("success")) {
                       mArray = mOBJECT.getJSONArray("data")
                       if (mArray.length() > 0) {

                           LimitStart += mArray.length()

                           JobPostList!!.clear()

                           for (i in 0 until mArray.length()) {

                               val mData = mArray.getJSONObject(i)
                               val mylists = java.util.ArrayList<String>()
                               mylists.add(mData.getString("id"))
                               mylists.add(mData.getString("user_id"))
                               mylists.add(mData.getString("business_id"))
                               mylists.add(mData.getString("designation"))
                               mylists.add(mData.getString("job_category"))
                               mylists.add(mData.getString("description"))
                               mylists.add(mData.getString("location"))
                               mylists.add(mData.getString("business_cat_id"))
                               mylists.add(mData.getString("experience"))
                               mylists.add(mData.getString("salary"))
                               mylists.add(mData.getString("admin_verified"))
                               mylists.add(mData.getString("created_date"))
                               mylists.add(mData.getString("business_category"))
                               mylists.add(mData.getString("user_name"))
                               mylists.add(mData.getString("user_profile_pic_thumb"))
                               mylists.add(mData.getString("user_profile_pic"))
                               //mylists.add(mData.getString("post_my_like"))
                               mylists.add("0")
                               mylists.add(mData.getString("post_my_apply"))
                               mylists.add(mData.getString("post_share_link"))
                               mylists.add(mData.getString("is_vip"))
                               mylists.add(mData.getString("business_name"))
                               JobPostList!!.add(mylists)
                           }
                       }

                       hidepDialog()
                       adapter = JobPostAdapter(JobPostList, context)
                       recyclerView!!.adapter = adapter

                   } else {
                       hidepDialog()
                       // JobPostList!!.clear()
                       recyclerView!!.visibility = View.GONE
                       Toast.makeText(context, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                   }
               } catch (e: Exception) {
                   Log.e("Errors", e.toString())
               }

           }, Response.ErrorListener { error ->
               Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
               hidepDialog()
           }) {
               override fun getParams(): Map<String, String> {
                   val map = HashMap<String, String>()
                   map["user_id"] = mSPF.getString("id", "")
                   map["post_type"] = "2"
                   map["total"] = "10"
                   map["start"] = LimitStart.toString()
                   map["filter_name"] = filter_job_name
                   map["filter_city"] = mSPF.getString("filter_job_city", "")
                   return map
               }
           }
           queue = newRequestQueue(activity)
           queue.add(req)
           req.retryPolicy = DefaultRetryPolicy(10000,
                   DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                   DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

       }
   */


    private val mMessageReceiverJobPost = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            // Get extra data included in the Intent

            val filter_job_name = intent.getStringExtra("filter_job_name")
            val filter_job_city = intent.getStringExtra("filter_job_city")


            val mURL = getString(R.string.server_url) + "Job/get_job"
            showpDialog()
            val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
                Log.e("JobResponse", s)
                try {
                    mOBJECT = JSONObject(s)
                    if (mOBJECT.getBoolean("success")) {
                        mArray = mOBJECT.getJSONArray("data")
                        if (mArray.length() > 0) {

                            LimitStart += mArray.length()


                            for (i in 0 until mArray.length()) {

                                val mData = mArray.getJSONObject(i)
                                val mylists = java.util.ArrayList<String>()
                                mylists.add(mData.getString("id"))
                                mylists.add(mData.getString("user_id"))
                                mylists.add(mData.getString("business_id"))
                                mylists.add(mData.getString("designation"))
                                mylists.add(mData.getString("job_category"))
                                mylists.add(mData.getString("description"))
                                mylists.add(mData.getString("location"))
                                mylists.add(mData.getString("business_cat_id"))
                                mylists.add(mData.getString("experience"))
                                mylists.add(mData.getString("salary"))
                                mylists.add(mData.getString("admin_verified"))
                                mylists.add(mData.getString("created_date"))
                                mylists.add(mData.getString("business_category"))
                                mylists.add(mData.getString("user_name"))
                                mylists.add(mData.getString("user_profile_pic_thumb"))
                                mylists.add(mData.getString("user_profile_pic"))
                                //mylists.add(mData.getString("post_my_like"))
                                mylists.add("0")
                                mylists.add(mData.getString("post_my_apply"))
                                mylists.add(mData.getString("post_share_link"))
                                mylists.add(mData.getString("is_vip"))
                                mylists.add(mData.getString("business_name"))
                                JobPostList!!.add(mylists)
                            }
                        }

                        hidepDialog()
                        adapter = JobPostAdapter(JobPostList, context)
                        recyclerView!!.adapter = adapter

                    } else {
                        hidepDialog()
                        //recyclerView!!.visibility = View.GONE
                        Toast.makeText(context, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("Errors", e.toString())
                }

            }, Response.ErrorListener { error ->
                Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
                hidepDialog()
            }) {
                override fun getParams(): Map<String, String> {
                    val map = HashMap<String, String>()
                    map["user_id"] = mSPF.getString("id", "")
                    map["post_type"] = "2"
                    map["total"] = "10"
                    map["start"] = LimitStart.toString()
                    map["filter_name"] = filter_job_name
                    map["filter_city"] = filter_job_city
                    return map

                }
            }
            queue = newRequestQueue(activity)
            queue.add(req)
            req.retryPolicy = DefaultRetryPolicy(10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        }


    }


    private fun BusinessCategoryVolleyRequest() {
        val mURL = getString(R.string.server_url) + "BusinessCard/get_bussiness_cats"
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
            Log.e("JobCategoryResponse", s)
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("success")) {
                    if (mOBJECT.getJSONArray("data") != null) {
                        mArray = mOBJECT.getJSONArray("data")
                        BusinessCategoryList!!.clear()
                        for (i in 0 until mArray.length()) {
                            val mData = mArray.getJSONObject(i)
                            BusinessCategoryList!!.add(mData.getString("name"))
                            BusinessCategoryListID!!.add(mData.getString("id"))
                        }
                        //dataAdapter!!.notifyDataSetInvalidated()
                        sp_bs_categories!!.setAdapter(dataAdapter)
                    }
                } else {
                    Toast.makeText(context, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Errors", e.toString())
            }

        }, Response.ErrorListener { error -> Toast.makeText(context, error.message, Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()
                return map
            }
        }
        queue = newRequestQueue(activity)
        queue.add(req)
        req.retryPolicy = DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
    }

    private fun CSCVolleyRequest(api: String) {
        val mURL = getString(R.string.server_url) + "comman/" + api
        val req = object : StringRequest(Request.Method.GET, mURL, Response.Listener<String> { s ->
            Log.e("JobCategoryResponse", s)
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("success")) {
                    if (mOBJECT.getJSONArray("data") != null) {
                        mArray = mOBJECT.getJSONArray("data")
                        if (api.equals("cities")) {
                            CityList.clear()
                            CityListID.clear()
                        } else if (api.equals("states")) {
                            StateList.clear()
                            StateListID.clear()
                        } else if (api.equals("countries")) {
                            CoutnryList.clear()
                            CoutnryListID.clear()
                        }
                        for (i in 0 until mArray.length()) {
                            val mData = mArray.getJSONObject(i)
                            if (api.equals("cities")) {
                                CityList!!.add(mData.getString("cityname"))
                                CityListID!!.add(mData.getString("cityid"))
                                bc_spinner_city!!.setAdapter(dataAdaptercity)
                                //dataAdaptercity!!.notifyDataSetInvalidated()
                            } else if (api.equals("states")) {
                                StateList!!.add(mData.getString("statename"))
                                StateListID!!.add(mData.getString("stateid"))
                                bc_spinner_state!!.setAdapter(dataAdapterState)
                                //dataAdapterState!!.notifyDataSetInvalidated()
                            } else if (api.equals("countries")) {
                                CoutnryList!!.add(mData.getString("countryname"))
                                CoutnryListID!!.add(mData.getString("countryid"))
                                bc_spinner_country!!.setAdapter(dataAdapterCountry)
                                //dataAdapterCountry!!.notifyDataSetInvalidated()
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Errors", e.toString())
            }

        }, Response.ErrorListener { error -> Toast.makeText(context, error.message, Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()
                return map
            }
        }
        queue = newRequestQueue(activity)
        queue.add(req)
        req.retryPolicy = DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
    }


    private fun sendJobPost() {

        when {
            edt_job_name!!.text.toString() == "" -> {
                Toast.makeText(context, "Please Enter Job For Post", Toast.LENGTH_LONG).show()
                return
            }
            edt_job_description!!.text.toString() == "" -> {
                Toast.makeText(context, "Please Enter Job Description", Toast.LENGTH_LONG).show()
                return
            }
            edt_job_location!!.text.toString() == "" -> {
                Toast.makeText(context, "Please Enter Location", Toast.LENGTH_LONG).show()
                return
            }
            edt_job_expirience!!.text.toString() == "" -> {
                Toast.makeText(context, "Please Enter Experience", Toast.LENGTH_LONG).show()
                return
            }
            edt_job_salary!!.text.toString() == "" -> {
                Toast.makeText(context, "Please Enter Salary", Toast.LENGTH_LONG).show()
                return
            }
            else -> {
                JobPostMakeVolleyRequest()
            }
        }
    }

    private fun JobPostMakeVolleyRequest() {
        val mURL = getString(R.string.server_url) + "Job/add_new_job"
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
            Log.e("AddNewJobResponse", s)
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("success")) {
                    Log.e("jobErrorResponse", s)
                    mOBJECT.getString("new_job_id")

                    sOBJECT = mOBJECT.getJSONObject("data")

//                        mData.has("post_images")
                    val mylists = java.util.ArrayList<String>()
                    mylists.add(sOBJECT.getString("user_id"))
                    mylists.add(sOBJECT.getString("business_id"))
                    mylists.add(sOBJECT.getString("designation"))
                    mylists.add(sOBJECT.getString("job_category"))
                    mylists.add(sOBJECT.getString("description"))
                    mylists.add(sOBJECT.getString("location"))
                    mylists.add(sOBJECT.getString("business_cat_id"))
                    mylists.add(sOBJECT.getString("experience"))
                    mylists.add(sOBJECT.getString("salary"))

//                        JobPostList!!.add(mylists)
                    jobPostDialog!!.dismiss()
                    Toast.makeText(context, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()

//                    val adapter = JobPostAdapter(JobPostList/*,ImageJobPost*/, context)
//                    recyclerView!!.adapter = adapter
                } else {
                    Toast.makeText(context, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Errors", e.toString())
            }

        }, Response.ErrorListener { error -> Toast.makeText(context, error.message, Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()
                map["user_id"] = mSPF.getString("id", "")
                map["business_id"] = mSPF.getString("business_id", "")
                map["business_cat_id"] = mSPF.getString("business_cat_id", "")
                map["designation"] = edt_job_name!!.text.toString()
                map["job_category"] = mJobCategoryId
                map["description"] = edt_job_description!!.text.toString()
                map["location"] = edt_job_location!!.text.toString()
                map["experience"] = edt_job_expirience!!.text.toString()
                map["salary"] = edt_job_salary!!.text.toString()

                return map
            }
        }
        queue = newRequestQueue(activity)
        queue.add(req)
        req.retryPolicy = DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

    }

    private fun MakeVolleyRequest() {

        val mURL = getString(R.string.server_url) + "Job/get_job"
        showpDialog()
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
            Log.e("JobResponse", s)
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("success")) {
                    mArray = mOBJECT.getJSONArray("data")
                    if (mArray.length() > 0) {

                        LimitStart += mArray.length()

                        for (i in 0 until mArray.length()) {

                            val mData = mArray.getJSONObject(i)
                            val mylists = java.util.ArrayList<String>()
                            mylists.add(mData.getString("id"))
                            mylists.add(mData.getString("user_id"))
                            mylists.add(mData.getString("business_id"))
                            mylists.add(mData.getString("designation"))
                            mylists.add(mData.getString("job_category"))
                            mylists.add(mData.getString("description"))
                            mylists.add(mData.getString("location"))
                            mylists.add(mData.getString("business_cat_id"))
                            mylists.add(mData.getString("experience"))
                            mylists.add(mData.getString("salary"))
                            mylists.add(mData.getString("admin_verified"))
                            mylists.add(mData.getString("created_date"))
                            mylists.add(mData.getString("business_category"))
                            mylists.add(mData.getString("user_name"))
                            mylists.add(mData.getString("user_profile_pic_thumb"))
                            mylists.add(mData.getString("user_profile_pic"))
                            //mylists.add(mData.getString("post_my_like"))
                            mylists.add("0")
                            mylists.add(mData.getString("post_my_apply"))
                            mylists.add(mData.getString("post_share_link"))
                            mylists.add(mData.getString("is_vip"))
                            mylists.add(mData.getString("business_name"))
                            JobPostList!!.add(mylists)
                        }
                    }

                    hidepDialog()
                    adapter = JobPostAdapter(JobPostList, context)
                    recyclerView!!.adapter = adapter

                } else {
                    hidepDialog()
                   // recyclerView!!.visibility = View.GONE
                    Toast.makeText(context, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Errors", e.toString())
            }

        }, Response.ErrorListener { error ->
            Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
            hidepDialog()
        }) {
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()
                map["user_id"] = mSPF.getString("id", "")
                map["post_type"] = "2"
                map["total"] = "10"
                map["start"] = LimitStart.toString()
               /* map["filter_name"] = mSPF.getString("filter_job_name", "")
                map["filter_city"] = mSPF.getString("filter_job_city", "")*/
                return map
            }
        }
        queue = newRequestQueue(activity)
        queue.add(req)
        req.retryPolicy = DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

    }

    private fun PostJobCategoriesMakeVolleyRequest() {
        val mURL = getString(R.string.server_url) + "Job/job_category_list"
        val req = object : StringRequest(Request.Method.GET, mURL, Response.Listener<String> { s ->
            Log.e("JobCategoryResponse", s)
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("success")) {
                    if (mOBJECT.getJSONArray("data") != null) {
                        mArray = mOBJECT.getJSONArray("data")
                        PostcategoryList!!.clear()
                        for (i in 0 until mArray.length()) {
                            val mData = mArray.getJSONObject(i)
                            PostcategoryList!!.add(mData.getString("category"))
                            PostcategoryListID!!.add(mData.getString("id"))
                        }
                        //dataAdapter!!.notifyDataSetInvalidated()
                        sp_bs_categories!!.setAdapter(dataAdapter)
                    }
                } else {
                    Toast.makeText(context, mOBJECT.getString("msg"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Errors", e.toString())
            }

        }, Response.ErrorListener { error -> Toast.makeText(context, error.message, Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()
                return map
            }
        }
        queue = newRequestQueue(activity)
        queue.add(req)
        req.retryPolicy = DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
    }

    private fun showpDialog() {
        pDialog = ProgressDialog(context)
        pDialog!!.setMessage("Please wait...")
        if (!pDialog!!.isShowing())
            pDialog!!.show()
    }

    private fun hidepDialog() {
        if (pDialog!!.isShowing())
            pDialog!!.dismiss()
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
