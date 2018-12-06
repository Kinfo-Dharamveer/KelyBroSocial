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
import com.android.volley.toolbox.Volley.newRequestQueue
import com.kelybro.android.adapters.BusinessCardAdapter
import com.kelybro.android.R
import org.json.JSONArray
import org.json.JSONObject
import android.widget.ArrayAdapter
import com.android.volley.toolbox.StringRequest
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.kelybro.android.AppConstants
import com.kelybro.android.customviews.mUtitily
import com.kelybro.android.activities.YourBusinessCardActivity

/**
 * Created by Krishna on 09-01-2018.
 */
class BusinessCardFragment : Fragment() {

    private var mUtility: mUtitily? = null

    var i = 0


    lateinit var recyclerViewBusiness: RecyclerView


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
    private var progressbar: ProgressBar? = null
    internal lateinit var mOBJECT: JSONObject
    internal lateinit var SOBJECT: JSONObject
    internal lateinit var mArray: JSONArray
    internal lateinit var queue: RequestQueue
    private var add_business_card: FloatingActionButton? = null
    private var BusinessCardPostDialog: AlertDialog? = null
    internal val BussinessCardList: MutableList<MutableList<String>>? = ArrayList()
    internal val ImageBusinessCard: MutableList<ArrayList<String>>? = ArrayList()
    private lateinit var mSPF: SharedPreferences
    internal lateinit var mEDT: SharedPreferences.Editor
    internal var LimitStart = 0
    internal var LoopStart = 1
    internal var maxScroll = 0
    internal var pDialog: ProgressDialog? = null
    var adapter: BusinessCardAdapter? = null
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout


