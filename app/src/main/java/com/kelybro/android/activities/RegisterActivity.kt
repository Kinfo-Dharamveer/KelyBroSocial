package com.kelybro.android.activities

import android.app.Activity
import android.content.DialogInterface
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
import com.android.volley.toolbox.Volley.newRequestQueue
import com.google.firebase.messaging.FirebaseMessaging
import com.kelybro.android.AppConstants
import com.kelybro.android.R
import org.json.JSONObject
import java.util.HashMap

/**
 * Created by Krishna on 26-01-2018.
 */
class RegisterActivity : Activity() {

    private var etFirstName: EditText? = null
    private var etLastName: EditText? = null
    private var etEmail: EditText? = null
    private var etPassword: EditText? = null
    private var etConfirmPassword: EditText? = null
    private var tvRegister: TextView? = null
    private var tv_RegisterLogin: TextView? = null
    private lateinit var mSPF: SharedPreferences
    internal lateinit var mEDT: SharedPreferences.Editor
    internal lateinit var mOBJECT: JSONObject
    internal lateinit var data: JSONObject
    internal lateinit var queue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        ReferenceControl()
    }
    private fun ReferenceControl() {
        mSPF = getSharedPreferences("AppData", 0)
        mEDT = mSPF.edit()
        etEmail = findViewById<EditText>(R.id.etEmail) as EditText
        etFirstName = findViewById<EditText>(R.id.etFirstName) as EditText
        etLastName = findViewById<EditText>(R.id.etLastName) as EditText
        etPassword = findViewById<EditText>(R.id.etPassword) as EditText
        etConfirmPassword = findViewById<EditText>(R.id.etConfirm_Password) as EditText
        tvRegister = findViewById<TextView>(R.id.tvRegister) as TextView
        tv_RegisterLogin = findViewById<TextView>(R.id.tv_RegisterLogin) as TextView

        tv_RegisterLogin!!.setOnClickListener{
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        tvRegister!!.setOnClickListener {
            ValidationRegister()
        }

    }
    private fun ValidationRegister() {

        when {
            etFirstName!!.text.toString() == "" -> {
                Toast.makeText(this, "Please Enter Your First Name", Toast.LENGTH_LONG).show()
                return
            }
            etLastName!!.text.toString() == "" -> {
                Toast.makeText(this, "Please Enter Your Last Name", Toast.LENGTH_LONG).show()
                return
            }
            etEmail!!.text.toString() == "" -> {
                Toast.makeText(this, "Please Enter Your Email Id", Toast.LENGTH_LONG).show()
                return
            }
            etPassword!!.text.toString() == "" -> {
                Toast.makeText(this, "Please Enter Your Password", Toast.LENGTH_LONG).show()
                return
            }
            etConfirmPassword!!.text.toString() == "" -> {
                Toast.makeText(this, "Please Enter Your Confirm Password", Toast.LENGTH_LONG).show()
                return
            }
            else -> {
                MakeVolleyRequest()
            }
        }
    }
    private fun MakeVolleyRequest() {
        val mURL = getString(R.string.server_url)+"Users/register"
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
            Log.e("RegisterResponse", s)
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("status")) {
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
                    android.app.AlertDialog.Builder(this)
                            .setMessage("Please verify your email : "+data.getString("email"))
                            .setCancelable(false)
                            .setPositiveButton("OK", object : DialogInterface.OnClickListener {
                                override fun onClick(dialog: DialogInterface, which: Int) {
                                    val intent = Intent(applicationContext, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            })
                            .show()

//                    val intent = Intent(applicationContext, MainActivity::class.java)
//                    startActivity(intent)
//                    finish()
//                    data = mOBJECT.getJSONObject("data")
//                    mEDT.putString("email", data.getString("email"))
//                    mEDT.putString("password", data.getString("password"))
//                    mEDT.putString("device_token", data.getString("device_token"))
//                    mEDT.putString("auth_token", data.getString("auth_token"))
//                    mEDT.commit()
//                    val intent = Intent(applicationContext, LoginActivity::class.java)
//                    startActivity(intent)
//                    finish()
                } else {
                    Toast.makeText(this, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Errors", e.toString())
            }
        }, Response.ErrorListener { error -> Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()
                map["email"] = etEmail!!.text.toString()
                map["password"] = etPassword!!.text.toString()
                map["firstname"] = etFirstName!!.text.toString()
                map["lastname"] = etLastName!!.text.toString()
                map["device_token"] = mSPF.getString("regId","");
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