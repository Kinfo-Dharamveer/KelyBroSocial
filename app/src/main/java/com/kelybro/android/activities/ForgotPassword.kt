package com.kelybro.android.activities

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.kelybro.android.R
import org.json.JSONObject
import java.util.HashMap

/**
 * Created by Krishna on 08-02-2018.
 */

class ForgotPassword : Activity() {
    private var fpa_et_email_id: EditText? = null
    private var fpa_tv_forgot_password: TextView? = null
    private var fpa_tv_sign_up: TextView? = null
    private lateinit var mSPF: SharedPreferences
    internal lateinit var mEDT: SharedPreferences.Editor
    internal lateinit var mOBJECT: JSONObject
    internal lateinit var data: JSONObject
    internal lateinit var queue: RequestQueue
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotpassword)
        ReferenceControl()
    }

    private fun ReferenceControl() {
        mSPF = getSharedPreferences("AppData", 0)
        mEDT = mSPF.edit()
        fpa_et_email_id = findViewById<EditText>(R.id.fpa_et_email_id) as EditText
        fpa_tv_forgot_password = findViewById<TextView>(R.id.fpa_tv_forgot_password) as TextView
        fpa_tv_sign_up = findViewById<TextView>(R.id.fpa_tv_sign_up) as TextView

        fpa_tv_sign_up!!.setOnClickListener {
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intent)
        }
        fpa_tv_forgot_password!!.setOnClickListener {
            MakeVolleyRequest()
        }

    }

    private fun MakeVolleyRequest() {
        val mURL = getString(R.string.server_url)+"Users/forgot_password"
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
            Log.e("RegisterResponse", s)
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("status")) {
                    Log.e("ErrorResponse", s)
                    data = mOBJECT.getJSONObject("data")
                    mEDT.putString("email", data.getString("email"))
                    Toast.makeText(this, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()

                    val intent = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(this, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Errors", e.toString())
            }

        }, Response.ErrorListener { error -> Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()

                map["email"] = fpa_et_email_id!!.text.toString()
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
