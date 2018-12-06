package com.kelybro.android.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import android.widget.*
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.kelybro.android.R
import com.kelybro.android.customviews.Utility
import java.io.*


/**
 * Created by Krishna on 02-02-2018.
 */
class YourPostActivity : Activity() {

    internal lateinit var mOBJECT: JSONObject
    internal lateinit var sOBJECT: JSONObject
    internal lateinit var queue: RequestQueue

    private var yp_iv_user_photo: ImageView? = null
    private var edt_user_post_txt: EditText? = null
    private var yp_tv_user_name: TextView? = null
    private var tv_youtube_link: TextView? = null
    private var you_post_image: ImageView? = null
    private var ll_your_post: LinearLayout? = null
    private var ll_Image_post: LinearLayout? = null
    private var ll_gif_post: LinearLayout? = null
    private var ll_video_post: LinearLayout? = null
    private var tv_send: TextView? = null
    private var userChoosenTask: String? = null
    private val REQUEST_CAMERA = 0
    private val SELECT_FILE = 1

    private lateinit var mSPF: SharedPreferences
    internal lateinit var mEDT: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_your_post)
        mSPF = getSharedPreferences("AppData", 0)
        mEDT = mSPF.edit()
        yp_iv_user_photo = findViewById<View>(R.id.yp_iv_user_photo) as ImageView
        edt_user_post_txt = findViewById<View>(R.id.edt_user_post_txt) as EditText
        yp_tv_user_name = findViewById<View>(R.id.yp_tv_user_name) as TextView
        tv_youtube_link = findViewById<View>(R.id.tv_youtube_link) as TextView
        you_post_image = findViewById<View>(R.id.you_post_image) as ImageView
//      ll_your_post = findViewById<View>(R.id.ll_your_post) as LinearLayout
        ll_Image_post = findViewById<View>(R.id.ll_Image_post) as LinearLayout
        ll_gif_post = findViewById<View>(R.id.ll_gif_post) as LinearLayout
        ll_video_post = findViewById<View>(R.id.ll_video_post) as LinearLayout
        tv_send = findViewById<View>(R.id.tv_send) as TextView

        Glide.with(this)
                .load(mSPF.getString("profilephoto"," "))
                .error(R.drawable.ic_avatar)
                .into(yp_iv_user_photo)

        yp_tv_user_name!!.text = mSPF.getString("f_name"," ")+mSPF.getString("l_name","")

        ll_Image_post!!.setOnClickListener {

            selectImage()

//            val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            startActivityForResult(takePicture, 0)
//
//            val pickPhoto = Intent(Intent.ACTION_PICK,
//                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            startActivityForResult(pickPhoto, 1)//one can be replaced with any action code
        }
        ll_gif_post!!.setOnClickListener {
//            val intent = Intent()
//            intent.type = "image/*"
//            intent.action = Intent.ACTION_GET_CONTENT//
//            startActivityForResult(Intent.createChooser(intent, "Select File"), 104)
            val intent = Intent()
            intent.type = "image/gif"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 106)
        }
        ll_video_post!!.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/")))
        }

        tv_send!!.setOnClickListener {
            MakeVolleyNewUserPostRequest()
        }
