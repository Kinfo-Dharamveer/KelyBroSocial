package com.kelybro.android.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kelybro.android.AppConstants
import com.kelybro.android.R
import com.kelybro.android.model.ModelGPJPFormDetail
import com.orhanobut.hawk.Hawk
import org.json.JSONArray
import org.json.JSONObject


/**
 * Created by Krishna on 24-01-2018.
 */
class SettingActivity : AppCompatActivity() {

    //    private var tv_our_unity: TextView? = null
//    private var tv_st_directory: TextView? = null
//    private var tv_st_news: TextView? = null
    private var tv_st_share: TextView? = null
    private var tv_st_rate_us: TextView? = null
    private var tv_st_feedback: TextView? = null
    private var tv_st_about_us: TextView? = null
    private var tv_GPJPForm: TextView? = null
    private var tv_st_logout: TextView? = null
    private var tv_Add_GPJPForm: TextView? = null
    private var tv_Download_GPJPForm: TextView? = null
    private var ivGPJPForm: ImageView? = null
    private var llGPJPForm: LinearLayout? = null
    private lateinit var mSPF: SharedPreferences
    internal lateinit var mEDT: SharedPreferences.Editor
    internal lateinit var queue: RequestQueue
    internal lateinit var mArray: JSONArray
    internal lateinit var UserFormDetail: ModelGPJPFormDetail
    private val MY_PREFS_NAME = "formCounter"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        mSPF = getSharedPreferences("AppData", 0)
        mEDT = mSPF.edit()
//        tv_our_unity = findViewById<TextView>(R.id.tv_our_unity) as TextView
//        tv_st_directory = findViewById<TextView>(R.id.tv_st_directory) as TextView
//        tv_st_news = findViewById<TextView>(R.id.tv_st_news) as TextView
        tv_st_share = findViewById<TextView>(R.id.tv_st_share) as TextView
        tv_st_rate_us = findViewById<TextView>(R.id.tv_st_rate_us) as TextView
        tv_st_feedback = findViewById<TextView>(R.id.tv_st_feedback) as TextView
        tv_st_about_us = findViewById<TextView>(R.id.tv_st_about_us) as TextView
        tv_GPJPForm = findViewById<TextView>(R.id.tv_GPJPForm) as TextView
        tv_st_logout = findViewById<TextView>(R.id.tv_st_logout) as TextView
        tv_Add_GPJPForm = findViewById<TextView>(R.id.tv_Add_GPJPForm) as TextView
        tv_Download_GPJPForm = findViewById<TextView>(R.id.tv_Download_GPJPForm) as TextView
        ivGPJPForm = findViewById<ImageView>(R.id.ivGPJPForm) as ImageView
        llGPJPForm = findViewById<LinearLayout>(R.id.llGPJPForm) as LinearLayout

//        tv_our_unity!!.setOnClickListener {
//            val intent = Intent(this, OurUnityActivity::class.java)
//            startActivity(intent)
//        }
//
//        tv_st_directory!!.setOnClickListener {
//            val intent = Intent(this, DirectoryActivity::class.java)
//            startActivity(intent)
//        }
//
//        tv_st_news!!.setOnClickListener {
//            val intent = Intent(this, NewsActivity::class.java)
//            startActivity(intent)
//        }

        tv_st_share!!.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "Hey check out my app at: https://play.google.com/store/apps/details?id=" + packageName)
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }
        tv_st_rate_us!!.setOnClickListener {
            //            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + R.string.app_name)))
            try {
                startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + this.packageName)))
            } catch (e: android.content.ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + this.packageName)))
            }

        }
        tv_st_feedback!!.setOnClickListener {
            //            val Email = Intent(Intent.ACTION_SEND)
//            Email.type = "text/email"
//            Email.putExtra(Intent.EXTRA_EMAIL, arrayOf("admin@hotmail.com"))
//            Email.putExtra(Intent.EXTRA_SUBJECT, "Feedback")
//            Email.putExtra(Intent.EXTRA_TEXT, "Dear ...," + "")
//            startActivity(Intent.createChooser(Email, "Send Feedback:"))

            val intent = Intent(this, FeedBackActivity::class.java)
            startActivity(intent)
        }

        tv_Add_GPJPForm!!.setOnClickListener {
            checkFormIsAvailble(this, mSPF.getString("id", ""), false)
            /* val intent = Intent(this, ActivityGPJPForm::class.java)
             startActivity(intent)*/
        }
        tv_Download_GPJPForm!!.setOnClickListener {
            checkFormIsAvailble(this, mSPF.getString("id", ""), true)
            /* val intent = Intent(this, ActivityGPJPForm::class.java)
             startActivity(intent)*/
        }

        tv_GPJPForm!!.setOnClickListener {
            /* val intent = Intent(this, ActivityGPJPForm::class.java)
             startActivity(intent)*/
            if (llGPJPForm!!.visibility == View.VISIBLE) {
                llGPJPForm!!.visibility = View.GONE
                ivGPJPForm!!.rotation = 0f
            } else {
                llGPJPForm!!.visibility = View.VISIBLE
                ivGPJPForm!!.rotation = 90f
            }
        }

        tv_st_about_us!!.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }

        tv_st_logout!!.setOnClickListener {
            mEDT.putString("id", "")
            mEDT.commit()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    fun checkFormIsAvailble(context: Context, User_id: String, isForDownload: Boolean) {
        val mURL = context.getString(R.string.server_url) + "Gpjp/check_form_is_done"
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener { s ->

            try {
                val mOBJECT: JSONObject = JSONObject(s)
                if (mOBJECT.getBoolean("success")) {
                    val isData = mOBJECT.getString("data")
                    if (isData != "") {

                        val type = object : TypeToken<ModelGPJPFormDetail>() {
                        }.type
                        UserFormDetail = Gson().fromJson<ModelGPJPFormDetail>(mOBJECT.getString("data"), type)
                        val mData = mOBJECT.getJSONObject("data")
                        val formCounterId = mData.getString("id")

                        Hawk.put(AppConstants.SAVE_COUNTER, formCounterId);

                        if (isForDownload) {
                            if (UserFormDetail.admin_verified == "0") {
                                val intent = Intent(context, ActivityGPJPFormDownload::class.java)
                                intent.putExtra("data", mData.toString())
                                startActivity(intent)
                            } else {
                                Toast.makeText(context, "Admin is not verify yet", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val intent = Intent(context, ActivityGPJPForm::class.java)
                            startActivity(intent)
                        }
                    } else {
                        if (isForDownload) {
                            Toast.makeText(context, "You have not fill form", Toast.LENGTH_SHORT).show()
                        } else {
                            val intent = Intent(context, ActivityGPJPForm::class.java)
                            startActivity(intent)
                        }
                    }
                } else {
                    Toast.makeText(context, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("ErrorsDeletePost", e.toString())
            }
        }, Response.ErrorListener { error -> Toast.makeText(context, error.message, Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val map = java.util.HashMap<String, String>()
                map["user_id"] = User_id
                return map
            }
        }
        queue = Volley.newRequestQueue(context)
        queue.add(req)
        req.retryPolicy = DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
    }


}