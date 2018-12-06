package com.kelybro.android.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.*

import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.kelybro.android.adapters.SelectedPostImage
import com.kelybro.android.customviews.Utility

import org.json.JSONObject

import java.io.ByteArrayOutputStream
import java.util.ArrayList
import java.util.HashMap

import com.android.volley.toolbox.Volley.newRequestQueue
import com.kelybro.android.R
import org.json.JSONArray

/**
 * Created by Krishna on 22-03-2018.
 */

class YourBusinessCardActivity : Activity() {
    internal lateinit var mOBJECT: JSONObject
    private var queue: RequestQueue? = null
    internal var mSPF: SharedPreferences? = null
    internal var mEDT: SharedPreferences.Editor? = null
    private var yp_iv_user_photo: ImageView? = null
    private var edt_user_post_txt: EditText? = null
    private var yp_tv_user_name: TextView? = null
    private var iv_add_image: ImageView? = null
    private var tv_send: TextView? = null
    private var userChoosenTask: String? = null
    private val REQUEST_CAMERA = 0
    private val SELECT_FILE = 1
    private var rv_selected_post_image: RecyclerView? = null
    private var postImageAdapter: SelectedPostImage? = null
    private val PostImages = ArrayList<String>()
    private val PostImagesBitmap = ArrayList<Bitmap>()
    private var pDialog: ProgressDialog? = null
    private var bitmap: Bitmap? = null
    private var category_adapter: ArrayAdapter<String>? = null
    private var sp_bs_categories: Spinner? = null
    var mPosition = 0
    var mCategoryId: String = "0"
    val BusinessCategoryList = ArrayList<String>()

    var mBusinessCaterodyID = ArrayList<String>()

    internal lateinit var mArray: JSONArray

