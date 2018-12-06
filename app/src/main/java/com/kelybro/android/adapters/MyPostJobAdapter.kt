package com.kelybro.android.adapters

import android.app.AlertDialog
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
import android.widget.TextView
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.kelybro.android.AppConstants
import com.kelybro.android.activities.CommentActivity
import com.kelybro.android.customviews.mUtitily
import com.kelybro.android.dialogs.ApplyJobDialog
import com.kelybro.android.R
import org.json.JSONObject

/**
 * Created by Krishna on 15-02-2018.
 */
class MyPostJobAdapter(var JobList: MutableList<MutableList<String>>?, var mc: Context) : RecyclerView.Adapter<MyPostJobAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPostJobAdapter.ViewHolder {
        val v = LayoutInflater.from(mc).inflate(R.layout.row_jobpost_list, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: MyPostJobAdapter.ViewHolder, position: Int) {
        holder.bindItems(JobList!![position]/*, ImageJobList!![position]*/, mc)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return JobList!!.size
    }


    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var textViewName: TextView? = null
        private var jp_business_name: TextView? = null
        private var jp_business_category: TextView? = null
        private var jp_job_category: TextView? = null
        private var job_experience: TextView? = null
        private var job_salary: TextView? = null
        private var job_location: TextView? = null
        private var comment_text: TextView? = null
        private var jp_tv_apply: TextView? = null
        private var profile_image: ImageView? = null
        private var iv_like_unlike: ImageView? = null
        private var hm_iv_comment: ImageView? = null
        private var jb_share_link: ImageView? = null
        internal lateinit var mOBJECT: JSONObject
        internal lateinit var SOBJECT: JSONObject
        internal lateinit var queue: RequestQueue
        private lateinit var  mUtil: mUtitily
        private var tv_post_date: TextView? = null
        private lateinit var mSPF: SharedPreferences
        internal lateinit var mEDT: SharedPreferences.Editor

        fun bindItems(JobData: MutableList<String>, mcc: Context) {
            mUtil= mUtitily(mcc)
            mSPF = mcc.getSharedPreferences("AppData", 0)
            mEDT = mSPF.edit()
            textViewName = itemView.findViewById<TextView>(R.id.textViewUsername) as TextView
            tv_post_date = itemView.findViewById<TextView>(R.id.tv_post_date) as TextView
            jp_business_name = itemView.findViewById<TextView>(R.id.jp_business_name) as TextView
            jp_business_category = itemView.findViewById<TextView>(R.id.jp_business_category) as TextView
            jp_job_category = itemView.findViewById<TextView>(R.id.jp_job_category) as TextView
            job_experience = itemView.findViewById<TextView>(R.id.job_experience) as TextView
            job_salary = itemView.findViewById<TextView>(R.id.job_salary) as TextView
            job_location = itemView.findViewById<TextView>(R.id.job_location) as TextView
            jp_tv_apply = itemView.findViewById<TextView>(R.id.jp_tv_apply) as TextView

            comment_text = itemView.findViewById<TextView>(R.id.comment_text) as TextView
            profile_image = itemView.findViewById<ImageView>(R.id.profile_image) as ImageView
            iv_like_unlike = itemView.findViewById<ImageView>(R.id.iv_like_unlike) as ImageView
            jb_share_link = itemView.findViewById<ImageView>(R.id.jb_share_link) as ImageView

            Glide.with(mcc).load(AppConstants.imgUrl + JobData[14]).error(R.drawable.ic_avatar).into(profile_image)
            textViewName!!.text = JobData[13]
            tv_post_date!!.text=mUtil.getDateDifference(JobData[11])

            comment_text!!.text = JobData[5]

            jp_business_name!!.text = JobData[20]
            jp_business_category!!.text = JobData[3]
            jp_job_category!!.text = JobData[4]
            job_experience!!.text = JobData[8]
            job_salary!!.text = JobData[9]
            job_location!!.text = JobData[6]

            if (JobData[19] == "1")
                profile_image!!.setBackgroundResource(R.drawable.rounded_image_border_corner)

            if (JobData[16].toBoolean())
                Glide.with(mcc).load(R.drawable.ic_favorite_black_24dp).into(iv_like_unlike)
            else
                Glide.with(mcc).load(R.drawable.ic_favorite_border_black_24dp).into(iv_like_unlike)

            iv_like_unlike!!.setOnClickListener {
                LikeUnlikeMakeVolleyRequest(mcc,JobData[0])
            }


            hm_iv_comment = itemView.findViewById<ImageView>(R.id.hm_iv_comment)
            hm_iv_comment!!.setOnClickListener {
                val intent = Intent(mcc, CommentActivity::class.java)
                intent.putExtra("PostType", "3")
                mcc.startActivity(intent)
            }


            jb_share_link!!.setOnClickListener {
                val share = Intent(Intent.ACTION_SEND)
                share.type = "text/plain"
                share.putExtra(Intent.EXTRA_SUBJECT, "KelyBro")
                val sAux = JobData[18]
                share.putExtra(Intent.EXTRA_TEXT, sAux)
                mcc.startActivity(Intent.createChooser(share, "choose one"))
            }


            jp_tv_apply!!.setOnClickListener {


                AlertDialog.Builder(mcc)
                        .setMessage("Are you sure you want to Apply this job?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface, which: Int) {
                                if (JobData[17].toBoolean()) {
                                    Toast.makeText(mcc, "Appley", Toast.LENGTH_LONG).show()
                                    val intent = Intent(mcc, ApplyJobDialog::class.java)
                                    mcc.startActivity(intent)
                                } else {
                                    Toast.makeText(mcc, "Failed", Toast.LENGTH_LONG).show()
                                }
//                                val applyJobDialog = ApplyJobDialog(mcc)
//                                applyJobDialog.show()


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

        private fun LikeUnlikeMakeVolleyRequest(mcc: Context,Post_id:String) {
            val mURL = mcc.getString(R.string.server_url) + "Likes/new_like"
            val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
                Log.e("LikeResponse", s)
                try {
                    mOBJECT = JSONObject(s)
                    if (mOBJECT.getBoolean("success")) {
                        Log.e("ErrorResponse", s)
                        SOBJECT = mOBJECT.getJSONObject("data")

//                            val mData = SOBJECT.getJSONObject("data")
                        SOBJECT.getString("post_id")
                        SOBJECT.getString("user_id")
                        SOBJECT.getString("post_type")
                        SOBJECT.getString("status")
//                        mEDT.putBoolean("status",SOBJECT.getBoolean("status"))

                        if (SOBJECT.getBoolean("status")) {
                            Glide.with(mcc).load(R.drawable.ic_favorite_black_24dp).into(iv_like_unlike)
                            Toast.makeText(mcc, "like this post", Toast.LENGTH_LONG).show()
                        } else {
                            Glide.with(mcc).load(R.drawable.ic_favorite_border_black_24dp).into(iv_like_unlike)
                            Toast.makeText(mcc, "Dislike this post", Toast.LENGTH_LONG).show()
                        }

                    } else {
                        Toast.makeText(mcc, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("Errors", e.toString())
                }
            }, Response.ErrorListener { error -> Toast.makeText(mcc, error.message, Toast.LENGTH_LONG).show() }) {
                override fun getParams(): Map<String, String> {
                    val map = HashMap<String, String>()
                    map["post_id"] = Post_id
                    map["user_id"] = mSPF.getString("id","")
                    map["post_type"] = "2"
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