    private var sp_bs_categories: Spinner? = null
    val BusinessCategoryList = ArrayList<String>()
    val BusinessCategoryListID = ArrayList<String>()
    var dataAdapter: ArrayAdapter<String>? = null
    var mBusinessCaterodyID: String = ""

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
    private var mAdView: AdView? = null


    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            if (isResumed && LimitStart == 0) {
                MakeVolleyRequest()
            }

        }
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view: View = inflater!!.inflate(R.layout.fragment_businesscard, container, false)

        mUtility = mUtitily(context)

        mSPF = context.getSharedPreferences("AppData", 0)
        mEDT = mSPF.edit()

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(context, resources.getString(R.string.banner_ad_unit_id))
        mAdView = view.findViewById<View>(R.id.ad_view) as AdView

        // Start loading the ad in the background.
        mAdView!!.loadAd(AdRequest.Builder().build())


        swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout) as SwipeRefreshLayout
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark)
        recyclerViewBusiness = view?.findViewById<RecyclerView>(R.id.bc_recyclerView) as RecyclerView
        recyclerViewBusiness!!.layoutManager = LinearLayoutManager(this!!.activity, LinearLayout.VERTICAL, false) as RecyclerView.LayoutManager?
        adapter = BusinessCardAdapter(BussinessCardList, ImageBusinessCard, context)
        recyclerViewBusiness!!.adapter = adapter

        recyclerViewBusiness!!.setOnScrollListener(object : RecyclerView.OnScrollListener() {
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

        })

        LocalBroadcastManager.getInstance(context).registerReceiver(
                mMessageReceiver, IntentFilter("businessCard"))


        if (userVisibleHint && LimitStart == 0) {
            MakeVolleyRequest()
        }


        swipeRefreshLayout.setOnRefreshListener {
            BussinessCardList!!.clear()
            ImageBusinessCard!!.clear()
            LimitStart = 0
            MakeVolleyRequest()

            swipeRefreshLayout.isRefreshing = false

        }

        add_business_card = view.findViewById<FloatingActionButton>(R.id.add_business_card) as FloatingActionButton

        add_business_card!!.setOnClickListener {

            /*    if (mSPF.getString("business_id", "") == "") {


                *//*    val view: View = inflater!!.inflate(R.layout.add_business_card_post_layout, container,
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
                          BusinessRegisterMakeVolleyRequest()
                      }

                      val dialog = AlertDialog.Builder(context)
                      dialog.setView(view)
                      BusinessCardPostDialog = dialog.show()*//*
                  }

                  else {*/


            val intent = Intent(context, YourBusinessCardActivity::class.java)
            startActivityForResult(intent, 999)
            //  }

        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 999) {
            BussinessCardList!!.clear()
            ImageBusinessCard!!.clear()
            LimitStart = 0
            MakeVolleyRequest()
        }
    }

    private val mMessageReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            // Get extra data included in the Intent
            val filter_business_category = intent.getStringExtra("filter_business_category")
            val filter_state = intent.getStringExtra("filter_state")

            BussinessCardList!!.clear()

            val mURL = getString(R.string.server_url) + "BusinessCard/get_businessvistingcard"
            showpDialog()
            val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
                Log.e("BusinessResponse", s)
                try {
                    mOBJECT = JSONObject(s)
                    if (mOBJECT.getBoolean("success")) {
                        mArray = mOBJECT.getJSONArray("data")
                        if (mArray.length() > 0) {
                            LimitStart += mArray.length()
                            LoopStart = 1

                            for (i in 0 until mArray.length()) {

                                val mData = mArray.getJSONObject(i)
                                val mylists = java.util.ArrayList<String>()
                                mylists.add(mData.getString("id"))
                                mylists.add(mData.getString("user_id"))
                                mylists.add(mData.getString("bus_cat"))
                                mylists.add(mData.getString("country"))
                                mylists.add(mData.getString("state"))
                                mylists.add(mData.getString("city"))
                                mylists.add(mData.getString("contact_no"))
                                mylists.add(mData.getString("text"))
                                mylists.add(mData.getString("text"))
                                mylists.add(mData.getString("business_category"))
                                mylists.add(mData.getString("user_name"))
                                mylists.add(com.kelybro.android.AppConstants.imgUrl + mData.getString("user_profile_pic_thumb"))
                                mylists.add(mData.getString("user_profile_pic"))
                                mylists.add(mData.getString("created_date"))


                                BussinessCardList!!.add(mylists)
                                val mpostimage = mData.getJSONObject("post_images")
                                val myImglists = java.util.ArrayList<String>()
                                for (i in 0 until (mpostimage.length() / 2)) {
                                    if (mpostimage.getString("main_img_" + i).length > 0) {
                                        myImglists.add(com.kelybro.android.AppConstants.imgUrl + mpostimage.getString("main_img_" + i))
                                    } else {
                                        myImglists.add("")
                                    }
                                }
                                ImageBusinessCard!!.add(myImglists)
                            }


                        }

                        hidepDialog()
                        adapter = BusinessCardAdapter(BussinessCardList, ImageBusinessCard, context)
                        recyclerViewBusiness!!.adapter = adapter


                    } else {
                        hidepDialog()
                       // recyclerViewBusiness!!.visibility = android.view.View.GONE

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
                    map["post_type"] = "0"
                    map["total"] = "10"
                    map["start"] = LimitStart.toString()
                    map["filter_category"] = filter_business_category
                    map["filter_state"] = filter_state

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

    override fun onDestroyView() {
        super.onDestroyView()
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver)
    }

    private fun MakeVolleyRequest() {

        val mURL = getString(R.string.server_url) + "BusinessCard/get_businessvistingcard"
        showpDialog()
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
            Log.e("BusinessResponse", s)
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("success")) {
                    mArray = mOBJECT.getJSONArray("data")
                    if (mArray.length() > 0) {
                        LimitStart += mArray.length()
                        LoopStart = 1

                        for (i in 0 until mArray.length()) {

                            val mData = mArray.getJSONObject(i)
                            val mylists = java.util.ArrayList<String>()
                            mylists.add(mData.getString("id"))
                            mylists.add(mData.getString("user_id"))
                            mylists.add(mData.getString("bus_cat"))
                            mylists.add(mData.getString("country"))
                            mylists.add(mData.getString("state"))
                            mylists.add(mData.getString("city"))
                            mylists.add(mData.getString("contact_no"))
                            mylists.add(mData.getString("text"))
                            mylists.add(mData.getString("text"))
                            mylists.add(mData.getString("business_category"))
                            mylists.add(mData.getString("user_name"))
                            mylists.add(AppConstants.imgUrl + mData.getString("user_profile_pic_thumb"))
                            mylists.add(mData.getString("user_profile_pic"))
                            mylists.add(mData.getString("created_date"))


                            BussinessCardList!!.add(mylists)
                            val mpostimage = mData.getJSONObject("post_images")
                            val myImglists = java.util.ArrayList<String>()

                            for (i in 0 until (mpostimage.length() / 2)) {
                                if (mpostimage.getString("main_img_" + i).length > 0) {
                                    myImglists.add(AppConstants.imgUrl + mpostimage.getString("main_img_" + i))
                                } else {
                                    myImglists.add("")
                                }
                            }
                            ImageBusinessCard!!.add(myImglists)
                        }


                    }



                    hidepDialog()
                    adapter = BusinessCardAdapter(BussinessCardList, ImageBusinessCard, context)
                    recyclerViewBusiness!!.adapter = adapter


                } else {
                    hidepDialog()

                   // Toast.makeText(context, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
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
                map["post_type"] = "0"
                map["total"] = "10"
                map["start"] = LimitStart.toString()
              /*  map["filter_category"] = mSPF.getString("filter_business_category", "")
                map["filter_state"] = mSPF.getString("filter_state", "")*/

                return map
            }


        }
        queue = newRequestQueue(activity)
        queue.add(req)
        req.retryPolicy = DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)


    }

    private fun removeItems() {



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

    private fun showpDialog() {
        pDialog = ProgressDialog(context)
        pDialog!!.setMessage("Please wait...")
        pDialog!!.setCancelable(false)
        if (!pDialog!!.isShowing())
            pDialog!!.show()
    }

    private fun hidepDialog() {
        if (pDialog!!.isShowing())
            pDialog!!.dismiss()
    }

    private fun BusinessPostUpdateMakeVolleyRequest() {
        val mURL = getString(R.string.server_url) + "BusinessCard/updateBusiness"
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("success")) {
                    Log.e("ErrorResponse", s)
//                    SOBJECT = mOBJECT.getJSONObject("data")

//                    for (i in 0..mArray.length()-1) {
//
//                        val mData = mArray.getJSONObject(i)
//                        val mylists = java.util.ArrayList<String>()
//                        SOBJECT.getString("business_id")
//                        mylists.add(mData.getString("post_id"))
//                        mylists.add(mData.getString("user_id"))
//                        mylists.add(mData.getString("user_name"))
//                        mylists.add(mData.getString("user_profile_pic_thumb"))
//                        mylists.add(mData.getString("user_profile_pic"))
//                        mylists.add(mData.getString("post_date_time"))
//                        mylists.add(mData.getString("post_text"))
//                        mylists.add(mData.getString("post_share_link"))
//                        mylists.add(mData.getString("post_my_like"))
//                        mylists.add(mData.getString("business_id"))
//                        mylists.add(mData.getString("business_name"))
//                        mylists.add(mData.getString("business_category"))
//
//                        BussinessCardList!!.add(mylists)
//
//                        val mpostimage = mData.getJSONObject("post_image")
//                        val myImglists = java.util.ArrayList<String>()
//                        for (i in 0..(mpostimage.length()/2)-1) {
////                            myImglists.add(mpostimage.getString("thumb_"+i))
//                            myImglists.add(mpostimage.getString("image_"+i))
//                        }
//                        ImageBusinessCard!!.add(myImglists)
//                    }
//                    val adapter = BusinessCardAdapter(BussinessCardList,ImageBusinessCard,context)
//                    recyclerView!!.adapter = adapter
                    Toast.makeText(context, mOBJECT.getString("message"), Toast.LENGTH_LONG).show()
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
                map["bus_description"] = bc_et_business_description!!.text.toString()
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