package com.kelybro.android.activities

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley.newRequestQueue
import com.google.firebase.messaging.FirebaseMessaging
import com.kelybro.android.AppConstants
import com.kelybro.android.R
import org.json.JSONObject
import java.util.HashMap

/**
 * Created by Krishna on 26-01-2018.
 */

class LoginActivity : Activity(){
    private var et_email_phone: EditText? = null
    private var etPassword: EditText? = null
    private var tvLogin: TextView? = null
    private var tv_Register: TextView? = null
    private var tvForgetPassword: TextView? = null
    private lateinit var mSPF: SharedPreferences
    internal lateinit var mEDT: SharedPreferences.Editor
    internal lateinit var mOBJECT: JSONObject
    internal lateinit var data: JSONObject
    internal lateinit var queue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        ReferenceControl()
    }
    private fun ReferenceControl() {
        mSPF = getSharedPreferences("AppData", 0)
        mEDT = mSPF.edit()
        et_email_phone = findViewById<EditText>(R.id.et_email_phone) as EditText
        etPassword = findViewById<EditText>(R.id.etPassword) as EditText
        tvLogin = findViewById<TextView>(R.id.tvLogin) as TextView
        tv_Register = findViewById<TextView>(R.id.tv_Register) as TextView
        tvForgetPassword = findViewById<TextView>(R.id.tvForgetPassword) as TextView

        tvLogin!!.setOnClickListener {

            ValidationLogin()
//            MakeVolleyRequest()

//            val intent = Intent(applicationContext, MainActivity::class.java)
//            startActivity(intent)
//            finish()
        }
        tv_Register!!.setOnClickListener {
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intent)
        }

        tvForgetPassword!!.setOnClickListener {
            val intent = Intent(applicationContext, ForgotPassword::class.java)
            startActivity(intent)
        }

    }
    private fun ValidationLogin() {

        when {
            et_email_phone!!.text.toString() == "" -> {
                Toast.makeText(this, "Please Enter Your Email Id", Toast.LENGTH_LONG).show()
                return
            }
            etPassword!!.text.toString() == "" -> {
                Toast.makeText(this, "Please Enter Your Password", Toast.LENGTH_LONG).show()
                return
            }
            else -> {
                MakeVolleyRequest()
            }
        }
    }
    private fun MakeVolleyRequest() {


        val mURL = getString(R.string.server_url)+"Users/login"
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
            Log.e("LoginResponse", s)
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("success")) {
                    Toast.makeText(this, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                    data = mOBJECT.getJSONObject("data")
                    mEDT.putString("id", data.getString("id"))
                    mEDT.putString("f_name", data.getString("f_name"))
                    mEDT.putString("m_name", data.getString("m_name"))
                    mEDT.putString("l_name", data.getString("l_name"))
                    mEDT.putString("email", data.getString("email"))
                    mEDT.putString("password", data.getString("password"))
                    mEDT.putString("password_confirmation", data.getString("password_confirmation"))
                    mEDT.putString("phone_number", data.getString("phone_number"))
                    mEDT.putString("device_token", data.getString("device_token"))
                    mEDT.putString("auth_token", data.getString("auth_token"))
                    mEDT.putString("status", data.getString("status"))
                    mEDT.putString("birthdate", data.getString("birthdate"))
                    mEDT.putString("gender", data.getString("gender"))
                    mEDT.putString("address", data.getString("address"))
                    mEDT.putString("resume", data.getString("resume"))
                    mEDT.putString("is_vip", data.getString("is_vip"))
                    mEDT.putString("marital_status", data.getString("marital_status"))
                    mEDT.putString("country", data.getString("country"))
                    mEDT.putString("state", data.getString("state"))
                    mEDT.putString("city", data.getString("city"))
                    mEDT.putString("taluka", data.getString("taluka"))
                    mEDT.putString("village", data.getString("village"))
                    mEDT.putString("zip_code", data.getString("zip_code"))
                    mEDT.putString("height", data.getString("height"))
                    mEDT.putString("weight", data.getString("weight"))
                    mEDT.putString("skin_tone", data.getString("skin_tone"))
                    mEDT.putString("physical_status", data.getString("physical_status"))
                    mEDT.putString("income", data.getString("income"))
                    mEDT.putString("total_family_members", data.getString("total_family_members"))
                    mEDT.putString("rent_or_owned", data.getString("rent_or_owned"))
                    mEDT.putString("about_urself", data.getString("about_urself"))
                    mEDT.putString("is_party_registered", data.getString("is_party_registered"))
                    mEDT.putString("membership_id", data.getString("membership_id"))
                    mEDT.putString("political_influence", data.getString("political_influence"))
                    mEDT.putString("profilephoto", AppConstants.imgUrl+data.getString("profilephoto"))
                    mEDT.putString("coverphoto", data.getString("coverphoto"))
                    mEDT.commit()
                    FirebaseMessaging.getInstance().subscribeToTopic("all")
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    }
                 else {
                    Toast.makeText(this, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Errors", e.toString())
            }

        }, Response.ErrorListener { error ->
            Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()
                map["requestParam"] = et_email_phone!!.text.toString()
                map["password"] = etPassword!!.text.toString()
                map["device_token"] = mSPF.getString("regId","")
                return map
            }
        }
        queue = newRequestQueue(this)
        queue.add(req)
        req.retryPolicy = DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
    }
}
