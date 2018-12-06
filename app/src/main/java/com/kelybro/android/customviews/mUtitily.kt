package com.kelybro.android.customviews

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request

import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.kelybro.android.R
import org.json.JSONArray
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Krishna on 16-02-2018.
 */

class mUtitily(private val mContext: Context) {
    private val mSPF: SharedPreferences
    private val mEDT: SharedPreferences.Editor
    internal lateinit var queue: RequestQueue
    internal lateinit var mOBJECT: JSONObject
    internal lateinit var mArray: JSONArray
    internal val CityDataList:MutableList<MutableList<String>>?= ArrayList()
    internal val StatesDataList:MutableList<MutableList<String>>?= ArrayList()
    internal val CountriesDataList:MutableList<MutableList<String>>?= ArrayList()
    var mUserID = ""
    var mProfileID = ""
    var mVideo = 0

    init {
        mSPF = mContext.getSharedPreferences("MyAppData", 0)
        mEDT = mSPF.edit()
    }

    fun MakeVollyRequestCities() {
        val mURL = mContext.getString(R.string.server_url)+"Comman/cities"
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
            Log.e("CityResponse", s)
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("success")) {
                    Log.e("ErrorResponse", s)
                    mArray = mOBJECT.getJSONArray("data")


                    for (i in 0 until mArray.length()) {

                        val mData = mArray.getJSONObject(i)
                        val mylists = java.util.ArrayList<String>()
                        mylists.add(mData.getString("cityid"))
                        mylists.add(mData.getString("countryid"))
                        mylists.add(mData.getString("stateid"))
                        mylists.add(mData.getString("cityname"))

                        CityDataList!!.add(mylists)

//
                    }
//                    val adapter = HomeAdapter(PostData,ImagePostData,context)
//                    recyclerView!!.adapter = adapter
                } else {
                    Toast.makeText(mContext, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Errors", e.toString())
            }

        }, Response.ErrorListener { error -> Toast.makeText(mContext, error.message, Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()

//                map["user_id"] = mSPF.getString("id","")
//                map["post_type"] = "1"

                return map
            }

        }
        queue = Volley.newRequestQueue(mContext)
        queue.add(req)
        req.retryPolicy = DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

    }


    fun MakeVollyRequeststates() {
        val mURL = mContext.getString(R.string.server_url)+"Comman/states"
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
            Log.e("StateResponse", s)
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("success")) {
                    Log.e("ErrorResponse", s)
                    mArray = mOBJECT.getJSONArray("data")


                    for (i in 0 until mArray.length()) {

                        val mData = mArray.getJSONObject(i)
                        val mylists = java.util.ArrayList<String>()
                        mylists.add(mData.getString("stateid"))
                        mylists.add(mData.getString("countryid"))
                        mylists.add(mData.getString("statename"))


                        StatesDataList!!.add(mylists)

//
                    }
//                    val adapter = HomeAdapter(PostData,ImagePostData,context)
//                    recyclerView!!.adapter = adapter
                } else {
                    Toast.makeText(mContext, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Errors", e.toString())
            }

        }, Response.ErrorListener { error -> Toast.makeText(mContext, error.message, Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()

//                map["user_id"] = mSPF.getString("id","")
//                map["post_type"] = "1"

                return map
            }

        }
        queue = Volley.newRequestQueue(mContext)
        queue.add(req)
        req.retryPolicy = DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

    }
    fun MakeVollyRequestCountries() {
        val mURL = mContext.getString(R.string.server_url)+"Comman/countries"
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
            Log.e("StateResponse", s)
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("success")) {
                    Log.e("ErrorResponse", s)
                    mArray = mOBJECT.getJSONArray("data")


                    for (i in 0 until mArray.length()) {

                        val mData = mArray.getJSONObject(i)
                        val mylists = java.util.ArrayList<String>()
                        mylists.add(mData.getString("countryid"))
                        mylists.add(mData.getString("countryname"))

                        CountriesDataList!!.add(mylists)

//
                    }
//                    val adapter = HomeAdapter(PostData,ImagePostData,context)
//                    recyclerView!!.adapter = adapter
                } else {
                    Toast.makeText(mContext, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Errors", e.toString())
            }

        }, Response.ErrorListener { error -> Toast.makeText(mContext, error.message, Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()

//                map["user_id"] = mSPF.getString("id","")
//                map["post_type"] = "1"

                return map
            }

        }
        queue = Volley.newRequestQueue(mContext)
        queue.add(req)
        req.retryPolicy = DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
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
    fun getNotificationDateDifference(thenDate: String): String {

        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

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
}
