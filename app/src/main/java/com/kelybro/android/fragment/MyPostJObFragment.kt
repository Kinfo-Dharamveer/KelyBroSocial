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
import com.kelybro.android.adapters.MyPostJobAdapter
import com.kelybro.android.R
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by Krishna on 15-02-2018.
 */

class MyPostJObFragment : Fragment() {
    private var recyclerView: RecyclerView? = null
    internal lateinit var mOBJECT: JSONObject
    internal lateinit var mArray: JSONArray
    internal lateinit var queue: RequestQueue
    internal val MyJobPostList: MutableList<MutableList<String>>? = ArrayList()
    internal var LimitStart = 0
    internal var LoopStart = 1
    internal var maxScroll = 0
    internal var pDialog: ProgressDialog? = null
    var adapter: MyPostJobAdapter? = null
    private lateinit var mSPF: SharedPreferences
    internal lateinit var mEDT: SharedPreferences.Editor
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout


    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            if (isResumed && LimitStart == 0) {
                MakeVolleyRequest()
                Log.e("hhhh", "uservisibility")
            }
        }
    }



    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view: View = inflater!!.inflate(R.layout.fragment_mypost_job, container, false)

        mSPF = context.getSharedPreferences("AppData", 0)
        mEDT = mSPF.edit()
        swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout) as SwipeRefreshLayout
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark)
        recyclerView = view?.findViewById<RecyclerView>(R.id.job_recyclerView) as RecyclerView
        recyclerView!!.layoutManager = LinearLayoutManager(this!!.activity!!, LinearLayout.VERTICAL, false) as RecyclerView.LayoutManager?
        adapter = MyPostJobAdapter(MyJobPostList, context)
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
//        pDialog!!.setCancelable(false)

        if (userVisibleHint && LimitStart == 0) {
            MakeVolleyRequest()
            Log.e("hhhh", "oncreate")
        }
        swipeRefreshLayout.setOnRefreshListener {
            MyJobPostList!!.clear()

            LimitStart = 0
            MakeVolleyRequest()

            swipeRefreshLayout.isRefreshing = false

        }
        return view
    }

    private fun MakeVolleyRequest() {
        val mURL = getString(R.string.server_url) + "Job/get_myjob "
        showpDialog()
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
            Log.e("MyJobResponse", s)
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("success")) {
                    Log.e("jobErrorResponse", s)
                    mArray = mOBJECT.getJSONArray("data")

                    LimitStart += mArray.length()
                    LoopStart = 1

                    for (i in 0..mArray.length() - 1) {

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
                       // mylists.add(mData.getString("post_my_like"))
                        mylists.add("")
                        mylists.add(mData.getString("post_my_apply"))
                        mylists.add(mData.getString("post_share_link"))
                        mylists.add(mData.getString("is_vip"))
                        mylists.add(mData.getString("business_name"))
                        MyJobPostList!!.add(mylists)
                    }
                    hidepDialog()

                    adapter = MyPostJobAdapter(MyJobPostList, context)
                    recyclerView!!.adapter = adapter
                } else {
                    hidepDialog()
//                    Toast.makeText(context, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                hidepDialog()
                Log.e("Errors", e.toString())
            }

        }, Response.ErrorListener { error ->
            Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
            hidepDialog()
        }) {
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()

//                map["user_id"] = "4838"
                map["user_id"] = mSPF.getString("id","")
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
        if (!pDialog!!.isShowing)
            pDialog!!.show()
    }

    private fun hidepDialog() {
        if (pDialog!!.isShowing)
            pDialog!!.dismiss()
    }
}
