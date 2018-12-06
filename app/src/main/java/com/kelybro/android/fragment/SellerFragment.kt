package com.kelybro.android.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.kelybro.android.AppConstants
import com.kelybro.android.adapters.SelectedPostImage
import com.kelybro.android.adapters.SellingAdapter
import com.kelybro.android.customviews.Utility
import com.kelybro.android.R
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream

/**
 * Created by Krishna on 23-01-2018.
 */
class SellerFragment:Fragment() {
    private var recyclerView: RecyclerView? = null
    internal lateinit var mOBJECT: JSONObject
    internal lateinit var SOBJECT: JSONObject
    internal lateinit var mArray: JSONArray
    internal lateinit var queue: RequestQueue
    //    private lateinit var staggeredLayoutManager: StaggeredGridLayoutManager
    private var add_seller_button: FloatingActionButton? = null
    private var SellItemDialog: AlertDialog? = null
    private var btn_sl_submit: Button? = null
    private var edt_location: EditText? = null
    private var edt_number: EditText? = null
    private var edt_name: EditText? = null
    private var edt_description: EditText? = null
    private var edt_price: EditText? = null
    private var edt_category: EditText? = null
    private var edt_product_name: EditText? = null
    internal var LimitStart = 0
    internal var maxScroll = 0
    internal var pDialog: ProgressDialog? = null
    internal val SellingList:MutableList<MutableList<String>>?= ArrayList()
    internal val ImagePostData: MutableList<ArrayList<String>>? = ArrayList()
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    var adapter : SellingAdapter?=null
    private lateinit var mSPF: SharedPreferences
    private var rv_selected_post_image: RecyclerView? = null
    private var postImageAdapter: SelectedPostImage? = null
    private val PostImages = java.util.ArrayList<String>()
    private val PostImagesBitmap = java.util.ArrayList<Bitmap>()
    private val SELECT_FILE = 1
    private var bitmap: Bitmap? = null
    private var iv_add_image:ImageView? = null

