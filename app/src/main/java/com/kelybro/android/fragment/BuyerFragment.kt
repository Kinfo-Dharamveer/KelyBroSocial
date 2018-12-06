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
import com.android.volley.toolbox.Volley
import com.kelybro.android.adapters.BuyerAdapter
import com.kelybro.android.R
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by Krishna on 23-01-2018.
 */
class BuyerFragment :Fragment(){
    private var recyclerView: RecyclerView? = null
    internal lateinit var mOBJECT: JSONObject
    internal lateinit var SOBJECT: JSONObject
    internal lateinit var mArray: JSONArray
    internal lateinit var queue: RequestQueue
    internal val BuyerList:MutableList<MutableList<String>>?= ArrayList()
    private var add_buyer_button: FloatingActionButton? = null
    private var BuyerItemDialog: AlertDialog? = null
    private var btn_buy_submit: Button? = null
    private var edt_location: EditText? = null
    private var edt_number: EditText? = null
    private var edt_name: EditText? = null
    private var edt_description: EditText? = null
    private var edt_price: EditText? = null
    private var edt_category: EditText? = null
    private var edt_product_name: EditText? = null
    internal var LimitStart = 0
    internal var maxScroll = 0
    internal var pDialog: ProgressDialog? = null
    var adapter : BuyerAdapter?=null
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mSPF: SharedPreferences

