package com.kelybro.android.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*

import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.kelybro.android.adapters.SingleChatAdapter

import org.json.JSONException
import org.json.JSONObject

import java.io.ByteArrayOutputStream
import java.util.ArrayList
import java.util.HashMap

import com.android.volley.toolbox.Volley.newRequestQueue
import com.kelybro.android.AppConstants
import com.kelybro.android.R

/**
 * Created by Krishna on 06-04-2018.
 */

class SingleChatActivity : AppCompatActivity() {
    internal lateinit var ma_toolbar: Toolbar
    internal lateinit var recyclerView: RecyclerView
    internal lateinit var adapter: SingleChatAdapter
    internal lateinit var mOBJECT: JSONObject
    var SingleChatList: MutableList<List<String>> = ArrayList()
    internal lateinit var iv_single_chat_profile: de.hdodenhof.circleimageview.CircleImageView
    internal lateinit var tv_single_chat_name: TextView
//    internal lateinit var et_chat_message: EditText
    private var et_chat_message: EditText? = null
    internal lateinit var bt_chat_send: ImageView
    internal lateinit var iv_choose_image: ImageView
    internal lateinit var iv_chat_image: ImageView
    internal lateinit var mSPF: SharedPreferences
    internal lateinit var mEDT: SharedPreferences.Editor
    internal var sendId = ""
    internal var receiveId = ""
    internal var actionImage = ""
    internal var actionNmae = ""
    internal lateinit var queue: RequestQueue
    internal lateinit var layoutManager: LinearLayoutManager
    internal var handler: Handler? = null
    private var bitmap: Bitmap? = null
    internal var chat_image = ""
    private var pDialog: ProgressDialog? = null
    private var mAdView: AdView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_chat)

        val intent = intent
//        sendId = intent.getStringExtra("send_id")
        receiveId = intent.getStringExtra("receive_id")
        actionImage = intent.getStringExtra("action_image")
        actionNmae = intent.getStringExtra("action_name")

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, resources.getString(R.string.banner_ad_unit_id))
        mAdView = findViewById<View>(R.id.ad_view) as AdView

        // Start loading the ad in the background.
        mAdView!!.loadAd(AdRequest.Builder().build())



        mSPF = getSharedPreferences("AppData", 0)
        mEDT = mSPF.edit()
        ma_toolbar = findViewById<View>(R.id.ma_toolbar) as Toolbar
        setSupportActionBar(ma_toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = ""
        iv_single_chat_profile = findViewById<View>(R.id.iv_single_chat_profile) as de.hdodenhof.circleimageview.CircleImageView
        tv_single_chat_name = findViewById<View>(R.id.tv_single_chat_name) as TextView

        Glide.with(applicationContext).load(actionImage).error(R.drawable.ic_avatar).into(iv_single_chat_profile)
        tv_single_chat_name.text = actionNmae

        et_chat_message = findViewById<EditText>(R.id.et_chat_message) as EditText
        bt_chat_send = findViewById<Button>(R.id.bt_chat_send) as ImageView
        iv_choose_image = findViewById<ImageView>(R.id.iv_choose_image) as ImageView
        iv_chat_image = findViewById<ImageView>(R.id.iv_chat_image) as ImageView

        recyclerView = findViewById<RecyclerView>(R.id.recycler_single_chat) as RecyclerView
        adapter = SingleChatAdapter(this, SingleChatList)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        recyclerView.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val currentScroll = recyclerView!!.computeVerticalScrollOffset() + recyclerView.computeVerticalScrollExtent()
                if (currentScroll >= recyclerView.computeVerticalScrollRange())
                    timer()
                else
                    handler!!.removeMessages(0)
            }
        })
        iv_choose_image.setOnClickListener {
            val mimeTypes = arrayOf("image/jpeg", "image/png")
            val intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            intent.action = Intent.ACTION_PICK
            startActivityForResult(Intent.createChooser(intent, "Select Image"), 111)
        }

        bt_chat_send.setOnClickListener {
            if (et_chat_message!!.text.toString() != "" || chat_image !== "") {
                SendDataMakeVolleyRequest()
            }
        }
        ma_toolbar.setOnClickListener {
            val i = Intent(applicationContext, UserProfileActivity::class.java)
            i.putExtra("UserID", mSPF.getString("id", ""))
            i.putExtra("ProfileID", receiveId)
            startActivity(i)
        }
        timer()


    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, data.data)
                val baos = ByteArrayOutputStream()
                bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val imageBytes = baos.toByteArray()
                chat_image = Base64.encodeToString(imageBytes, Base64.DEFAULT)
            } catch (e: Exception) {
                Log.e("Post Images", e.message)
            }

        }
    }

    private fun MakeVolleyRequest() {
        val mURL = getString(R.string.server_url) + "Users/single_chat_history "
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener { s ->
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("success")) {
                    SingleChatList.clear()
                    val data = mOBJECT.getJSONArray("data")
                    for (i in 0 until data.length()) {
                        val mData = data.getJSONObject(i)
                        val chatlist = ArrayList<String>()
                        chatlist.add(mData.getString("id"))
                        chatlist.add(mData.getString("send_id"))
                        chatlist.add(mData.getString("receive_id"))
                        chatlist.add(mData.getString("message"))
                        chatlist.add(AppConstants.imgUrl + mData.getString("chat_image"))
                        chatlist.add(mData.getString("read"))
                        chatlist.add(mData.getString("created_date"))
                        chatlist.add(mData.getString("user_name"))
                        chatlist.add(mData.getString("profilephoto"))
                        SingleChatList.add(chatlist)
                    }
                } else {
                  //  Toast.makeText(applicationContext, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                }
                adapter.notifyDataSetChanged()
                layoutManager.scrollToPosition(SingleChatList.size - 1)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()
                map.put("user_id", mSPF.getString("id", ""))
                map.put("other_id", receiveId)
                return map
            }
        }
        queue = newRequestQueue(applicationContext)
        queue.add(req)
        req.retryPolicy = DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
    }

    private fun SendDataMakeVolleyRequest() {

        Log.e("Krishna",et_chat_message!!.text.toString())
        showpDialog()
        val mURL = getString(R.string.server_url)+"Users/send_message"
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
            Log.e("Krishna Come", s)
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("success")) {
                    et_chat_message!!.setText("")
                    chat_image = ""
                    MakeVolleyRequest()
                    Toast.makeText(this, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Errors", e.toString())
            }
            hidepDialog()
        }, Response.ErrorListener { error ->
            hidepDialog()
            Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()

                map["user_id"] = mSPF.getString("id", "")
                map["message"] = et_chat_message!!.text.toString()
                map["sender_id"] =  receiveId
                map["chat_image"] = chat_image
                map["type"] = "PHOTO"

                return map
            }

        }
        queue = Volley.newRequestQueue(this)
        queue.add(req)
        req.retryPolicy = DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)