    private var sp_bs_categories:Spinner? = null
    val BusinessCategoryList = ArrayList<String>()
    val BusinessCategoryListID = ArrayList<String>()
    var dataAdapter: ArrayAdapter<String>?=null
    var mBusinessCaterodyID:String=""

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            if (isResumed && LimitStart == 0) {
                MakeVolleyRequest()
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view: View = inflater!!.inflate(R.layout.fragment_seller, container, false)

        add_seller_button = view.findViewById<FloatingActionButton>(R.id.add_seller_button) as FloatingActionButton
        mSPF = context.getSharedPreferences("AppData", 0)

        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView_seller) as RecyclerView
        var gridLayoutManager = GridLayoutManager(context,2)
        recyclerView!!.layoutManager = gridLayoutManager
        adapter = SellingAdapter(SellingList,ImagePostData,context)
        recyclerView!!.adapter = adapter
        recyclerView!!.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            internal var scrollDy = 0
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                maxScroll = recyclerView!!.computeVerticalScrollRange()
                scrollDy += dy
                val currentScroll = recyclerView!!.computeVerticalScrollOffset() + recyclerView.computeVerticalScrollExtent()
                if (currentScroll >= maxScroll && maxScroll != 0)
                    MakeVolleyRequest()
            }

        })

        pDialog = ProgressDialog(context)
        pDialog!!.setMessage("Please wait...")
        pDialog!!.setCancelable(true)


        LocalBroadcastManager.getInstance(context).registerReceiver(
                mMessageReceiver, IntentFilter("buyerSeller"))


        if (userVisibleHint && LimitStart == 0) {
            MakeVolleyRequest()
        }


        add_seller_button!!.setOnClickListener(View.OnClickListener {

            val view: View = inflater!!.inflate(R.layout.add_seller_item, container,
                    false)
            val dialog = AlertDialog.Builder(context)
            dialog.setView(view)
            SellItemDialog = dialog.show()

            edt_product_name = view.findViewById<EditText>(R.id.edt_product_name) as EditText
            edt_category = view.findViewById<EditText>(R.id.edt_category) as EditText
            edt_price = view.findViewById<EditText>(R.id.edt_price) as EditText
            edt_description = view.findViewById<EditText>(R.id.edt_description) as EditText
            edt_name = view.findViewById<EditText>(R.id.edt_name) as EditText
            edt_number = view.findViewById<EditText>(R.id.edt_number) as EditText
            edt_location = view.findViewById<EditText>(R.id.edt_location) as EditText
            iv_add_image = view.findViewById<ImageView>(R.id.iv_add_image) as ImageView
            iv_add_image!!.setOnClickListener {
                val mimeTypes = arrayOf("image/jpeg", "image/png")
                val intent = Intent()
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE)
            }

            btn_sl_submit = view.findViewById<Button>(R.id.btn_sl_submit) as Button

            btn_sl_submit!!.setOnClickListener {
                if(PostImages.size>0) {
                    SellItemMakeVolleyRequest()
                }else{
                    Toast.makeText(context,"Select Images",Toast.LENGTH_LONG).show()
                }
            }


            dataAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, BusinessCategoryList)
            dataAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sp_bs_categories = view.findViewById<Spinner>(R.id.sp_bs_categories) as Spinner
            sp_bs_categories!!.setAdapter(dataAdapter)
            BusinessCategoryVolleyRequest()
            sp_bs_categories!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    mBusinessCaterodyID = BusinessCategoryListID[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
            rv_selected_post_image = view.findViewById<RecyclerView>(R.id.rv_post_image)
            val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rv_selected_post_image!!.setLayoutManager(layoutManager)
            postImageAdapter = SelectedPostImage(PostImagesBitmap, context)
            rv_selected_post_image!!.setAdapter(postImageAdapter)

        })

        swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout_seller)as SwipeRefreshLayout
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark)
        swipeRefreshLayout.setOnRefreshListener {
            SellingList!!.clear()
            ImagePostData!!.clear()
            LimitStart=0
            MakeVolleyRequest()
            swipeRefreshLayout.isRefreshing = false
        }

        return view
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val mimeTypes = arrayOf("image/jpeg", "image/png")
                val intent = Intent()
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE)
            } else {
                //code for deny
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE){
                onSelectFromGalleryResult(data)
            }
        }
    }

    private fun onSelectFromGalleryResult(data: Intent) {
        rv_selected_post_image!!.setVisibility(View.VISIBLE)
        val images = ArrayList<Uri>()
        val clipData = data.clipData
        if (clipData != null) {
            for (i in 0 until clipData.itemCount) {
                val item = clipData.getItemAt(i)
                images.add(item.uri)
            }
        } else {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), data.data)
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
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), images.get(i))
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

    private val mMessageReceiver = object : BroadcastReceiver(){

        override fun onReceive(p0: Context?, intent: Intent) {

            val filter_product_name = intent.getStringExtra("filter_product_name")
            val filter_product_category = intent.getStringExtra("filter_product_category")
            val filter_product_city = intent.getStringExtra("filter_product_city")

            Log.e("Krishna","Call")
            showpDialog()
            val mURL = getString(R.string.server_url)+"Product/get_product_seller"
            val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
                Log.e("SellerResponse", s)
                try {
                    mOBJECT = JSONObject(s)
                    if (mOBJECT.getBoolean("success")) {
                        mArray = mOBJECT.getJSONArray("data")
                        if(mArray.length()>0){
                            LimitStart += mArray.length()


                            for (i in 0 until mArray.length()) {
                                val mData = mArray.getJSONObject(i)
                                val mylists = java.util.ArrayList<String>()
                                mylists.add(mData.getString("id"))
                                mylists.add(mData.getString("user_id"))
                                mylists.add(mData.getString("type"))
                                mylists.add(mData.getString("product_name"))
                                mylists.add(mData.getString("description"))
                                mylists.add(mData.getString("category"))
                                mylists.add(mData.getString("price"))
                                mylists.add(mData.getString("name"))
                                mylists.add(mData.getString("number"))
                                mylists.add(mData.getString("location"))
                                mylists.add(mData.getString("views"))
                                mylists.add(mData.getString("created_date"))
                                mylists.add(mData.getString("user_name"))
                                mylists.add(AppConstants.imgUrl + mData.getString("user_profile_pic_thumb"))
                                mylists.add(mData.getString("user_profile_pic"))
                                mylists.add(mData.getString("is_vip"))
                                SellingList!!.add(mylists)
                                val mpostimage = mData.getJSONObject("image")
                                val myImglists = java.util.ArrayList<String>()
                                for (i in 0 until (mpostimage.length() / 2)) {
                                    if (mpostimage.getString("main_img_" + i).length > 0) {
                                        myImglists.add(AppConstants.imgUrl + mpostimage.getString("main_img_" + i))
                                    } else {
                                        myImglists.add("")
                                    }
                                }

                                ImagePostData!!.add(myImglists)
                            }
                        }
                        adapter!!.notifyDataSetChanged()
                        if (mOBJECT.getString("message").equals("No result found.")){
                            recyclerView!!.visibility = android.view.View.GONE
                            Toast.makeText(context, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                            hidepDialog()

                        }


                    }
                    else
                    {
                        hidepDialog()
                        recyclerView!!.visibility = android.view.View.GONE
                        Toast.makeText(context, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                    }

                } catch (e: Exception) {
                    Log.e("SellerErrors", e.toString())
                    hidepDialog()
                }

            }, Response.ErrorListener { error ->
                Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
                hidepDialog()
            }) {
                override fun getParams(): Map<String, String> {
                    val map = HashMap<String, String>()

                    //map["user_id"] = mSPF.getString("id", "")
                    map["total"] = "10"
                    map["start"] = LimitStart.toString()
                    map["filter_name"] =filter_product_name
                    map["filter_category"] = filter_product_category
                    map["filter_city"] = filter_product_city
                    return map
                }

            }
            queue = Volley.newRequestQueue(activity)
            queue.add(req)
            req.retryPolicy = DefaultRetryPolicy(10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        }

    }

    private fun MakeVolleyRequest() {

        Log.e("Krishna","Call")
        showpDialog()
        val mURL = getString(R.string.server_url)+"Product/get_product_seller"
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
            Log.e("SellerResponse", s)
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("success")) {
                    mArray = mOBJECT.getJSONArray("data")
                    if(mArray.length()>0){
                        LimitStart += mArray.length()


                        for (i in 0 until mArray.length()) {
                            val mData = mArray.getJSONObject(i)
                            val mylists = java.util.ArrayList<String>()
                            mylists.add(mData.getString("id"))
                            mylists.add(mData.getString("user_id"))
                            mylists.add(mData.getString("type"))
                            mylists.add(mData.getString("product_name"))
                            mylists.add(mData.getString("description"))
                            mylists.add(mData.getString("category"))
                            mylists.add(mData.getString("price"))
                            mylists.add(mData.getString("name"))
                            mylists.add(mData.getString("number"))
                            mylists.add(mData.getString("location"))
                            mylists.add(mData.getString("views"))
                            mylists.add(mData.getString("created_date"))
                            mylists.add(mData.getString("user_name"))
                            mylists.add(AppConstants.imgUrl + mData.getString("user_profile_pic_thumb"))
                            mylists.add(mData.getString("user_profile_pic"))
                            mylists.add(mData.getString("is_vip"))
                            SellingList!!.add(mylists)
                            val mpostimage = mData.getJSONObject("image")
                            val myImglists = java.util.ArrayList<String>()
                            for (i in 0 until (mpostimage.length() / 2)) {
                                if (mpostimage.getString("main_img_" + i).length > 0) {
                                    myImglists.add(AppConstants.imgUrl + mpostimage.getString("main_img_" + i))
                                } else {
                                    myImglists.add("")
                                }
                            }

                            ImagePostData!!.add(myImglists)
                        }
                    }
                    adapter!!.notifyDataSetChanged()
                    hidepDialog()
                } else {
                    hidepDialog()

                    recyclerView!!.visibility = android.view.View.GONE

                    Toast.makeText(context, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()

                }
            } catch (e: Exception) {
                Log.e("SellerErrors", e.toString())
                hidepDialog()
            }

        }, Response.ErrorListener { error -> Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
            hidepDialog()}) {
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()

                //map["user_id"] = mSPF.getString("id", "")
                map["total"] = "10"
                map["start"] = LimitStart.toString()
                map["filter_name"] = mSPF.getString("filter_product_name","")
                map["filter_category"] = mSPF.getString("filter_product_category","")
                map["filter_city"] = mSPF.getString("filter_product_city","")
                return map
            }

        }
        queue = Volley.newRequestQueue(activity)
        queue.add(req)
        req.retryPolicy = DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
    }

    private fun SellItemMakeVolleyRequest() {
        showpDialog()
        val mURL = getString(R.string.server_url)+"Product/new_seller"
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
            Log.e("SellerAddItemResponse", s)
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("success")) {
                    Toast.makeText(context, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                    hidepDialog()
                    PostImages.clear()
                    PostImagesBitmap.clear()
                    postImageAdapter!!.notifyDataSetChanged()
                    SellItemDialog!!.dismiss()


                    SellingList!!.clear()
                    ImagePostData!!.clear()
                    LimitStart=0
                    MakeVolleyRequest()
                } else {
                    hidepDialog()
                    Toast.makeText(context, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                hidepDialog()
                Log.e("Errors", e.toString())
            }

        }, Response.ErrorListener { error -> Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()  }) {
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()

                map["user_id"] = mSPF.getString("id","")
                map["category"] = mBusinessCaterodyID
                map["product_name"] = edt_product_name!!.text.toString()
                map["price"] = edt_price!!.text.toString()
                map["description"] = edt_description!!.text.toString()
                map["name"] = edt_name!!.text.toString()
                map["number"] = edt_number!!.text.toString()
                map["location"] = edt_location!!.text.toString()
                map["images"] = PostImages.toString()
                return map
            }
        }
        queue = Volley.newRequestQueue(activity)
        queue.add(req)
        req.retryPolicy = DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
    }
    private fun showpDialog() {

        if (!pDialog!!.isShowing())
            pDialog!!.show()
    }

    private fun hidepDialog() {
        if (pDialog!!.isShowing())
            pDialog!!.dismiss()
    }

    private fun BusinessCategoryVolleyRequest() {
        val mURL = getString(R.string.server_url) + "Product/get_product_categories"
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
            Log.e("JobCategoryResponse", s)
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("success")) {
                    if(mOBJECT.getJSONArray("data")!=null) {
                        mArray = mOBJECT.getJSONArray("data")
                        BusinessCategoryList!!.clear()
                        for (i in 0 until mArray.length()) {
                            val mData = mArray.getJSONObject(i)
                            BusinessCategoryList!!.add(mData.getString("name"))
                            BusinessCategoryListID!!.add(mData.getString("id"))
                        }
                        //dataAdapter!!.notifyDataSetInvalidated()
                        sp_bs_categories!!.setAdapter(dataAdapter)
                    }
                } else {
                    Toast.makeText(context, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Errors", e.toString())
            }

        }, Response.ErrorListener { error -> Toast.makeText(context, error.message, Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()
                return map
            }
        }
        queue = Volley.newRequestQueue(activity)
        queue.add(req)
        req.retryPolicy = DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
    }
}