package com.kelybro.android.adapters

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.kelybro.android.model.ModelNotificationDetail

import org.json.JSONObject

import java.util.ArrayList
import java.util.HashMap

import com.android.volley.toolbox.Volley.newRequestQueue
import com.kelybro.android.*
import com.kelybro.android.activities.*

/**
 * Created by Krishna on 19-05-2017.
 */

class MyNotificationAdapter(internal var mC: Context, internal var mRequest: ArrayList<ModelNotificationDetail>, internal var mRequestDate: ArrayList<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    internal var mSPF: SharedPreferences
    internal lateinit var mOBJECT: JSONObject
    internal lateinit var SOBJECT: JSONObject
    internal lateinit var queue: RequestQueue

    init {
        mSPF = mC.getSharedPreferences("AppData", 0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(mC).inflate(R.layout.row_notification, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mCVH = holder as CardViewHolder
        mCVH.tvRequest.text = mRequest[position].message
        mCVH.tvDate.text = "As On : " + mRequestDate[position]
        mCVH.card_view.setOnClickListener {
            if (mRequest[position].type == "NEW_FOLLOWING") {
                val intent = Intent(mC, FollowingYouActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra("CurrentTab", 0)
                intent.putExtra("ProfileID", mSPF.getString("id", ""))
                mC.startActivity(intent)
            } else if (mRequest[position].type == "POST_COMMENT") {
                val myPrefs = PreferenceManager.getDefaultSharedPreferences(mC)
                val post_id = myPrefs.getString("post_id", "0")
                MakeVolleyRequest(post_id)
            }
            else if(mRequest[position].type == "Like"){
                val myPrefs = PreferenceManager.getDefaultSharedPreferences(mC)
                val post_id = myPrefs.getString("post_id", "0")
                MakeVolleyRequest(post_id)
            }
            else if(mRequest[position].title == "Job Notification"){
                //val intent = Intent(mC, FollowingYouActivity::class.java)
               // mC.startActivity(intent)

            }
            else if(mRequest[position].title == "Job Notification"){
             //   val intent = Intent(mC, FollowingYouActivity::class.java)
                //mC.startActivity(intent)

            }
            else if(mRequest[position].type == "Resume_Unverified"){
                //   val intent = Intent(mC, FollowingYouActivity::class.java)
                //mC.startActivity(intent)
            }
            else if(mRequest[position].type == "Resume_Verified"){
                //   val intent = Intent(mC, FollowingYouActivity::class.java)
                //mC.startActivity(intent)
            }
            else if(mRequest[position].type == "GPJP Notification"){
                   val intent = Intent(mC, ActivityGPJPFormDownload::class.java)
                mC.startActivity(intent)
            }
            else if(mRequest[position].type == "GPJP Notification"){
                   val intent = Intent(mC, ActivityGPJPForm::class.java)
                mC.startActivity(intent)
            }
            else if(mRequest[position].type == "Chat"){
                val intent = Intent(mC, ChatActivity::class.java)
                mC.startActivity(intent)
            }


        }
    }

    private fun MakeVolleyRequest(post_id: String) {
        val mURL = mC.getString(R.string.server_url) + "Comments/open_comment"
        val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
            Log.e("HomeResponse", s)
            try {
                mOBJECT = JSONObject(s)
                if (mOBJECT.getBoolean("success")) {
                    SOBJECT = mOBJECT.getJSONObject("data")



                   // for (i in 1 until SOBJECT.length()) {
                      //  val mData = SOBJECT.getJSONObject(i.toString())
                        //val mylists = java.util.ArrayList<String>()
                      //  mylists.add(mData.getString("id"))
                       // mylists.add(mData.getString("user_id"))
                        val user_name = SOBJECT.getString("user_name")
                        val user_profile_pic_thumb = SOBJECT.getString("user_profile_pic_thumb")
                        //val user_profile_pic = mylists.add(mData.getString("user_profile_pic"))
                        val total_likes = SOBJECT.getString("total_likes")
                        val total_comments = SOBJECT.getString("total_comments")
                        val created_date = SOBJECT.getString("created_date")



                        val myImglists = ArrayList<String>()
                        if (SOBJECT.getString("post_image") !== "") {
                            val mpostimage = SOBJECT.getJSONObject("post_image")
                            for (i in 0..mpostimage.length()) {
                                myImglists.add(AppConstants.imgUrl + mpostimage.getString("main_img_" + 0))
                            }
                        }

                        val intentComment = Intent(mC, PostActivity::class.java)
                        intentComment.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        intentComment.putExtra("user_name", user_name)
                        intentComment.putExtra("user_profile_pic_thumb", user_profile_pic_thumb)
                        intentComment.putExtra("total_likes", total_likes)
                        intentComment.putExtra("total_comments", total_comments)
                        intentComment.putExtra("created_date", created_date)
                        intentComment.putExtra("post_image", myImglists.get(1))
                        intentComment.putExtra("post_id", post_id)
                        mC.startActivity(intentComment)




                  //  }
                } else {
                    Toast.makeText(mC, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Errors", e.toString())
            }

        }, Response.ErrorListener { error -> Toast.makeText(mC, error.message, Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()
                map["post_id"] = post_id
                return map
            }
        }
        queue = newRequestQueue(mC)
        queue.add(req)
        req.retryPolicy = DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
    }



    override fun getItemCount(): Int {
        return mRequest.size
    }

    inner class CardViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var tvID: TextView? = null
        internal var tvRequest: TextView
        internal var tvDate: TextView
        internal var card_view: CardView

        init {
            tvRequest = view.findViewById<View>(R.id.tvRequest) as TextView
            tvDate = view.findViewById<View>(R.id.tvRequestDate) as TextView
            card_view = view.findViewById<View>(R.id.card_view) as CardView
        }
    }
}