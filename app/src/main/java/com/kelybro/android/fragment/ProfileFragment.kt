package com.kelybro.android.fragment

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.android.volley.toolbox.Volley.newRequestQueue
import com.bumptech.glide.Glide
import com.kelybro.android.AppConstants
import com.kelybro.android.R
import com.kelybro.android.activities.FollowingYouActivity
import com.kelybro.android.model.VolleyMultipartRequest
import com.kelybro.android.activities.SettingActivity
import com.kelybro.android.activities.ViewImageActivity
import com.kelybro.android.activities.ViewProfileActivityNew
import kotlinx.android.synthetic.main.fragment_profile.view.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream

/**
 * Created by Krishna on 09-01-2018.
 */
class ProfileFragment : Fragment() {
    private var profile_image: ImageView? = null
    private var profile_setting: ImageView? = null
    private var pf_iv_follow_you: ImageView? = null
    private var pf_iv_setting: ImageView? = null
    private var profile_name: TextView? = null
    private var profile_total_post: TextView? = null
    private var profile_total_followers: TextView? = null
    private var profile_total_followings: TextView? = null
    private var pf_tv_edit_profile: TextView? = null
    private var vpf_ll_edit_profile: LinearLayout? = null

    internal lateinit var mOBJECT: JSONObject
    internal lateinit var SOBJECT: JSONObject
    internal lateinit var mSOBJECT: JSONObject
    internal lateinit var queue: RequestQueue
    private lateinit var mSPF: SharedPreferences
    internal lateinit var mEDT: SharedPreferences.Editor
    internal lateinit var filePath: String
    internal lateinit var mProfilePicMain: String
    private var ll_follower: LinearLayout? = null
    private var ll_following: LinearLayout? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view: View = inflater!!.inflate(R.layout.fragment_profile, container, false)
        mSPF = context.getSharedPreferences("AppData", 0)
        mEDT = mSPF.edit()

        MakeVolleyRequest()
        configureTabLayout(view)
        RefrenceControl(view)
        return view
    }

    private fun RefrenceControl(view: View) {

        profile_image = view.findViewById<ImageView>(R.id.pf_iv_image) as ImageView
        pf_iv_follow_you = view.findViewById<ImageView>(R.id.pf_iv_follow_you) as ImageView
        profile_setting = view.findViewById<ImageView>(R.id.pf_iv_setting) as ImageView
        pf_iv_setting = view.findViewById<ImageView>(R.id.pf_iv_setting) as ImageView
        profile_name = view.findViewById<TextView>(R.id.pf_tv_name) as TextView
        profile_total_post = view.findViewById<TextView>(R.id.pf_total_post) as TextView
        profile_total_followers = view.findViewById<TextView>(R.id.pf_total_followers) as TextView
        profile_total_followings = view.findViewById<TextView>(R.id.pf_total_followings) as TextView
        vpf_ll_edit_profile = view.findViewById<LinearLayout>(R.id.vpf_ll_edit_profile) as LinearLayout
        pf_tv_edit_profile = view.findViewById<TextView>(R.id.pf_tv_edit_profile) as TextView
        ll_follower = view.findViewById<LinearLayout>(R.id.ll_follower) as LinearLayout
        ll_follower!!.setOnClickListener {
            val intent = Intent(context, FollowingYouActivity::class.java)
            intent.putExtra("CurrentTab", 1)
            intent.putExtra("ProfileID", mSPF.getString("id", ""))
            startActivity(intent)
        }
        ll_following = view.findViewById<LinearLayout>(R.id.ll_following) as LinearLayout
        ll_following!!.setOnClickListener {
            val intent = Intent(context, FollowingYouActivity::class.java)
            intent.putExtra("CurrentTab", 0)
            intent.putExtra("ProfileID", mSPF.getString("id", ""))
            startActivity(intent)
        }


        profile_image!!.setOnClickListener {
            val intent = Intent(context, ViewImageActivity::class.java)
            intent.putExtra("ImagePath", mProfilePicMain)
            context.startActivity(intent)
        }
        pf_iv_follow_you!!.setOnClickListener {
            val intent = Intent(context, FollowingYouActivity::class.java)
            intent.putExtra("CurrentTab", 0)
            intent.putExtra("ProfileID", mSPF.getString("id", ""))
            startActivity(intent)
            context.startActivity(intent)
        }

        pf_iv_setting!!.setOnClickListener {
            val intent = Intent(context, SettingActivity::class.java)
            context.startActivity(intent)
        }

        pf_tv_edit_profile!!.setOnClickListener {
            val intent = Intent(context, ViewProfileActivityNew::class.java)
            context.startActivity(intent)
        }

        vpf_ll_edit_profile!!.setOnClickListener {
            val intent = Intent()
            val mimeTypes = arrayOf("image/jpeg", "image/png")
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Image"), 121)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 121 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {

            profile_image!!.setImageURI(data.data)
            val bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), data.data)
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val imageBytes = baos.toByteArray()
            filePath = Base64.encodeToString(imageBytes, Base64.DEFAULT)
            SendDataMakeVolleyRequest()
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
                    Glide.with(activity).load(AppConstants.imgUrl + mSOBJECT.getString("user_profile_pic_thumb")).into(profile_image)
                    mProfilePicMain = AppConstants.imgUrl + mSOBJECT.getString("user_profile_pic")

                    profile_total_post!!.text = mSOBJECT.getString("total_posts")
                    profile_total_followers!!.text = mSOBJECT.getString("user_total_follower")
                    profile_total_followings!!.text = mSOBJECT.getString("user_total_following")

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
                map["profile_id"] = mSPF.getString("id", "")
                return map
            }

        }
        queue = Volley.newRequestQueue(activity)
        queue.add(req)
        req.retryPolicy = DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
    }

    private fun SendDataMakeVolleyRequest() {
        val mURL = getString(R.string.server_url) + "Users/change_profilepic"
        val req = object : VolleyMultipartRequest(Request.Method.POST, mURL, Response.Listener { response ->
            val resultResponse = String(response.data)
            Log.e("Data", resultResponse)
            try {
                mOBJECT = JSONObject(resultResponse)
                if (mOBJECT.getBoolean("success")) {
                    mEDT.putString("profilephoto", AppConstants.imgUrl + mOBJECT.getString("profilephoto"))
                    mEDT.putString("profilephotoName", mOBJECT.getString("profilephoto"))
                    mEDT.commit()
                    Toast.makeText(context, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Error", e.toString())
            }
        }, Response.ErrorListener { error ->
            Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
        }) {
            override fun getParams(): Map<String, String> {
                val map = java.util.HashMap<String, String>()
                map.put("user_id", mSPF.getString("id", ""))
                map.put("image", filePath)
                return map
            }
        }
        queue = newRequestQueue(context)
        queue.add(req)
        req.retryPolicy = DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
    }

    private fun configureTabLayout(view: View) {
        view.pf_tab_layout.addTab(view.pf_tab_layout.newTab().setIcon(R.drawable.ic_dashboard_black_24dp))
        view.pf_tab_layout.addTab(view.pf_tab_layout.newTab().setIcon(R.drawable.ic_business_center_black_24dp))
        view.pf_tab_layout.addTab(view.pf_tab_layout.newTab().setIcon(R.drawable.ic_move_to_inbox_black_24dp))

        val adapter = ViewPagerAdapter(childFragmentManager)
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
                0 -> fragment = MyPostDesBoardFragment()
                1 -> fragment = MyPostBusinessCardFragment()
                2 -> fragment = MyPostJObFragment()
            }
            return fragment
        }

        override fun getCount(): Int {
            return 3
        }
    }
}