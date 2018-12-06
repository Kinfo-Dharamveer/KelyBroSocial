package com.kelybro.android.adapters

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.kelybro.android.R
import com.kelybro.android.activities.UserProfileActivity
import org.json.JSONObject
import java.util.HashMap

/**
 * Created by Krishna on 19-01-2018.
 */
class YouAdapter(var YouDatalist: MutableList<MutableList<String>>?, var mc: Context) : RecyclerView.Adapter<YouAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YouAdapter.ViewHolder {
        val v = LayoutInflater.from(mc).inflate(R.layout.row_you_fragment, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: YouAdapter.ViewHolder, position: Int) {
        holder.bindItems(YouDatalist!![position], mc)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return YouDatalist!!.size
    }


    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var you_pf_name: TextView? = null
        private var you_follow_request: TextView? = null
        private var you_pf_image: de.hdodenhof.circleimageview.CircleImageView? = null
        private var ll_profile_view: LinearLayout? = null
        private var mSPF: SharedPreferences? = null
        internal lateinit var queue: RequestQueue

        fun bindItems(YouDatalists: MutableList<String>, mcc: Context) {
            mSPF = mcc.getSharedPreferences("AppData", 0)
            you_pf_name = itemView.findViewById<TextView>(R.id.you_pf_name) as TextView
            you_follow_request = itemView.findViewById<TextView>(R.id.you_follow_request) as TextView
            you_pf_image = itemView.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.you_pf_image) as de.hdodenhof.circleimageview.CircleImageView
            you_pf_name!!.text = YouDatalists[2]
            Glide.with(mcc).load(YouDatalists[3]).error(R.drawable.ic_avatar).into(you_pf_image)
            you_follow_request!!.text = YouDatalists[6]
            you_follow_request!!.setBackgroundResource(R.drawable.rounded_layout_sky)


            you_follow_request!!.setOnClickListener {
                if (YouDatalists[6].equals("Accept")) {
                    MakeVolleyRequest(mcc, YouDatalists[0], YouDatalists[6], 2)
                    YouDatalists[6] = "Block"
                } else if (YouDatalists[6] == "Block") {
                    MakeVolleyRequest(mcc, YouDatalists[0], YouDatalists[6], 4)
                    YouDatalists[6] = "Unblock"
                } else if (YouDatalists[6] == "Unblock") {
                    MakeVolleyRequest(mcc, YouDatalists[0], YouDatalists[6], 2)
                    YouDatalists[6] = "Block"
                }
            }

            if (YouDatalists[4].toBoolean())
                you_pf_image!!.setBackgroundResource(R.drawable.rounded_image_border_corner)

            ll_profile_view = itemView.findViewById<LinearLayout>(R.id.lyn_main) as LinearLayout
            ll_profile_view!!.setOnClickListener {
                val intent = Intent(mcc, UserProfileActivity::class.java)
                intent.putExtra("UserID", mSPF!!.getString("id", ""))
                intent.putExtra("ProfileID", YouDatalists[1])
                mcc.startActivity(intent)
            }

        }

        private fun MakeVolleyRequest(mcc: Context, Post_id: String, YourData: String, Follow_ID: Int) {

            val mURL = mcc.getString(R.string.server_url) + "Users/request_type"
            val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
                try {
                    val mOBJECT: JSONObject = JSONObject(s)
                    if (mOBJECT.getBoolean("success")) {
                        Toast.makeText(mcc, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                        if (YourData.equals("Accept")) {
                            you_follow_request!!.text = "Block"
                        } else if (YourData == "Block") {
                            you_follow_request!!.text = "Unblock"
                        } else if (YourData == "Unblock") {
                            you_follow_request!!.text = "Block"
                        }
                    } else {
                        Toast.makeText(mcc, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("ErrorsDeletePost", e.toString())
                }
            }, Response.ErrorListener { error -> Toast.makeText(mcc, error.message, Toast.LENGTH_LONG).show() }) {
                override fun getParams(): Map<String, String> {
                    val map = HashMap<String, String>()
                    map["id"] = Post_id
                    map["follow_type"] = Follow_ID.toString()
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