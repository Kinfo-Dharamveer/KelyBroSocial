package com.kelybro.android.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.widget.*
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley.newRequestQueue
import com.kelybro.android.R
import org.json.JSONObject
import java.util.HashMap

/**
 * Created by Krishna on 26-01-2018.
 */
class EditProfileActivity : AppCompatActivity() {
    private var mVPPhoto: ImageView? = null
    private var mVPEditPhoto: ImageView? = null

    private var mVPFirstName: TextView? = null
    private var mVPLastName: TextView? = null
    private var mVPContactNo: TextView? = null
    //    private var mVPEmailId: TextView? = null
//    private var mVPDateOfBirth: TextView? = null
//    private var mVPPassword: TextView? = null
    private var mVPAddress: TextView? = null
    private var mVPCity: TextView? = null
    private var mVPZipCode: TextView? = null
    private var tv_update: TextView? = null


    private var mVPEditFirstname: EditText? = null
    private var mVPEditLastName: EditText? = null
    private var mVPEditContactNo: EditText? = null
    //    private var mVPEditEmailId: EditText? = null
//    private var mVPEditDateOfBirth: EditText? = null
//    private var mVPEditPassword: EditText? = null
    private var mVPEditAddress: EditText? = null
    private var mVPEditCity: EditText? = null
    private var mVPEditZipCode: EditText? = null

    private var mVPEdit: LinearLayout? = null
    private var mVPSaveProfile: LinearLayout? = null

    private var mVPRlProfile: RelativeLayout? = null
    private var mVPRlEditProfile: RelativeLayout? = null

    private var mVPFScrollview: ScrollView? = null
    private var mEPFScrollview: ScrollView? = null

    internal lateinit var mOBJECT: JSONObject
    internal lateinit var SOBJECT: JSONObject
    private lateinit var mREQ: RequestQueue
    private lateinit var mSPF: SharedPreferences
    internal lateinit var mEDT: SharedPreferences.Editor

//    internal lateinit var filePath: String

    var filePath: String = ""
    private var ma_toolbar: Toolbar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        mSPF = getSharedPreferences("AppData", 0)
        mEDT = mSPF.edit()

//        ma_toolbar = findViewById(R.id.ma_toolbar)
//        setSupportActionBar(ma_toolbar)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        supportActionBar!!.title = ""