    private var edCountry: TextView? = null
    private var edState: TextView? = null
    private var edCity: TextView? = null
    private var edPhone: TextView? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_your_business_card)
        mSPF = getSharedPreferences("AppData", 0)
        mEDT = mSPF!!.edit()
        yp_iv_user_photo = findViewById(R.id.yp_iv_user_photo)
        edt_user_post_txt = findViewById(R.id.edt_user_post_txt)
        yp_tv_user_name = findViewById(R.id.yp_tv_user_name)
        iv_add_image = findViewById(R.id.iv_add_image)
        tv_send = findViewById(R.id.tv_send)
        rv_selected_post_image = findViewById(R.id.rv_post_image)
        sp_bs_categories = findViewById(R.id.sp_bs_categories)

        edCountry = findViewById(R.id.edCountry)
        edState = findViewById(R.id.edState)
        edCity = findViewById(R.id.edCity)
        edPhone = findViewById(R.id.edPhone)

        BusinessCategoryVolleyRequest()

        // Creating adapter for spinner
        category_adapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, BusinessCategoryList!!)
        // Drop down layout style - list view with radio button
        category_adapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        sp_bs_categories!!.setAdapter(category_adapter)

        sp_bs_categories!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if(mBusinessCaterodyID[position] == "0")
                    mCategoryId=""
                else
                    mCategoryId=mBusinessCaterodyID[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }



        Glide.with(this)
                .load(mSPF!!.getString("profilephoto", ""))
                .error(R.drawable.ic_avatar)
                .into(yp_iv_user_photo!!)

        yp_tv_user_name!!.text = mSPF!!.getString("f_name", " ") + " " + mSPF!!.getString("l_name", "")
        iv_add_image!!.setOnClickListener {
            rv_selected_post_image!!.visibility = View.VISIBLE
            PostImages.clear()
            PostImagesBitmap.clear()
            selectImage()
        }
        tv_send!!.setOnClickListener {
            if (PostImages.size > 0) {
                MakeVolleyNewUserPostRequest()
            } else {
                Toast.makeText(applicationContext, "Select Images.", Toast.LENGTH_SHORT).show()
            }
        }


        val layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
        rv_selected_post_image!!.layoutManager = layoutManager
        postImageAdapter = SelectedPostImage(PostImagesBitmap, applicationContext)
        rv_selected_post_image!!.adapter = postImageAdapter

    }

    private fun BusinessCategoryVolleyRequest() {

        var mURL  = getString(R.string.server_url) + "BusinessCard/get_bussiness_cats"

        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
            Log.e("JobCategoryResponse", s)
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("success")) {
                    if(mOBJECT.getJSONArray("data")!=null) {
                        mArray = mOBJECT.getJSONArray("data")
                        BusinessCategoryList.clear()
                        BusinessCategoryList.add("Select Category")
                        mBusinessCaterodyID.add("0")
                        for (i in 0 until mArray.length()) {
                            val mData = mArray.getJSONObject(i)
                            BusinessCategoryList.add(mData.getString("name"))
                            mBusinessCaterodyID.add(mData.getString("id"))
                        }
                        //dataAdapter!!.notifyDataSetInvalidated()
                        sp_bs_categories!!.setAdapter(category_adapter)
                        category_adapter
                    }
                } else {
                    Toast.makeText(this, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Errors", e.toString())
            }

        }, Response.ErrorListener { error -> Toast.makeText(this, error.message, Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()
                return map
            }
        }
        queue = Volley.newRequestQueue(this)
        queue!!.add(req)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (userChoosenTask == "Take Photo")
                    cameraIntent()
                else if (userChoosenTask == "Choose from Library")
                    galleryIntent()
            } else {
                //code for deny
            }
        }
    }

    private fun selectImage() {
        val items = arrayOf<CharSequence>("Take Photo", "Choose from Library")

        val builder = AlertDialog.Builder(this@YourBusinessCardActivity)
        builder.setTitle("Add Photo!")
        builder.setItems(items) { dialog, item ->
            val result = Utility.checkPermission(this@YourBusinessCardActivity)

            if (items[item] == "Take Photo") {
                userChoosenTask = "Take Photo"
                if (result)
                    cameraIntent()

            } else if (items[item] == "Choose from Library") {
                userChoosenTask = "Choose from Library"
                if (result)
                    galleryIntent()

            }
        }
        builder.show()
    }

    private fun galleryIntent() {
        val mimeTypes = arrayOf("image/jpeg", "image/png")
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE)
    }

    private fun cameraIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CAMERA)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data)
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data)
        }
    }

    private fun onCaptureImageResult(data: Intent) {
        val thumbnail = data.extras!!.get("data") as Bitmap
        val bytes = ByteArrayOutputStream()
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val imageBytes = bytes.toByteArray()
        PostImages.add(Base64.encodeToString(imageBytes, Base64.DEFAULT))
        PostImagesBitmap.add(thumbnail)
        postImageAdapter!!.notifyDataSetChanged()
    }

    private fun onSelectFromGalleryResult(data: Intent) {
        rv_selected_post_image!!.visibility = View.VISIBLE
        val images = ArrayList<Uri>()
        val clipData = data.clipData
        if (clipData != null) {
            for (i in 0 until clipData.itemCount) {
                val item = clipData.getItemAt(i)
                images.add(item.uri)
            }
        } else {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, data.data)
                val baos = ByteArrayOutputStream()
                bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val imageBytes = baos.toByteArray()
                PostImagesBitmap.add(bitmap!!)
                PostImages.add(Base64.encodeToString(imageBytes, Base64.DEFAULT))

            } catch (e: Exception) {
                Log.e("Post Images", e.message)
            }

        }

        for (i in images.indices) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, images.get(i))
                val baos = ByteArrayOutputStream()
                bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val imageBytes = baos.toByteArray()
                PostImagesBitmap.add(bitmap!!)
                PostImages.add(Base64.encodeToString(imageBytes, Base64.DEFAULT))

            } catch (e: Exception) {
                Log.e("Post Images", e.message)
            }

        }
        postImageAdapter!!.notifyDataSetChanged()
    }

    private fun MakeVolleyNewUserPostRequest() {

        showpDialog()
        val mURL = getString(R.string.server_url) + "BusinessCard/registerBusinessvisitingcard"
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener { s ->
            Log.e("NewUserPostResponse", s)
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT!!.getBoolean("success")) {
                    Toast.makeText(applicationContext, mOBJECT!!.getString("message"), Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(applicationContext, mOBJECT!!.getString("message"), Toast.LENGTH_SHORT).show()
                }
                hidepDialog()
            } catch (e: Exception) {
                Log.e("Error", e.toString())
                hidepDialog()
            }
        }, Response.ErrorListener { hidepDialog() }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()
                map["user_id"] = mSPF!!.getString("id", "")
                map["bus_cat"] = mCategoryId
                map["city"] = edCity!!.text.toString()
                map["state"] = edState!!.text.toString()
                map["country"] = edCountry!!.text.toString()
                map["text"] = edt_user_post_txt!!.text.toString()
                map["images"] = PostImages.toString()
                map["contact_no"] = edPhone!!.text.toString()
                return map
            }
        }
        queue = newRequestQueue(applicationContext)
        queue!!.add(req)
        req.retryPolicy = DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

    }

    private fun showpDialog() {
        pDialog = ProgressDialog(this)
        pDialog!!.setMessage("Please wait...")
        pDialog!!.setCancelable(false)
        if (!pDialog!!.isShowing)
            pDialog!!.show()
    }

    private fun hidepDialog() {
        if (pDialog!!.isShowing)
            pDialog!!.dismiss()
    }
}
