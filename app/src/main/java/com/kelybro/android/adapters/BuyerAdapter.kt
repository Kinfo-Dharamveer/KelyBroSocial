package com.kelybro.android.adapters

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.kelybro.android.customviews.mUtitily
import com.kelybro.android.R
import com.kelybro.android.activities.UserProfileActivity
import org.json.JSONObject
import java.util.HashMap

/**
 * Created by Krishna on 23-01-2018.
 */
class BuyerAdapter(var BuyerDatalist: MutableList<MutableList<String>>?, var mc: Context) : RecyclerView.Adapter<BuyerAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyerAdapter.ViewHolder {
        val v = LayoutInflater.from(mc).inflate(R.layout.row_buyer_item, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: BuyerAdapter.ViewHolder, position: Int) {
        holder.bindItems(BuyerDatalist!![position], mc)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return BuyerDatalist!!.size
    }


    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var buyer_name: TextView? = null
        private var buyer_item_name: TextView? = null
        private var buyer_item_description: TextView? = null
        private var buyer_tv_location: TextView? = null
        private var buyer_tv_contact: TextView? = null
        private var buyer_profile_image: de.hdodenhof.circleimageview.CircleImageView? = null
        private var tv_post_date: TextView? = null
        private lateinit var  mUtil:mUtitily
        private lateinit var mSPF: SharedPreferences
        private var iv_post_delete: ImageView? = null
        internal lateinit var mOBJECT: JSONObject
        internal lateinit var queue: RequestQueue
        private var ll_profile_view : LinearLayout?=null


        fun bindItems(BuyingDatalists: MutableList<String>, mcc: Context) {
            mUtil= mUtitily(mcc)
            mSPF = mcc.getSharedPreferences("AppData", 0)
            buyer_name = itemView.findViewById<TextView>(R.id.buyer_name) as TextView
            buyer_item_name = itemView.findViewById<TextView>(R.id.buyer_item_name) as TextView
            buyer_item_description = itemView.findViewById<TextView>(R.id.buyer_item_description) as TextView
            buyer_tv_location = itemView.findViewById<TextView>(R.id.buyer_tv_location) as TextView
            buyer_tv_contact = itemView.findViewById<TextView>(R.id.buyer_tv_contact) as TextView
            buyer_profile_image = itemView.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.buyer_profile_image) as de.hdodenhof.circleimageview.CircleImageView
            iv_post_delete = itemView.findViewById<ImageView>(R.id.iv_post_delete) as ImageView
            tv_post_date = itemView.findViewById<TextView>(R.id.tv_post_date) as TextView
            ll_profile_view = itemView.findViewById<LinearLayout>(R.id.lyn_main) as LinearLayout


            tv_post_date!!.text=mUtil.getDateDifference(BuyingDatalists[11])
            buyer_name!!.text = BuyingDatalists[12]
            buyer_item_name!!.text = BuyingDatalists[3]
            buyer_item_description!!.text = BuyingDatalists[4]
            buyer_tv_location!!.text = BuyingDatalists[9]
            buyer_tv_contact!!.text = BuyingDatalists[8]
            Glide.with(mcc)
                    .load(BuyingDatalists[13])
                    .into(buyer_profile_image)
            if (BuyingDatalists[1]==mSPF.getString("id","")){
                iv_post_delete!!.visibility=View.VISIBLE
                iv_post_delete!!.setOnClickListener {
                    android.app.AlertDialog.Builder(mcc)
                            .setMessage("Are you sure? You want to delete this business card.")
                            .setCancelable(false)
                            .setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                                override fun onClick(dialog: DialogInterface, which: Int) {
                                    DeleteMyPost(mcc,BuyingDatalists[0])
                                }
                            })
                            .setNegativeButton("No", object : DialogInterface.OnClickListener {
                                override fun onClick(dialog: DialogInterface, which: Int) {
                                    dialog.dismiss()
                                }
                            })
                            .show()

                }
            }
            if (BuyingDatalists[15] == "1")
                buyer_profile_image!!.setBackgroundResource(R.drawable.rounded_image_border_corner)

            ll_profile_view!!.setOnClickListener {
                val intent = Intent(mcc, UserProfileActivity::class.java)
                intent.putExtra("UserID", mSPF.getString("id",""))
                intent.putExtra("ProfileID", BuyingDatalists[1])
                mcc.startActivity(intent)
            }
        }
        private fun DeleteMyPost(mcc: Context,Post_id:String) {
            val mURL = mcc.getString(R.string.server_url) + "Posts/delete_posts"
            val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
                Log.e("DeletePostResponse", s)
                try {
                    mOBJECT = JSONObject(s)
                    Toast.makeText(mcc, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Log.e("ErrorsDeletePost", e.toString())
                }
            }, Response.ErrorListener { error -> Toast.makeText(mcc, error.message, Toast.LENGTH_LONG).show() }) {
                override fun getParams(): Map<String, String> {
                    val map = HashMap<String, String>()
                    map["post_id"] = Post_id
                    map["user_id"] = mSPF.getString("id","")
                    map["post_type"] = "3"
                    return map
                }
            }
            queue = Volley.newRequestQueue(mcc)
            queue.add(req)
            req.retryPolicy = DefaultRetryPolicy(10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        }
    }
}