        ReferenceControl()
        Implementaion()
    }


    private fun ReferenceControl() {
//        mVPPhoto = findViewById<ImageView>(R.id.vpf_UserPhoto)
//        mVPEdit = findViewById<LinearLayout>(R.id.vpf_ll_edit_profile)
//        mVPFirstName = findViewById<TextView>(R.id.vpf_first_name)
//        mVPLastName = findViewById<TextView>(R.id.vpf_last_name)
//        mVPContactNo = findViewById<TextView>(R.id.vpf_contact_no)
//        mVPEmailId = findViewById<TextView>(R.id.vpf_email_address)
//        mVPDateOfBirth = findViewById<TextView>(R.id.vpf_date_of_birth)
//        mVPPassword = findViewById<TextView>(R.id.vpf_password)
//        mVPAddress = findViewById<TextView>(R.id.vpf_address)
//        mVPCity = findViewById<TextView>(R.id.vpf_city)
//        mVPZipCode = findViewById<TextView>(R.id.vpf_zip_code)

//        mVPFScrollview = findViewById<ScrollView>(R.id.vpf_scrollview)
//        mEPFScrollview = findViewById<ScrollView>(R.id.epf_scrollview)


//        mVPRlProfile = findViewById<RelativeLayout>(R.id.vpf_rl_view_profile)
//        mVPRlEditProfile = findViewById<RelativeLayout>(R.id.vpf_rl_view_edit_profile)

//        mVPEditPhoto = findViewById<ImageView>(R.id.UpUserImage)
        mVPEditFirstname = findViewById<EditText>(R.id.up_et_first_name)
        mVPEditLastName = findViewById<EditText>(R.id.up_et_last_name)
        mVPEditContactNo = findViewById<EditText>(R.id.up_et_contact_no)
//        mVPEditEmailId = findViewById<EditText>(R.id.up_et_email_id)
//        mVPEditDateOfBirth = findViewById<EditText>(R.id.up_et_date_of_birth)
//        mVPEditPassword = findViewById<EditText>(R.id.up_et_password)
        mVPEditAddress = findViewById<EditText>(R.id.up_et_address)
        mVPEditCity = findViewById<EditText>(R.id.up_et_city)
        mVPEditZipCode = findViewById<EditText>(R.id.up_et_zipcode)
//        tv_update = findViewById<TextView>(R.id.tv_update)

//        mVPSaveProfile = findViewById<LinearLayout>(R.id.vpf_save_profile)
    }

    private fun Implementaion() {


//        tv_update!!.setOnClickListener {
//
//            MakeVolleyRequest()
//        }
//        Glide.with(applicationContext)
//                .load("http://kelybro.com/kelybro/" + mSPF.getString("profilephoto", ""))
//                .into(mVPPhoto)


//        mVPFirstName!!.text = mSPF.getString("f_name", "")
//        mVPLastName!!.text = mSPF.getString("l_name", "")
//        mVPContactNo!!.text = mSPF.getString("phone_number", "")
//        mVPEmailId!!.text = "test@gmail.com"
//        mVPPassword!!.text = ("123456")
//        mVPPassword!!.text = "Gujrat"
//        mVPAddress!!.text = mSPF.getString("address", "")
//        mVPCity!!.text = mSPF.getString("phone_number", "")
//        mVPZipCode!!.text = mSPF.getString("zip_code", "")


//        mVPEdit!!.setOnClickListener(View.OnClickListener {
//            mVPRlProfile!!.visibility = View.GONE
//            mVPRlEditProfile!!.visibility = View.VISIBLE
//
//            mVPFScrollview!!.visibility = View.INVISIBLE
//            mEPFScrollview!!.visibility = View.VISIBLE
//
//            mVPEditFirstname!!.setText(mSPF.getString("f_name", ""))
//            mVPEditLastName!!.setText(mSPF.getString("l_name", ""))
//            mVPEditContactNo!!.setText(mSPF.getString("phone_number", ""))
////            mVPEditEmailId!!.setText("test@gmail.com")
////            mVPEditPassword!!.setText("123456")
//            mVPEditAddress!!.setText(mSPF.getString("address", ""))
//            mVPEditCity!!.setText(mSPF.getString("city", ""))
//            mVPEditZipCode!!.setText(mSPF.getString("zip_code", ""))
//
//        })

//        Glide.with(applicationContext)
//                .load("http://kelybro.com/kelybro/" + mSPF.getString("profilephoto", ""))
//                .into(mVPEditPhoto)

//        mVPSaveProfile!!.setOnClickListener(View.OnClickListener {
//            mVPRlProfile!!.visibility = View.VISIBLE
//            mVPRlEditProfile!!.visibility = View.GONE
//            mVPFScrollview!!.visibility = View.VISIBLE
//            mEPFScrollview!!.visibility = View.INVISIBLE
//            MakeVolleyRequest()
//        })

//        mVPEditPhoto!!.setOnClickListener(View.OnClickListener {
//            val intent = Intent()
//            intent.type = "image/*"
//            intent.action = Intent.ACTION_PICK
//            startActivityForResult(Intent.createChooser(intent, "Select Image"), 111)
//        })
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 111 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
//            val picUri = data.data
//
//            filePath = getPath(picUri)
//            mVPEditPhoto!!.setImageURI(picUri)
//        }
//    }

//    private fun getPath(contentUri: Uri): String {
//        val proj = arrayOf(MediaStore.Images.Media.DATA)
//        val loader = CursorLoader(applicationContext, contentUri, proj, null, null, null)
//        val cursor = loader.loadInBackground()
//        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//        cursor.moveToFirst()
//        val result = cursor.getString(column_index)
//        cursor.close()
//        return result
//    }

//    private fun MakeVolleyRequest() {
//        val mURL = getString(R.string.server_url) + "Users/update_users_data"
//        val smr1 = object : VolleyMultipartRequest(Request.Method.POST, mURL, Response.Listener<NetworkResponse> { response ->
//            val resultResponse = String(response.data)
//
//            Log.d("UpdateProfileResponse", resultResponse)
//            try {
//                mOBJECT = JSONObject(resultResponse)
//                if (mOBJECT.getBoolean("success")) {
//                    SOBJECT = mOBJECT.getJSONObject("data")
//
//                    mEDT.putString("f_name", SOBJECT.getString("f_name"))
//                    mEDT.putString("l_name", SOBJECT.getString("l_name"))
//                    mEDT.putString("phone_number", SOBJECT.getString("phone_number"))
//                    mEDT.putString("address", SOBJECT.getString("address"))
//                    mEDT.putString("city", SOBJECT.getString("city"))
//                    mEDT.putString("zip_code", SOBJECT.getString("zip_code"))
//                    mEDT.putString("profilephoto", SOBJECT.getString("profilephoto"))
//
//                    Toast.makeText(applicationContext, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
//                    Log.e("errormessage",mOBJECT.getString("message"))
//                    val intent = Intent(applicationContext, MainActivity::class.java)
//                    startActivity(intent)
//                } else {
//                    Toast.makeText(applicationContext, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
//
//                }
//            } catch (e: Exception) {
//                Log.e("Error", e.toString())
//            }
//        }, Response.ErrorListener { error -> Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show() }) {
//            override fun getParams(): Map<String, String> {
//                val map = HashMap<String, String>()
//
//                map["id"] = mSPF.getString("id", "")
//                map["f_name"] = mVPEditFirstname!!.text.toString()
//                map["l_name"] = mVPEditLastName!!.text.toString()
//                map["phone_number"] = mVPEditContactNo!!.text.toString()
//                map["address"] = mVPEditAddress!!.text.toString()
//                map["city"] = mVPEditCity!!.text.toString()
//                map["zip_code"] = mVPEditZipCode!!.text.toString()
//
//                return map
//            }
//
//
//            override fun getByteData(): Map<String, DataPart> {
//                val params = HashMap<String, DataPart>()
//                params["profilephoto"] = DataPart(filePath, getFileDataFromDrawable(baseContext,  mVPEditPhoto!!.getDrawable()), "image/jpeg")
//                return params
//            }
//        }
//        mREQ = newRequestQueue(applicationContext)
//        mREQ.add(smr1)
//        smr1.retryPolicy = DefaultRetryPolicy(10000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
//    }
//
//    private fun getFileDataFromDrawable(context: Context, drawable: Drawable): ByteArray {
//        val bitmap = (drawable as BitmapDrawable).bitmap
//        val byteArrayOutputStream = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
//        return byteArrayOutputStream.toByteArray()
//    }


    private fun MakeVolleyRequest() {
        val mURL = getString(R.string.server_url) + "Users/update_users_data"
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->

            Log.d("Data", s)
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("success")) {

                    SOBJECT = mOBJECT.getJSONObject("data")

                    mEDT.putString("f_name", SOBJECT.getString("f_name"))
                    mEDT.putString("l_name", SOBJECT.getString("l_name"))
                    mEDT.putString("phone_number", SOBJECT.getString("phone_number"))
                    mEDT.putString("address", SOBJECT.getString("address"))
                    mEDT.putString("city", SOBJECT.getString("city"))
                    mEDT.putString("zip_code", SOBJECT.getString("zip_code"))

                    Toast.makeText(applicationContext, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(applicationContext, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Error", e.toString())
            }
        }, Response.ErrorListener { error -> Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()

                map["id"] = mSPF.getString("id", "")
                map["f_name"] = mVPEditFirstname!!.text.toString()
                map["l_name"] = mVPEditLastName!!.text.toString()
                map["phone_number"] = mVPEditContactNo!!.text.toString()
                map["address"] = mVPEditAddress!!.text.toString()
                map["city"] = mVPEditCity!!.text.toString()
                map["zip_code"] = mVPEditZipCode!!.text.toString()

                return map
            }

        }
        mREQ = newRequestQueue(this)
        mREQ.add(req)
        req.retryPolicy = DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
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