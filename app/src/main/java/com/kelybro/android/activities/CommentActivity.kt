package com.kelybro.android.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.kelybro.android.adapters.CommentAdapter
import org.json.JSONArray
import org.json.JSONObject
import android.content.SharedPreferences
import android.os.Handler
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import android.text.Editable
import android.text.TextWatcher
import com.kelybro.android.AppConstants
import com.kelybro.android.R


/**
 * Created by Krishna on 18-01-2018.
 */

class CommentActivity : AppCompatActivity() {
    private var hm_et_add_comment: EditText? = null
    private var hm_iv_send_comment: ImageView? = null
    private var iv_comment_user_photo: ImageView? = null
    private var recyclerView: RecyclerView? = null
    internal lateinit var mOBJECT: JSONObject
    internal lateinit var mArray: JSONArray
    internal lateinit var queue: RequestQueue
    internal val CommentList:MutableList<MutableList<String>>?= ArrayList()
    private lateinit var mSPF: SharedPreferences
    internal lateinit var mEDT: SharedPreferences.Editor
    var PostType :String = ""
    var PostID :String = ""
    internal var handler: Handler? = null
    var adapter: CommentAdapter? = null
    internal var layoutManager: LinearLayoutManager?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        mSPF = getSharedPreferences("AppData", 0)
        mEDT = mSPF.edit()

        val intent = intent
        PostType = intent.getStringExtra("PostType")
        PostID = intent.getStringExtra("PostId")

        MakeVolleyRequest()

        hm_et_add_comment= findViewById<EditText>(R.id.hm_et_add_comment) as EditText
        iv_comment_user_photo= findViewById<ImageView>(R.id.iv_comment_user_photo) as ImageView
        Glide.with(this).load(mSPF.getString("profilephoto","")).error(R.drawable.ic_avatar).into(iv_comment_user_photo)
        hm_iv_send_comment= findViewById<ImageView>(R.id.hm_iv_send_comment) as ImageView


        hm_et_add_comment!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(arg0: Editable) {
                hm_iv_send_comment!!.setOnClickListener {

                    hm_iv_send_comment!!.isEnabled = true

                    if(hm_et_add_comment!!.text.length>0)
                        AddCommentVolleyRequest()
                    hm_iv_send_comment!!.isEnabled = false
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                hm_iv_send_comment!!.isEnabled = true
            }

        })
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView_comment) as RecyclerView
        layoutManager = LinearLayoutManager(this)
        recyclerView!!.setLayoutManager(layoutManager)
//        recyclerView!!.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false) as RecyclerView.LayoutManager?

        adapter = CommentAdapter(CommentList,this)
        recyclerView!!.adapter = adapter
        timer()
    }

    private fun AddCommentVolleyRequest() {
        Log.e("Krishna",hm_et_add_comment!!.text.toString())

        val mURL = getString(R.string.server_url)+"Comments/add_comment"
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
            Log.e("Krishna Come", s)
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("success")) {
                    hm_et_add_comment!!.setText("")
                    Toast.makeText(this, "Add comment successfully.", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Errors", e.toString())
            }

        }, Response.ErrorListener { error -> Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()

                map["user_id"] = mSPF.getString("id","")
                map["post_id"] = PostID
                map["post_type"] = PostType
                map["comment"] = hm_et_add_comment!!.text.toString()

                return map
            }

        }
        queue = Volley.newRequestQueue(this)
        queue.add(req)
        req.retryPolicy = DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
    }


    private fun MakeVolleyRequest() {
        val mURL = getString(R.string.server_url)+"Comments/get_comments"
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
            Log.e("CommentResponse", s)
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("success")) {
                    mArray = mOBJECT.getJSONArray("data")
                    if(mArray.length()>0 && mArray.length()>CommentList!!.size) {
                        CommentList.clear()
                        for (i in 0 until mArray.length()) {
                            val mData = mArray.getJSONObject(i)
                            val mylists = java.util.ArrayList<String>()
                            mylists.add(mData.getString("id"))
                            mylists.add(mData.getString("post_id"))
                            mylists.add(mData.getString("user_name"))
                            mylists.add(mData.getString("comment"))
                            mylists.add(mData.getString("post_type"))
                            mylists.add(mData.getString("user_id"))
                            mylists.add(mData.getString("rating"))
                            mylists.add(mData.getString("created_at"))
                            mylists.add(AppConstants.imgUrl + mData.getString("user_profile_pic_thumb"))
                            mylists.add(mData.getString("user_profile_pic_img"))
                            CommentList.add(mylists)
                        }
                        adapter!!.notifyDataSetChanged()
                        layoutManager!!.scrollToPosition(CommentList.size - 1)
                    }

                } else {
                    Toast.makeText(this, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Errors", e.toString())
            }

        }, Response.ErrorListener { error -> Toast.makeText(getApplicationContext(), error.message, Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()

                map["user_id"] = mSPF.getString("id","")
                map["post_id"] = PostID

                return map
            }

        }
        queue = Volley.newRequestQueue(this)
        queue.add(req)
        req.retryPolicy = DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
    }

    private fun timer() {
        handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                MakeVolleyRequest()
                handler!!.postDelayed(this, 2000)
            }
        }
        handler!!.postDelayed(runnable, 2000)
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    public override fun onPause() {
        if (handler != null) {
            handler!!.removeMessages(0)
        }
        super.onPause()
    }

    /** Called when returning to the activity  */
    public override fun onResume() {
        super.onResume()
        timer()
    }

    /** Called before the activity is destroyed  */
    public override fun onDestroy() {
        if (handler != null)
            handler!!.removeMessages(0)
        super.onDestroy()
    }
}