//        ll_your_post!!.setOnClickListener {
//            val intent = Intent(this@YourPostActivity, MultipleImagePost::class.java)
//            startActivity(intent)
//        }

        //        you_post_image.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //               Intent intent=new Intent(YourPostActivity.this,MultipleImagePost.class);
        //               startActivity(intent);
        //
        ////                Intent intent = new Intent();
        ////                intent.setType("image/*");
        ////                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        ////                intent.setAction(Intent.ACTION_GET_CONTENT);
        ////                startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);
        //
        //            }
        //        });

        val extras = intent.extras
        if (extras != null) {
            val value1 = extras.getString(Intent.EXTRA_TEXT)
            Log.e("youtubestring", value1)

            tv_youtube_link!!.text = value1

            tv_youtube_link!!.setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=Hxy8BZGQ5Jo"))) }

        }
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
        val items = arrayOf<CharSequence>("Take Photo", "Choose from Library", "Cancel")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Photo!")
        builder.setItems(items) { dialog, item ->
            val result = Utility.checkPermission(this)

            if (items[item] == "Take Photo") {
                userChoosenTask = "Take Photo"
                if (result)
                    cameraIntent()

            } else if (items[item] == "Choose from Library") {
                userChoosenTask = "Choose from Library"
                if (result)
                    galleryIntent()

            } else if (items[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun galleryIntent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT//
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
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes)

        val destination = File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis().toString() + ".jpg")

        val fo: FileOutputStream
        try {
            destination.createNewFile()
            fo = FileOutputStream(destination)
            fo.write(bytes.toByteArray())
            fo.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        you_post_image!!.setImageBitmap(thumbnail)
    }

    private fun onSelectFromGalleryResult(data: Intent?) {

        var bm: Bitmap? = null
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(applicationContext.contentResolver, data.data)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        you_post_image!!.setImageBitmap(bm)
    }

    private fun MakeVolleyNewUserPostRequest() {
        val mURL = getString(R.string.server_url)+"Users/new_userpost"
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
            Log.e("NewUserPostResponse", s)
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("success")) {
                    Log.e("ErrorResponse", s)

                    mOBJECT.getString("id")

                    sOBJECT = mOBJECT.getJSONObject("data")

                    val mylists = java.util.ArrayList<String>()
                    mylists.add(sOBJECT.getString("post_text"))
                    mylists.add(sOBJECT.getString("post_image"))
                    mylists.add(sOBJECT.getString("video_link"))
                    mylists.add(sOBJECT.getString("user_id"))

//                        PostData!!.add(mylists)

//                    val adapter = HomeAdapter(PostData/*,ImagePostData*/,context)
//                    recyclerView!!.adapter = adapter
                } else {
                    Toast.makeText(this, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Errors", e.toString())
            }

        }, Response.ErrorListener { error -> Toast.makeText(this, error.message, Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()

//                val encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT)

//                val bm = BitmapFactory.decodeFile("/path/to/image.jpg")
//                val baos = ByteArrayOutputStream()
//                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos) //bm is the bitmap object
//                val b = baos.toByteArray()

                map["user_id"] = mSPF.getString("id","")
                map["images"] = "http://2.bp.blogspot.com/-AVUSC1Ttyug/UsEuvS7oN9I/AAAAAAAADWI/CdACKvdxfwc/s1600/natural+scene.jpg"
                map["post_text"] = edt_user_post_txt!!.text.toString()
                map["video_link"] = tv_youtube_link.toString()

                return map
            }

        }
        queue = Volley.newRequestQueue(this)
        queue.add(req)
        req.retryPolicy = DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
    }

    private fun MakeVolleyRequest() {
        val mURL = getString(R.string.server_url)+"Image/new_image"
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
            Log.e("ImageUploadResponse", s)
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("success")) {
                    Log.e("ErrorResponse", s)

                    mOBJECT.getString("id")

                    sOBJECT = mOBJECT.getJSONObject("data")

                        val mylists = java.util.ArrayList<String>()
                        mylists.add(sOBJECT.getString("thumb_img"))
                        mylists.add(sOBJECT.getString("main_img"))
                        mylists.add(sOBJECT.getString("user_id"))
                        mylists.add(sOBJECT.getString("post_id"))
                        mylists.add(sOBJECT.getString("post_type"))

//                        PostData!!.add(mylists)

//                    val adapter = HomeAdapter(PostData/*,ImagePostData*/,context)
//                    recyclerView!!.adapter = adapter
                } else {
                    Toast.makeText(this, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Errors", e.toString())
            }

        }, Response.ErrorListener { error -> Toast.makeText(this, error.message, Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()

//              map["user_id"] = mSPF.getString("id","")
                map["user_id"] = "2872"
                map["post_id"] = "479"
                map["post_type"] = "1"
                map["image"] = "1"
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
