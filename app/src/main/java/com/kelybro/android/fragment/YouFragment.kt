package com.kelybro.android.fragment

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
import com.kelybro.android.adapters.YouAdapter
import com.kelybro.android.R
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by Krishna on 09-01-2018.
 */
class YouFragment : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var swipeRefresh: SwipeRefreshLayout? = null
    internal lateinit var mOBJECT: JSONObject
    internal lateinit var mArray: JSONArray
    internal lateinit var queue: RequestQueue
    internal val YouDataList:MutableList<MutableList<String>>?= ArrayList()
    internal lateinit var adapter: YouAdapter
    internal lateinit var mSPF: SharedPreferences
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view: View = inflater!!.inflate(R.layout.fragment_you, container, false)
        mSPF = context.getSharedPreferences("AppData", 0)

        swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout) as SwipeRefreshLayout
        swipeRefresh!!.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorAccent)
        swipeRefresh!!.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            YouDataList!!.clear()
            MakeVolleyRequest()
        })

        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView_you) as RecyclerView
        recyclerView!!.layoutManager = LinearLayoutManager(this.activity!!, LinearLayout.VERTICAL, false) as RecyclerView.LayoutManager?
        adapter = YouAdapter(YouDataList, context)
        recyclerView!!.adapter = adapter
        MakeVolleyRequest()
        return view
    }
    private fun MakeVolleyRequest() {
        val mURL:String
        if(mSPF.getString("ProfileID","").equals(mSPF.getString("id","")))
            mURL = getString(R.string.server_url)+"Users/my_followers_list"
        else
            mURL = getString(R.string.server_url)+"Users/user_followers_list"
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("status")) {
                    mArray = mOBJECT.getJSONArray("data")
                    if(mArray.length()>0) {
                        for (i in 0..mArray.length() - 1) {
                            val mData = mArray.getJSONObject(i)
                            val mylists = java.util.ArrayList<String>()
                            mylists.add(mData.getString("id"))
                            mylists.add(mData.getString("user_id"))
                            mylists.add(mData.getString("user_name"))
                            mylists.add(AppConstants.imgUrl+mData.getString("user_profile_pic_thumb"))
                            mylists.add(mData.getString("user_profile_pic"))
                            mylists.add(mData.getString("user_is_vip"))
                            if(mSPF.getString("id","") == mData.getString("user_id"))
                                mylists.add("You")
                            else
                                mylists.add(mData.getString("user_status"))
                            YouDataList!!.add(mylists)
                        }
                        adapter.notifyDataSetChanged()
                        swipeRefresh!!.setRefreshing(false)

                    }
                } else {
                    Toast.makeText(context, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                    swipeRefresh!!.setRefreshing(false)
                }
            } catch (e: Exception) {
                swipeRefresh!!.setRefreshing(false)
                Log.e("Errors", e.toString())
            }

        }, Response.ErrorListener { error -> Toast.makeText(context, error.message, Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()
                map["user_id"] = mSPF.getString("ProfileID","")
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