package com.kelybro.android.fragment

import android.app.ProgressDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.kelybro.android.AppConstants
import com.kelybro.android.adapters.MyPostBusinessCardAdapter
import com.kelybro.android.R
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by krishna on 05/04/18.
 */
class UserPostBusinessCardFragment : Fragment() {
    private var recyclerView: RecyclerView? = null
    internal lateinit var mOBJECT: JSONObject
    internal lateinit var mArray: JSONArray
    internal lateinit var queue: RequestQueue
    internal val MyBussinessCardList: MutableList<MutableList<String>>? = ArrayList()
    internal val MyImageBusinessCard: MutableList<ArrayList<String>>? = ArrayList()
    private lateinit var mSPF: SharedPreferences
    internal lateinit var mEDT: SharedPreferences.Editor
    internal var LimitStart = 0
    internal var LoopStart = 1
    internal var maxScroll = 0
    internal var pDialog: ProgressDialog? = null
    var adapter: MyPostBusinessCardAdapter?=null
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            if (isResumed && LimitStart == 0) {
                MakeVolleyRequest()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view: View = inflater!!.inflate(R.layout.fragment_mypost_businesscard, container, false)

        mSPF = context.getSharedPreferences("AppData", 0)
        mEDT = mSPF.edit()
        swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)as SwipeRefreshLayout
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark)
        recyclerView = view?.findViewById<RecyclerView>(R.id.bc_recyclerView) as RecyclerView
        recyclerView!!.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false) as RecyclerView.LayoutManager?
        adapter = MyPostBusinessCardAdapter(MyBussinessCardList, MyImageBusinessCard, context)
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
                    MakeVolleyRequest()
            }

        })
        pDialog = ProgressDialog(context)
        pDialog!!.setMessage("Please wait...")
        pDialog!!.setCancelable(false)

        if (userVisibleHint && LimitStart == 0) {
            MakeVolleyRequest()
        }

        swipeRefreshLayout.setOnRefreshListener {
            MyBussinessCardList!!.clear()
            MyImageBusinessCard!!.clear()
            LimitStart=0
            MakeVolleyRequest()

            swipeRefreshLayout.isRefreshing = false

        }
        return view

    }


    private fun MakeVolleyRequest() {
        val mURL = getString(R.string.server_url) + "BusinessCard/get_mybusinesscard"
        showpDialog()
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
            Log.e("MyBusinessResponse", s)
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("success")) {
                    mArray = mOBJECT.getJSONArray("data")

                    LimitStart += mArray.length()
                    LoopStart = 1

                    for (i in 0 until mArray.length()) {

                        val mData = mArray.getJSONObject(i)
                        val mylists = java.util.ArrayList<String>()
                        mylists.add(mData.getString("id"))
                        mylists.add(mData.getString("user_id"))
                        mylists.add(mData.getString("business_name"))
                        mylists.add(mData.getString("business_contactno"))
                        mylists.add(mData.getString("location"))
                        mylists.add(mData.getString("business_cat_id"))
                        mylists.add(mData.getString("created_date"))
                        mylists.add(mData.getString("post_text"))
                        mylists.add(mData.getString("post_share_link"))
                        mylists.add(mData.getString("user_name"))
                        mylists.add(AppConstants.imgUrl+mData.getString("user_profile_pic_thumb"))
                        mylists.add(mData.getString("user_profile_pic"))
                        mylists.add(mData.getString("post_my_like"))
//                        mylists.add(mData.getString("video_link"))
                        mylists.add(mData.getString("business_category"))
                        mylists.add(mData.getString("is_vip"))
                        MyBussinessCardList!!.add(mylists)
                        if(mData.getString("post_images")!="") {
                            val mpostimage = mData.getJSONObject("post_images")
                            val myImglists = java.util.ArrayList<String>()
                            for (i in 0 until (mpostimage.length() / 2)) {
                                if (mpostimage.getString("main_img_" + i).length > 0) {
                                    myImglists.add(AppConstants.imgUrl + mpostimage.getString("main_img_" + i))
                                } else {
                                    myImglists.add("")
                                }
                            }
                            MyImageBusinessCard!!.add(myImglists)
                        }
                    }
                    hidepDialog()
                    adapter = MyPostBusinessCardAdapter(MyBussinessCardList, MyImageBusinessCard, context)
                    recyclerView!!.adapter = adapter
                } else {
                    hidepDialog()
                    Toast.makeText(context, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                hidepDialog()
                Log.e("Errors", e.toString())
            }

        }, Response.ErrorListener { error -> Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
            hidepDialog() }) {
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()

                map["user_id"] = mSPF.getString("ProfileID","")
                map["profile_id"] = mSPF.getString("id","")
                map["post_type"] = "0"
                map["total"] = "10"
                map["start"] = LimitStart.toString()


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

}