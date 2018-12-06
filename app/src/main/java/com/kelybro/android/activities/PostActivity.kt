package com.kelybro.android.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.widget.ImageView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

import com.bumptech.glide.Glide
import com.kelybro.android.R
import com.kelybro.android.customviews.CustomTextView
import com.kelybro.android.customviews.mUtitily

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
import java.util.HashMap

class PostActivity : AppCompatActivity() {

    private var user_profile_pic: CircleImageView? = null
    private var textViewUsername: CustomTextView? = null
    private var tv_post_date: CustomTextView? = null
    private var tv_total_like: CustomTextView? = null
    private var tv_total_comment: CustomTextView? = null
    private var image_pic: ImageView? = null
    private var iv_like_unlike: ImageView? = null
    private var image_comment: ImageView? = null
    private var image_share: ImageView? = null
    private var toolbar: Toolbar? = null
    private var mUtil: mUtitily? = null
    private var mSPF: SharedPreferences? = null
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    internal var handler: Handler? = null
    private lateinit var post_id : String
    internal lateinit var mOBJECT: JSONObject
    internal lateinit var SOBJECT: JSONObject
    internal lateinit var queue: RequestQueue


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        toolbar = findViewById(R.id.post_toolbar)
        toolbar!!.setTitle("Post Activity")
        setSupportActionBar(toolbar)

        mSPF = getSharedPreferences("AppData", 0)

        mUtil = mUtitily(applicationContext)


        image_pic = findViewById(R.id.image_pic)
        textViewUsername = findViewById(R.id.textViewUsername)
        tv_post_date = findViewById(R.id.tv_post_date)
        tv_total_like = findViewById(R.id.tv_total_like)
        tv_total_comment = findViewById(R.id.tv_total_comment)

        user_profile_pic = findViewById(R.id.user_profile_pic)
        iv_like_unlike = findViewById(R.id.iv_like_unlike)
        image_comment = findViewById(R.id.image_comment)
        image_share = findViewById(R.id.image_share)
        swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)as SwipeRefreshLayout


        swipeRefreshLayout.isRefreshing = true


        handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                swipeRefreshLayout.isRefreshing = false

                handler!!.postDelayed(this, 2000)
            }
        }
        handler!!.postDelayed(runnable, 2000)


        image_share!!.setOnClickListener {
            val share = Intent(Intent.ACTION_SEND)
            share.type = "text/plain"
            share.putExtra(Intent.EXTRA_SUBJECT, "Kelybro")
            val sAux = ""
            share.putExtra(Intent.EXTRA_TEXT, sAux)
            startActivity(Intent.createChooser(share, "choose one"))
        }

        val intent = intent

        if (intent != null) {
            val userName = intent.getStringExtra("user_name")
            val userProfilePicThumb = intent.getStringExtra("user_profile_pic_thumb")
            val totalLikes = intent.getStringExtra("total_likes")
            val totalComments = intent.getStringExtra("total_comments")
            val postDate = intent.getStringExtra("created_date")
            val main_image = intent.getStringExtra("post_image")
             post_id = intent.getStringExtra("post_id")

            tv_post_date!!.setText(getDateDifference(postDate))

            Glide.with(applicationContext).load(main_image).into(image_pic!!)


            textViewUsername!!.text = userName
            Glide.with(applicationContext).load("http://www.kelybro.com/$userProfilePicThumb").into(user_profile_pic!!)
            tv_total_like!!.text = totalLikes
            tv_total_comment!!.text = totalComments

        }

        iv_like_unlike!!.setOnClickListener {
            LikeUnlikeMakeVolleyRequest(applicationContext,post_id)
        }


        image_comment!!.setOnClickListener {
            val intent = Intent(applicationContext, CommentActivity::class.java)
            intent.putExtra("PostType", "1")
            intent.putExtra("PostId", post_id)
            startActivity(intent)
        }



    }
    fun getDateDifference(thenDate: String): String {

        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        format.timeZone= TimeZone.getTimeZone("Etc/GTM")

        var date : Date
        date = Date()
        try {
            date = format.parse(thenDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val now = Calendar.getInstance()
        val then = Calendar.getInstance()
        now.time = Date()
        then.time = date

        // Get the represented date in milliseconds
        val nowMs = now.timeInMillis
        val thenMs = then.timeInMillis

        // Calculate difference in milliseconds
        val diff = nowMs - thenMs

        // Calculate difference in seconds
        val diffMinutes = diff / (60 * 1000)
        val diffHours = diff / (60 * 60 * 1000)
        val diffDays = diff / (24 * 60 * 60 * 1000)

        return if (diffMinutes < 60) {
            if (diffMinutes == 0L)
                "now"
            else if (diffMinutes == 1L)
                diffMinutes.toString() + " minute ago"
            else
                diffMinutes.toString() + " minutes ago"
        } else if (diffHours < 24) {
            if (diffHours == 1L)
                diffHours.toString() + " hour ago"
            else
                diffHours.toString() + " hours ago"
        } else if (diffDays < 30) {
            if (diffDays == 1L)
                diffDays.toString() + " day ago"
            else
                diffDays.toString() + " days ago"
        } else {
            "a long time ago.."
        }
    }

    private fun LikeUnlikeMakeVolleyRequest(mcc: Context, Post_id:String) {

        val mURL = mcc.getString(R.string.server_url) + "Likes/new_like"
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
            Log.e("LikeResponse", s)
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("success")) {
                    Log.e("ErrorResponse", s)
                    SOBJECT = mOBJECT.getJSONObject("data")
                    if (SOBJECT.getBoolean("status")) {
                        tv_total_like!!.text = (Integer.parseInt(tv_total_like!!.text.toString())+1).toString()
                        Glide.with(mcc).load(R.drawable.ic_favorite_black_24dp).into(iv_like_unlike)
                        iv_like_unlike!!.setColorFilter(ContextCompat.getColor(mcc, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN)
                    } else {
                        tv_total_like!!.text = (Integer.parseInt(tv_total_like!!.text.toString())-1).toString()
                        Glide.with(mcc).load(R.drawable.ic_favorite_border_black_24dp).into(iv_like_unlike)
                        iv_like_unlike!!.setColorFilter(ContextCompat.getColor(mcc, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN)
                    }

                } else {
                    Toast.makeText(mcc, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Errors", e.toString())
            }
        }, Response.ErrorListener { error -> Toast.makeText(mcc, error.message, Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()
                map["post_id"] = Post_id
                map["user_id"] = mSPF!!.getString("id","")
                map["post_type"] = "1"
                return map
            }
        }
        queue = Volley.newRequestQueue(mcc)
        queue.add(req)
        req.retryPolicy = DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
    }




    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(applicationContext, MainActivity::class.java))
        finish()
    }

}
