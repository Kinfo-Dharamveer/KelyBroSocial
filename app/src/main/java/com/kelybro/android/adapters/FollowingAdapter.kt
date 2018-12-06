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
import com.kelybro.android.*
import com.kelybro.android.activities.UserProfileActivity
import org.json.JSONObject
import java.util.HashMap


/**
 * Created by Krishna on 19-01-2018.
 */
class FollowingAdapter(var YouDatalist: MutableList<MutableList<String>>?, var mc: Context) : RecyclerView.Adapter<FollowingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowingAdapter.ViewHolder {

        val v = LayoutInflater.from(mc).inflate(R.layout.row_following_fragment, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: FollowingAdapter.ViewHolder, position: Int) {
        holder.bindItems(YouDatalist!![position], mc)
    }

    override fun getItemCount(): Int {
        return YouDatalist!!.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var follow_pf_name: TextView? = null
        private var follow_request: TextView? = null
        private var follow_pf_iv: de.hdodenhof.circleimageview.CircleImageView? = null
        private var ll_profile_view: LinearLayout? = null
        private var mSPF: SharedPreferences? = null
        internal lateinit var queue: RequestQueue


        fun bindItems(YouDatalists: MutableList<String>, mcc: Context) {
            mSPF = mcc.getSharedPreferences("AppData", 0)
            follow_pf_iv = itemView.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.you_pf_image) as de.hdodenhof.circleimageview.CircleImageView
            Glide.with(mcc).load(YouDatalists[3]).error(R.drawable.ic_avatar).into(follow_pf_iv)
            follow_pf_name = itemView.findViewById<TextView>(R.id.you_pf_name) as TextView
            follow_pf_name!!.text = YouDatalists[2]
            follow_request = itemView.findViewById<TextView>(R.id.you_follow_request) as TextView
            follow_request!!.text = YouDatalists[6]
            follow_request!!.setBackgroundResource(R.drawable.rounded_layout_sky)


            follow_request!!.setOnClickListener {

                 if (YouDatalists[6].equals("Unfollow")) {
                    Log.e("Krishna", YouDatalists[0])
                    MakeVolleyRequest(mcc, YouDatalists[0], YouDatalists[6], 0)
                    YouDatalists[6] = "Follow"
                } else if (YouDatalists[6].equals("Request")) {
                    Log.e("Krishna", YouDatalists[0])
                    MakeVolleyRequest(mcc, YouDatalists[0], YouDatalists[6], 0)
                    YouDatalists[6] = "Follow"
                } else if (YouDatalists[6].equals("Follow")) {
                    Log.e("Krishna", YouDatalists[0])
                    MakeVolleyRequest(mcc, YouDatalists[0], YouDatalists[6], 1)
                    YouDatalists[6] = "Request"
                }
            }
//                Request
            if (YouDatalists[4].toBoolean())
                follow_pf_iv!!.setBackgroundResource(R.drawable.rounded_image_border_corner)
            ll_profile_view = itemView.findViewById<LinearLayout>(R.id.lyn_main) as LinearLayout
            ll_profile_view!!.setOnClickListener {
                val intent = Intent(mcc, UserProfileActivity::class.java)
                intent.putExtra("UserID", mSPF!!.getString("id", ""))
                intent.putExtra("ProfileID", YouDatalists[1])
                mcc.startActivity(intent)
            }
        }

        private fun MakeVolleyRequest(mcc: Context, Post_id: String, StrinSix: String, Follow_ID: Int) {

            val mURL = mcc.getString(R.string.server_url) + "Users/request_type"
            val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
                try {
                    val mOBJECT: JSONObject = JSONObject(s)
                    if (mOBJECT.getBoolean("success")) {
                        if (Follow_ID == 0)
                            follow_request!!.text = "Follow"
                        if (Follow_ID == 1)
                            follow_request!!.text = "Cancel"
                        Toast.makeText(mcc, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
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