//
//
//
//
//
//        Log.e("Krishna",et_chat_message!!.text.toString())
//        showpDialog()
////        val mURL = getString(R.string.server_url) + "Users/send_message"
//        val mURL = getString(R.string.server_url) + "Comments/add_comment"
//        val req = object : VolleyMultipartRequest(Request.Method.POST, mURL, Response.Listener { response ->
//            val resultResponse = String(response.data)
//            try {
//                Log.e("Krishna Res",resultResponse)
//                mOBJECT = JSONObject(resultResponse)
//                if (mOBJECT.getBoolean("success") == true) {
//                    et_chat_message!!.setText("")
//                    chat_image = ""
//                    MakeVolleyRequest()
//                    Toast.makeText(applicationContext, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(applicationContext, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: Exception) {
//                Log.e("Error", e.toString())
//            }
//
//            hidepDialog()
//        }, Response.ErrorListener { error ->
//            hidepDialog()
//            Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
//        }) {
//
//
//            override fun getParams(): Map<String, String> {
//                val map = HashMap<String, String>()
//                map["user_id"] = mSPF.getString("id", "")
//                map["message"] = et_chat_message!!.text.toString()
//                map["sender_id"] =  receiveId
//                map["chat_image"] = chat_image
//                map["type"] = "PHOTO"
//                return map
//            }
//        }
//        queue = newRequestQueue(applicationContext)
//        queue.add(req)
//        req.retryPolicy = DefaultRetryPolicy(5000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun timer() {
        handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                MakeVolleyRequest()
                handler!!.postDelayed(this, 3000)
            }
        }
        handler!!.postDelayed(runnable, 3000)
    }

    private fun showpDialog() {
        pDialog = ProgressDialog(this)
        pDialog!!.setMessage("Please wait...")
        pDialog!!.setCancelable(false)
        if (!pDialog!!.isShowing)
            pDialog!!.show()
    }

    private fun hidepDialog() {
        if (pDialog!!.isShowing)
            pDialog!!.dismiss()
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
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
        if (handler != null)
            handler!!.removeMessages(0)
        super.onDestroy()
    }
}