    private var sp_bs_categories:Spinner? = null
    val BusinessCategoryList = ArrayList<String>()
    val BusinessCategoryListID = ArrayList<String>()
    var dataAdapter: ArrayAdapter<String>?=null
    var mBusinessCaterodyID:String=""

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            if (isResumed && LimitStart == 0) {
                MakeVolleyRequestBuyer()
            }

        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view: View = inflater!!.inflate(R.layout.fragment_buyer, container, false)
        mSPF = context.getSharedPreferences("AppData", 0)
        add_buyer_button = view.findViewById<FloatingActionButton>(R.id.add_buyer_button) as FloatingActionButton

        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView_buyer) as RecyclerView
        recyclerView!!.layoutManager = LinearLayoutManager(this.activity!!, LinearLayout.VERTICAL, false) as RecyclerView.LayoutManager?
        adapter = BuyerAdapter(BuyerList,context)
        recyclerView!!.adapter = adapter
        recyclerView!!.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            internal var scrollDy = 0
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                maxScroll = recyclerView!!.computeVerticalScrollRange()
                scrollDy += dy
                val currentScroll = recyclerView!!.computeVerticalScrollOffset() + recyclerView.computeVerticalScrollExtent()
                if (currentScroll >= maxScroll && maxScroll != 0)
                    MakeVolleyRequestBuyer()
            }

        })
        pDialog = ProgressDialog(context)
        pDialog!!.setMessage("Please wait...")
        pDialog!!.setCancelable(true)


        LocalBroadcastManager.getInstance(context).registerReceiver(
                mMessageReceiver,  IntentFilter("buyerSeller"))

        if (userVisibleHint && LimitStart == 0) {
            MakeVolleyRequestBuyer()
        }


        swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)as SwipeRefreshLayout
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark)
        swipeRefreshLayout.setOnRefreshListener {
            BuyerList!!.clear()
            LimitStart=0
            MakeVolleyRequestBuyer()
            swipeRefreshLayout.isRefreshing = false
        }
        add_buyer_button!!.setOnClickListener(View.OnClickListener {

            val view: View = inflater!!.inflate(R.layout.add_buyer_item, container,
                    false)
            val dialog = AlertDialog.Builder(context)
            dialog.setView(view)
            BuyerItemDialog = dialog.show()


            edt_product_name = view.findViewById<EditText>(R.id.edt_product_name) as EditText
            edt_category = view.findViewById<EditText>(R.id.edt_category) as EditText
            edt_price = view.findViewById<EditText>(R.id.edt_price) as EditText
            edt_description = view.findViewById<EditText>(R.id.edt_description) as EditText
            edt_name = view.findViewById<EditText>(R.id.edt_name) as EditText
            edt_number = view.findViewById<EditText>(R.id.edt_number) as EditText
            edt_location = view.findViewById<EditText>(R.id.edt_location) as EditText
            btn_buy_submit = view.findViewById<Button>(R.id.btn_buy_submit) as Button
            btn_buy_submit!!.setOnClickListener {
                BuyerItemMakeVolleyRequest()
            }

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

        })
        return view
    }


    private val mMessageReceiver = object : BroadcastReceiver(){

        override fun onReceive(context: Context, intent: Intent) {

            val filter_product_name = intent.getStringExtra("filter_product_name")
            val filter_product_category = intent.getStringExtra("filter_product_category")
            val filter_product_city = intent.getStringExtra("filter_product_city")

            showpDialog()
            val mURL = getString(R.string.server_url)+"Product/get_product_buyer"
            val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
                Log.e("BuyerResponse", s)
                try {
                    mOBJECT = JSONObject(s)
                    if (mOBJECT.getBoolean("success")) {
                        mArray = mOBJECT.getJSONArray("data")
                        LimitStart += mArray.length()



                        if(mArray.length()>0) {
                            for (i in 0 until mArray.length()) {
                                val mData = mArray.getJSONObject(i)
                                val mylists = java.util.ArrayList<String>()
                                mylists.add(mData.getString("id"))
                                mylists.add(mData.getString("user_id"))
                                mylists.add(mData.getString("type"))
                                mylists.add(mData.getString("product_name"))
                                mylists.add(mData.getString("description"))
                                mylists.add(mData.getString("category"))
                                mylists.add(mData.getString("price"))
                                mylists.add(mData.getString("name"))
                                mylists.add(mData.getString("number"))
                                mylists.add(mData.getString("location"))
                                mylists.add(mData.getString("views"))
                                mylists.add(mData.getString("created_at"))
                                mylists.add(mData.getString("user_name"))
                                mylists.add("http://www.kelybro.com/" + mData.getString("user_profile_pic_thumb"))
                                mylists.add(mData.getString("user_profile_pic"))
                                mylists.add(mData.getString("is_vip"))
                                BuyerList!!.add(mylists)
                            }
                        }


                        adapter!!.notifyDataSetChanged()
                        if (mOBJECT.getString("message").equals("No result found.")){
                            recyclerView!!.visibility = android.view.View.GONE
                            Toast.makeText(context, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                            hidepDialog()

                        }
                    } else {
                        hidepDialog()
                        recyclerView!!.visibility = android.view.View.GONE
                        Toast.makeText(context, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("Errors", e.toString())
                    hidepDialog()
                }

            },
                    Response.ErrorListener { error ->
                        hidepDialog()
                        Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
                    })
            {
                override fun getParams(): Map<String, String> {
                    val map = HashMap<String, String>()

                    //map["user_id"] = mSPF.getString("id", "")
                    map["total"] = "10"
                    map["start"] = LimitStart.toString()
                    map["filter_name"] = filter_product_name
                    map["filter_category"] = filter_product_category
                    map["filter_city"] = filter_product_city

                    return map
                }

            }
            queue = Volley.newRequestQueue(activity)
            queue.add(req)
            req.retryPolicy = DefaultRetryPolicy(10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        }

    }



    private fun MakeVolleyRequestBuyer() {

        showpDialog()
        val mURL = getString(R.string.server_url)+"Product/get_product_buyer"
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
            Log.e("BuyerResponse", s)
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("success")) {
                    mArray = mOBJECT.getJSONArray("data")
                    LimitStart += mArray.length()



                    if(mArray.length()>0) {
                        for (i in 0 until mArray.length()) {
                            val mData = mArray.getJSONObject(i)
                            val mylists = java.util.ArrayList<String>()
                            mylists.add(mData.getString("id"))
                            mylists.add(mData.getString("user_id"))
                            mylists.add(mData.getString("type"))
                            mylists.add(mData.getString("product_name"))
                            mylists.add(mData.getString("description"))
                            mylists.add(mData.getString("category"))
                            mylists.add(mData.getString("price"))
                            mylists.add(mData.getString("name"))
                            mylists.add(mData.getString("number"))
                            mylists.add(mData.getString("location"))
                            mylists.add(mData.getString("views"))
                            mylists.add(mData.getString("created_at"))
                            mylists.add(mData.getString("user_name"))
                            mylists.add("http://kelybro.com/kelybro/" + mData.getString("user_profile_pic_thumb"))
                            mylists.add(mData.getString("user_profile_pic"))
                            mylists.add(mData.getString("is_vip"))
                            BuyerList!!.add(mylists)
                        }
                    }

                    val name = mSPF.getString("filter_product_name","")

                    if (name.length>0){
                        BuyerList!!.clear()
                    }


                    adapter!!.notifyDataSetChanged()
                    hidepDialog()
                } else {
                    hidepDialog()
                    recyclerView!!.visibility = android.view.View.GONE
                    Toast.makeText(context, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Errors", e.toString())
                hidepDialog()
            }

        },
                Response.ErrorListener { error -> Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
                    hidepDialog()})
        {
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()

                //map["user_id"] = mSPF.getString("id", "")
                map["total"] = "10"
                map["start"] = LimitStart.toString()
                map["filter_name"] = mSPF.getString("filter_product_name","")
                map["filter_category"] = mSPF.getString("filter_product_category","")
                map["filter_city"] = mSPF.getString("filter_product_city","")

                return map
            }

        }
        queue = Volley.newRequestQueue(activity)
        queue.add(req)
        req.retryPolicy = DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
    }

    private fun BuyerItemMakeVolleyRequest() {
        val mURL = getString(R.string.server_url)+"Product/new_buyer"
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
            Log.e("BuyerAddItemResponse", s)
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("success")) {
                    BuyerItemDialog!!.dismiss()
                    BuyerList!!.clear()
                    LimitStart=0
                    MakeVolleyRequestBuyer()
                } else {
                    Toast.makeText(context, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Errors", e.toString())
            }

        }, Response.ErrorListener { error -> Toast.makeText(context, error.message, Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()

                map["user_id"] = mSPF.getString("id","")
                map["category"] = mBusinessCaterodyID
                map["product_name"] = edt_product_name!!.text.toString()
                map["price"] = edt_price!!.text.toString()
                map["description"] = edt_description!!.text.toString()
                map["name"] = edt_name!!.text.toString()
                map["number"] = edt_number!!.text.toString()
                map["location"] = edt_location!!.text.toString()

                return map
            }
        }
        queue = Volley.newRequestQueue(activity)
        queue.add(req)
        req.retryPolicy = DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
    }

    private fun showpDialog() {

        if (!pDialog!!.isShowing())
            pDialog!!.show()
    }

    private fun hidepDialog() {
        if (pDialog!!.isShowing())
            pDialog!!.dismiss()
    }

    private fun BusinessCategoryVolleyRequest() {
        val mURL = getString(R.string.server_url) + "Product/get_product_categories"
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
            Log.e("JobCategoryResponse", s)
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("success")) {
                    if(mOBJECT.getJSONArray("data")!=null) {
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
        queue = Volley.newRequestQueue(activity)
        queue.add(req)
        req.retryPolicy = DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
    }
}