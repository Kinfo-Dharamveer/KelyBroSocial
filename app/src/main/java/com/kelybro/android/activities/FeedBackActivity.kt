package com.kelybro.android.activities

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.kelybro.android.R
import org.json.JSONObject

/**
 * Created by Krishna on 26-02-2018.
 */

class FeedBackActivity : Activity() {
    internal lateinit var mOBJECT: JSONObject
    internal lateinit var SOBJECT: JSONObject
    internal lateinit var queue: RequestQueue
    private var feedback_edittext: EditText? = null
    private var feedback_submit: Button? = null
    private lateinit var mSPF: SharedPreferences
    internal lateinit var mEDT: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        mSPF = getSharedPreferences("AppData", 0)
        mEDT = mSPF.edit()

        feedback_edittext = findViewById<EditText>(R.id.feedback_edittext) as EditText
        feedback_submit = findViewById<Button>(R.id.feedback_submit) as Button

        feedback_submit!!.setOnClickListener {
            SubmitFeedback()
        }
    }
    private fun SubmitFeedback() {
        val mURL = getString(R.string.server_url)+"Feedback/add_feedback"
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
            Log.e("FeedbackResponse", s)
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("success")) {
                    Log.e("ErrorResponse", s)
                    mOBJECT.getString("id")
                    SOBJECT = mOBJECT.getJSONObject("data")


                    val mylists = java.util.ArrayList<String>()

                    mylists.add(SOBJECT.getString("user_id"))
                    mylists.add(SOBJECT.getString("feedback"))

                    Toast.makeText(this, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, SettingActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(this, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Errors", e.toString())
            }

        }, Response.ErrorListener { error -> Toast.makeText(this, error.message, Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()

                map["user_id"] = mSPF.getString("id","")
                map["feedback"] = feedback_edittext!!.text.toString()

                return map
            }

        }
        queue = Volley.newRequestQueue(this)
        queue.add(req)
        req.retryPolicy = DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
    }

}
