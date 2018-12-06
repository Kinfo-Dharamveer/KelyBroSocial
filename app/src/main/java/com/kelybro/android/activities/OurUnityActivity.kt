package com.kelybro.android.activities

import android.app.Activity
import android.content.Context
import android.content.CursorLoader
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.*
import com.android.volley.*
import com.android.volley.toolbox.Volley
import com.kelybro.android.R
import com.kelybro.android.model.VolleyMultipartRequest
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.HashMap

/**
 * Created by Krishna on 03-02-2018.
 */

class OurUnityActivity : Activity() {
    private var et_FirstName: EditText? = null
    private var et_Middle_Name: EditText? = null
    private var et_LastName: EditText? = null
    private var tv_date_of_birth: TextView? = null
    private var et_Mobile_No: EditText? = null
    private var et_WhatsApp_no: EditText? = null
    private var et_Email: EditText? = null
    private var et_Address: EditText? = null
    private var et_city_village: EditText? = null
    private var et_Taluka: EditText? = null
    private var et_zip_code: EditText? = null
    private var tv_district: TextView? = null
    private var tv_sub_cast: TextView? = null
    private var tv_gender: TextView? = null
    private var tv_Political_influence: TextView? = null
    private var tv_Marital_status: TextView? = null
    private var spinner_district: Spinner? = null
    private var spinner_subCast: Spinner? = null
    private var spinner_political_influence: Spinner? = null
    private var sp_marital_status: Spinner? = null
    private var et_state: EditText? = null
    private var et_cast: EditText? = null
    private var et_qualification: EditText? = null
    private var tv_browse_image: TextView? = null
    private var tv_submit: TextView? = null
    private var cb_accept_term_condition: CheckBox? = null


    internal lateinit var mOBJECT: JSONObject
    private lateinit var mREQ: RequestQueue
    internal lateinit var filePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_our_unity)

        ReferenceControl()
    }

    private fun ReferenceControl() {
        et_FirstName = findViewById<EditText>(R.id.et_FirstName)as EditText
        et_Middle_Name = findViewById<EditText>(R.id.et_Middle_Name)as EditText
        et_LastName = findViewById<EditText>(R.id.et_LastName)as EditText
        tv_date_of_birth = findViewById<TextView>(R.id.tv_date_of_birth)as TextView
        tv_district = findViewById<TextView>(R.id.tv_district)as TextView
        tv_sub_cast = findViewById<TextView>(R.id.tv_sub_cast)as TextView
        tv_gender = findViewById<TextView>(R.id.tv_gender)as TextView
        tv_Political_influence = findViewById<TextView>(R.id.tv_Political_influence)as TextView
        tv_Marital_status = findViewById<TextView>(R.id.tv_Marital_status)as TextView
        et_Mobile_No = findViewById<EditText>(R.id.et_Mobile_No)as EditText
        et_WhatsApp_no = findViewById<EditText>(R.id.et_WhatsApp_no)as EditText
        et_Email = findViewById<EditText>(R.id.et_Email)as EditText
        et_Address = findViewById<EditText>(R.id.et_Address)as EditText
        et_city_village = findViewById<EditText>(R.id.et_city_village)as EditText
        et_Taluka = findViewById<EditText>(R.id.et_Taluka)as EditText
        et_zip_code = findViewById<EditText>(R.id.et_zip_code)as EditText
        et_state = findViewById<EditText>(R.id.et_state)as EditText
        et_cast = findViewById<EditText>(R.id.et_cast)as EditText
        et_qualification = findViewById<EditText>(R.id.et_qualification)as EditText
        cb_accept_term_condition = findViewById<CheckBox>(R.id.cb_accept_term_condition)as CheckBox
        spinner_district = findViewById<Spinner>(R.id.spinner_district)as Spinner
        spinner_subCast = findViewById<Spinner>(R.id.spinner_subCast)as Spinner
        spinner_political_influence = findViewById<Spinner>(R.id.spinner_political_influence)as Spinner
        sp_marital_status = findViewById<Spinner>(R.id.sp_marital_status)as Spinner
        tv_browse_image = findViewById<TextView>(R.id.tv_browse_image)as TextView
        tv_submit = findViewById<TextView>(R.id.tv_submit)as TextView

        tv_submit!!.setOnClickListener {
            MakeVolleyRequest()
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val picUri = data.data
            filePath = getPath(picUri)
//            mVPEditPhoto!!.setImageURI(picUri)
        }
    }

    private fun getPath(contentUri: Uri): String {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(applicationContext, contentUri, proj, null, null, null)
        val cursor = loader.loadInBackground()
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val result = cursor.getString(column_index)
        cursor.close()
        return result
    }
    private fun MakeVolleyRequest() {
        val mURL = getString(R.string.server_url) + "unity"
        val smr1 = object : VolleyMultipartRequest(Request.Method.POST, mURL, Response.Listener<NetworkResponse> { response ->
            val resultResponse = String(response.data)

            Log.d("Data", resultResponse)
            try {
                mOBJECT = JSONObject(resultResponse)
                if (mOBJECT.getBoolean("success")) {

                    Toast.makeText(applicationContext, "success", Toast.LENGTH_SHORT).show()
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                } else {
                }
            } catch (e: Exception) {
                Log.e("Error", e.toString())
            }
        }, Response.ErrorListener { error -> Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()

//                map["first_name"] = mVPEditFirstname!!.text.toString()
//                map["last_name"] = mVPEditLastName!!.text.toString()
//                map["new_password"] = mVPEditPassword!!.text.toString()
//                map["phone"] = mVPEditContactNo!!.text.toString()
//                map["email"] = mVPEditEmailId!!.text.toString()
                return map
            }


            override fun getByteData(): Map<String, DataPart> {
                val params = HashMap<String, DataPart>()
//                params["picture"] = DataPart(filePath, getFileDataFromDrawable(baseContext, mVPEditPhoto!!.drawable), "image/jpeg")
                return params
            }
        }
        mREQ = Volley.newRequestQueue(applicationContext)
        mREQ.add(smr1)
        smr1.retryPolicy = DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
    }

    private fun getFileDataFromDrawable(context: Context, drawable: Drawable): ByteArray {
        val bitmap = (drawable as BitmapDrawable).bitmap
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)

    }

}
