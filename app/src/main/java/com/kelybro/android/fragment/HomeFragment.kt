package com.kelybro.android.fragment

import android.app.ProgressDialog
import android.content.*
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley.newRequestQueue
import com.kelybro.android.adapters.HomeAdapter
import com.kelybro.android.R
import com.kelybro.android.activities.YourPostActivityNew
import org.json.JSONArray
import org.json.JSONObject
import android.support.v4.widget.NestedScrollView
import android.support.v4.widget.SwipeRefreshLayout
import android.view.ViewTreeObserver
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.kelybro.android.AppConstants
import kotlinx.android.synthetic.main.row_home_list.*
import java.util.*


/**
 * Created by Krishna on 09-01-2018.
 */

class HomeFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var nestedView: NestedScrollView? = null
    internal lateinit var mOBJECT: JSONObject
    internal lateinit var mArray: JSONArray
    internal lateinit var queue: RequestQueue
    //    private var progressbar: ProgressBar? = null
    internal val PostData: MutableList<MutableList<String>>? = ArrayList()
    internal val AdData: MutableList<MutableList<String>>? = ArrayList()
    internal val ImagePostData: MutableList<ArrayList<String>>? = ArrayList()
    private var hf_tv_post_you: TextView? = null
    private lateinit var mSPF: SharedPreferences
    internal lateinit var mEDT: SharedPreferences.Editor
    internal var maxScroll = 0
    internal var LimitStart = 0
    internal var pDialog: ProgressDialog? = null
    var adapter:HomeAdapter?=null
    var iv_profile:ImageView?=null
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    internal var handler: Handler? = null


    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            if (isResumed && LimitStart == 0) {
                MakeVolleyRequest()
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view: View = inflater!!.inflate(R.layout.fragment_home, container, false)
        mSPF = context.getSharedPreferences("AppData", 0)
        mEDT = mSPF.edit()
        nestedView = view.findViewById<NestedScrollView>(R.id.nestesd_scroll_view) as NestedScrollView
        iv_profile = view.findViewById(R.id.iv_user_post)
        Glide.with(context).load(mSPF.getString("profilephoto","")).error(R.drawable.ic_avatar).into(iv_profile)
        swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)as SwipeRefreshLayout
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark)
        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView) as RecyclerView
        recyclerView!!.layoutManager = LinearLayoutManager(this.activity!!, LinearLayout.VERTICAL, false) as RecyclerView.LayoutManager?
        adapter = HomeAdapter(PostData, ImagePostData, context,AdData)
        recyclerView!!.adapter = adapter
        hf_tv_post_you = view.findViewById<TextView>(R.id.hf_tv_post_you) as TextView
        hf_tv_post_you!!.setOnClickListener {

            val intent = Intent(context, YourPostActivityNew::class.java)
            startActivityForResult(intent,999)
        }
        recyclerView?.getLayoutManager()?.scrollToPosition(5)



        nestedView!!.getViewTreeObserver().addOnScrollChangedListener(ViewTreeObserver.OnScrollChangedListener {
            val view = nestedView!!.getChildAt(nestedView!!.getChildCount() - 1) as View

            val diff = view.bottom - (nestedView!!.getHeight() + nestedView!!.getScrollY())

            if (diff == 0 && LimitStart >2) {
                MakeVolleyRequest()
            }
        })

        LocalBroadcastManager.getInstance(context).registerReceiver(
                mMessageReceiver,  IntentFilter("intentKey"))


        //Dialog
        pDialog = ProgressDialog(context)
        pDialog!!.setMessage("Please wait...")
        pDialog!!.setCancelable(false)

        if (userVisibleHint && LimitStart == 0) {
            MakeVolleyRequest()
        }
        swipeRefreshLayout.isRefreshing = true
        MakeVolleyRequest()


        handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                swipeRefreshLayout.isRefreshing = false

                handler!!.postDelayed(this, 2000)
            }
        }
        handler!!.postDelayed(runnable, 2000)



        swipeRefreshLayout.setOnRefreshListener {
            PostData!!.clear()
            ImagePostData!!.clear()
            AdData!!.clear()
            LimitStart=0
            MakeVolleyRequest()

            swipeRefreshLayout.isRefreshing = false

        }

        return view
    }



    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Get extra data included in the Intent
            val message = intent.getStringExtra("key")
            // For like counting
            if (message.equals("Post Like")){
                val totalLike = tv_total_like!!.getText()
                val totalL = Integer.parseInt(totalLike.toString())
                tv_total_like!!.text = ""+totalL?.inc()
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

            }
            else if(message.equals("Comment Notification")){
                val total_comments = intent.getStringExtra("total_comments")
                tv_total_comment!!.text = total_comments
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==999){
            PostData!!.clear()
            ImagePostData!!.clear()
            AdData!!.clear()
            LimitStart=0
            MakeVolleyRequest()
        }
    }

    private fun MakeVolleyRequest() {
        val mURL = getString(R.string.server_url) + "Posts/get_posts"
        showpDialog()
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
            Log.e("HomeResponse", s)
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("success")) {
                    mArray = mOBJECT.getJSONArray("advertize")
                    for (i in 0 until mArray.length()) {
                        val mData = mArray.getJSONObject(i)
                        val mylists = java.util.ArrayList<String>()
                        mylists.add(mData.getString("id"))
                        mylists.add(mData.getString("ad_name"))
                        mylists.add(mData.getString("ad_description"))
                        mylists.add(AppConstants.imgUrl + mData.getString("ad_image"))
                        mylists.add(mData.getString("action"))
                        AdData!!.add(mylists)
                    }
                    mArray = mOBJECT.getJSONArray("data")

                    LimitStart += mArray.length()
                    for (i in 0 until mArray.length()) {
                        val mData = mArray.getJSONObject(i)
                        val mylists = java.util.ArrayList<String>()
                        mylists.add(mData.getString("user_name"))
                        mylists.add(AppConstants.imgUrl + mData.getString("user_profile_pic_thumb"))
                        mylists.add(mData.getString("user_profile_pic"))
                        mylists.add(mData.getString("is_vip"))
                        mylists.add(mData.getString("post_my_like"))
                        mylists.add(mData.getString("post_share_link"))
                        mylists.add(mData.getString("id"))
                        mylists.add(mData.getString("total_likes"))
                        mylists.add(mData.getString("total_comments"))
                        mylists.add(mData.getString("video_link"))
                        mylists.add(mData.getString("created_date"))
                        mylists.add(mData.getString("user_id"))
                        mylists.add(mData.getString("post_text"))
                        mylists.add(mData.getString("type"))

                        if(i%5==0 && i!=0) {
                            PostData!!.add(mylists)
                            PostData!!.add(mylists)
                        }else{
                            PostData!!.add(mylists)
                        }
                        val myImglists = ArrayList<String>()
                        if(mData.getString("post_image")!="") {
                            val mpostimage = mData.getJSONObject("post_image")
                            for (i in 0 until (mpostimage.length() / 2)) {
                                if (mpostimage.getString("main_img_" + i).isNotEmpty()) {
                                    myImglists.add(AppConstants.imgUrl + mpostimage.getString("main_img_" + i))
                                } else {
                                    myImglists.add("")
                                }
                            }
                        }
                        if (i % 5 == 0 && i != 0) {
                            ImagePostData!!.add(myImglists)
                            ImagePostData.add(myImglists)
                        } else {
                            ImagePostData!!.add(myImglists)
                        }

                    }
                    adapter!!.notifyDataSetChanged()
                    hidepDialog()
                } else {
                    hidepDialog()
                    //Toast.makeText(context, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                hidepDialog()
                Log.e("Errors", e.toString())
            }

        }, Response.ErrorListener
        { error -> Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
            hidepDialog()})


        {
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()
                map["user_id"] = mSPF.getString("id", "")
                map["post_type"] = "1"
                map["total"] = "10"
                map["start"] = LimitStart.toString()
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
        if (!pDialog!!.isShowing())
            pDialog!!.show()
    }

    private fun hidepDialog() {
        if (pDialog!!.isShowing())
            pDialog!!.dismiss()
    }



}



