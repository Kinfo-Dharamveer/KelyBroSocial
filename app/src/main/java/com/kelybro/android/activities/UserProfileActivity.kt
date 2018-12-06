package com.kelybro.android.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.kelybro.android.AppConstants
import com.kelybro.android.R
import com.kelybro.android.customviews.mUtitily
import com.kelybro.android.fragment.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import org.json.JSONObject

class UserProfileActivity : AppCompatActivity() {
    private var profile_image: ImageView? = null
    private var profile_name: TextView? = null
    private var profile_total_post: TextView? = null
    private var profile_total_followers: TextView? = null
    private var profile_total_followings: TextView? = null
    private var pf_tv_edit_profile: TextView? = null
    private var ll_follower: LinearLayout? = null
    private var ll_following: LinearLayout? = null


    internal lateinit var mOBJECT: JSONObject
    internal lateinit var SOBJECT: JSONObject
    internal lateinit var mSOBJECT: JSONObject
    internal lateinit var queue: RequestQueue
    private lateinit var mSPF: SharedPreferences
    internal lateinit var mEDT: SharedPreferences.Editor

    var mUserID: String = ""
    var mProfileID: String = ""
    var mProfileImage: String = ""
    var mUserFollowID: String = ""

    private lateinit var mUtil: mUtitily
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        mSPF = getSharedPreferences("AppData", 0)
        mEDT = mSPF.edit()
        mUserID = intent.getStringExtra("UserID")
        mProfileID = intent.getStringExtra("ProfileID")
        mEDT.putString("ProfileID", mProfileID)
        mEDT.commit()
        RefrenceControl()
        MakeVolleyRequest()


    }

    private fun RefrenceControl() {
        profile_image = findViewById<ImageView>(R.id.pf_iv_image) as ImageView
        profile_name = findViewById<TextView>(R.id.pf_tv_name) as TextView
        profile_total_post = findViewById<TextView>(R.id.pf_total_post) as TextView
        profile_total_followers = findViewById<TextView>(R.id.pf_total_followers) as TextView
        profile_total_followings = findViewById<TextView>(R.id.pf_total_followings) as TextView
        pf_tv_edit_profile = findViewById<TextView>(R.id.pf_tv_edit_profile) as TextView
        ll_follower = findViewById<LinearLayout>(R.id.ll_follower) as LinearLayout
        ll_follower!!.setOnClickListener {
            val intent = Intent(this, FollowingYouActivity::class.java)
            intent.putExtra("CurrentTab", 1)
            intent.putExtra("ProfileID", mProfileID)
            startActivity(intent)
        }
        ll_following = findViewById<LinearLayout>(R.id.ll_following) as LinearLayout
        ll_following!!.setOnClickListener {
            val intent = Intent(this, FollowingYouActivity::class.java)
            intent.putExtra("CurrentTab", 0)
            intent.putExtra("ProfileID", mProfileID)
            startActivity(intent)
        }
        pf_tv_edit_profile!!.setOnClickListener {
            if (pf_tv_edit_profile!!.text == "FOLLOW")
                UserFollowRequest()
            else if (pf_tv_edit_profile!!.text == "FOLLOW.") {
                MakeVolleyRequest(this, mUserFollowID, 1)
            } else if (pf_tv_edit_profile!!.text == "UNFOLLOW") {
                MakeVolleyRequest(this, mUserFollowID, 0)
            } else if (pf_tv_edit_profile!!.text == "CANCEL REQUEST") {
                MakeVolleyRequest(this, mUserFollowID, 0)
            }
        }

        profile_image!!.setOnClickListener {
            val intent = Intent(this, ViewImageActivity::class.java)
            intent.putExtra("ImagePath", mProfileImage)
            startActivity(intent)
        }
    }

    private fun MakeVolleyRequest() {
        val mURL = getString(R.string.server_url) + "Users/user_details"
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
            Log.e("UserDetailResponse", s)
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("status")) {
                    mSOBJECT = JSONObject()
                    mSOBJECT = mOBJECT.getJSONObject("data")
                    profile_name!!.text = mSOBJECT.getString("user_name")
                    profile_total_post!!.text = mSOBJECT.getString("total_posts")
                    profile_total_followers!!.text = mSOBJECT.getString("user_total_follower")
                    profile_total_followings!!.text = mSOBJECT.getString("user_total_following")
                    mProfileImage = AppConstants.imgUrl + mSOBJECT.getString("user_profile_pic")
                    Glide.with(this).load(AppConstants.imgUrl + mSOBJECT.getString("user_profile_pic_thumb")).error(R.drawable.ic_avatar).into(profile_image)
                    if (mSOBJECT.getString("follow") == "null") {
                        pf_tv_edit_profile!!.text = "FOLLOW"
                        pf_tv_edit_profile!!.setBackgroundResource(R.drawable.rounded_layout_sky)
                    } else if (mSOBJECT.getString("follow") == "0") {
                        pf_tv_edit_profile!!.text = "FOLLOW."
                        pf_tv_edit_profile!!.setBackgroundResource(R.drawable.rounded_layout_sky)
                    } else if (mSOBJECT.getString("follow") == "1") {
                        pf_tv_edit_profile!!.text = "CANCEL REQUEST"
                        pf_tv_edit_profile!!.setBackgroundResource(R.drawable.rounded_layout_sky)
                    } else if (mSOBJECT.getString("follow") == "2") {
                        pf_tv_edit_profile!!.text = "UNFOLLOW"
                        pf_tv_edit_profile!!.setBackgroundResource(R.drawable.rounded_layout_sky)
                    }
                    if (mUserID == mProfileID) {
                        pf_tv_edit_profile!!.text = "You"
                        pf_tv_edit_profile!!.setBackgroundResource(R.drawable.rounded_layout_sky)
                    }
                    mUserFollowID = mSOBJECT.getString("user_follow_id")
                    configureTabLayout(findViewById(android.R.id.content))
                } else {
                    Toast.makeText(this, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Errors", e.toString())
            }

        }, Response.ErrorListener { error -> Toast.makeText(this, error.message, Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()
                map["user_id"] = mUserID
                map["profile_id"] = mProfileID
                return map
            }

        }
        queue = Volley.newRequestQueue(applicationContext)
        queue.add(req)
        req.retryPolicy = DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
    }

    private fun UserFollowRequest() {
        val mURL = getString(R.string.server_url) + "Users/follow_request"
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
            Log.e("User Follow Request", s)
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("success")) {
                    pf_tv_edit_profile!!.text = "CANCEL REQUEST"
                    pf_tv_edit_profile!!.setBackgroundResource(R.drawable.rounded_corner_you_border)
                }
                Toast.makeText(this, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("Errors", e.toString())
            }

        }, Response.ErrorListener { error -> Toast.makeText(this, error.message, Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()
                map["user_id"] = mUserID
                map["other_id"] = mProfileID
                return map
            }

        }
        queue = Volley.newRequestQueue(applicationContext)
        queue.add(req)
        req.retryPolicy = DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
    }


    private fun configureTabLayout(view: View) {
        view.pf_tab_layout.addTab(view.pf_tab_layout.newTab().setIcon(R.drawable.ic_dashboard_black_24dp))
        view.pf_tab_layout.addTab(view.pf_tab_layout.newTab().setIcon(R.drawable.ic_business_center_black_24dp))
        view.pf_tab_layout.addTab(view.pf_tab_layout.newTab().setIcon(R.drawable.ic_move_to_inbox_black_24dp))

        val adapter = ViewPagerAdapter(supportFragmentManager)
        view.pf_view_pager.adapter = adapter

        view.pf_view_pager.addOnPageChangeListener(
                TabLayout.TabLayoutOnPageChangeListener(view.pf_tab_layout))
        view.pf_tab_layout.addOnTabSelectedListener(object :
                TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                view.pf_view_pager.currentItem = tab.position
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
                0 -> fragment = UserPostDesBoardFragment()
                1 -> fragment = UserPostBusinessCardFragment()
                2 -> fragment = UserPostJobFragment()
            }
            return fragment
        }

        override fun getCount(): Int {
            return 3
        }
    }

    private fun MakeVolleyRequest(mcc: Context, Post_id: String, Follow_id: Int) {
        val mURL = mcc.getString(R.string.server_url) + "Users/request_type"
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
            try {
                val mOBJECT: JSONObject = JSONObject(s)
                if (mOBJECT.getBoolean("success")) {
                    if (Follow_id == 0)
                        pf_tv_edit_profile!!.text = "FOLLOW."
                    if (Follow_id == 1)
                        pf_tv_edit_profile!!.text = "CANCEL REQUEST"

                    Toast.makeText(mcc, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(mcc, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("ErrorsDeletePost", e.toString())
            }
        }, Response.ErrorListener { error -> Toast.makeText(mcc, error.message, Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val map = java.util.HashMap<String, String>()
                map["id"] = Post_id
                map["follow_type"] = Follow_id.toString()
                return map
            }
        }
        queue = Volley.newRequestQueue(mcc)
        queue.add(req)
        req.retryPolicy = DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
    }
}
