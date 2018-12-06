package com.kelybro.android.adapters

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
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

/**
 * Created by Krishna on 12-01-2018.
 */
class BusinessCardAdapter(var BusinessList: MutableList<MutableList<String>>?, var ImageCardList: MutableList<ArrayList<String>>?, var mc: Context) : RecyclerView.Adapter<BusinessCardAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusinessCardAdapter.ViewHolder {
        val v = LayoutInflater.from(mc).inflate(R.layout.row_businesscard_list, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: BusinessCardAdapter.ViewHolder, position: Int) {
        holder.bindItems(BusinessList!![position], ImageCardList!![position], mc)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return BusinessList!!.size
    }


    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var textViewName: TextView? = null
        private var tv_post_date: TextView? = null


        private var tv_business_name: TextView? = null
        private var tv_business_cat_name: TextView? = null
        private var tvAddress: TextView? = null
        private var tvMobileNo: TextView? = null


        private var lyn_main: LinearLayout? = null
        private var profile_image: ImageView? = null
        private lateinit var viewPager: ViewPager
        private lateinit var sliderDotspanel: LinearLayout
        private var dotscount: Int = 0
        private var dots: Array<ImageView?>? = null
        private var bc_iv_share: ImageView? = null
        internal lateinit var mOBJECT: JSONObject
        internal lateinit var SOBJECT: JSONObject
        internal lateinit var queue: RequestQueue
        private lateinit var mSPF: SharedPreferences
        internal lateinit var mEDT: SharedPreferences.Editor
        private lateinit var mUtil: mUtitily
        private var iv_post_delete: ImageView? = null

        fun bindItems(CardData: MutableList<String>, CardImage: ArrayList<String>, mcc: Context) {
            mSPF = mcc.getSharedPreferences("AppData", 0)
            mEDT = mSPF.edit()
            mUtil = mUtitily(mcc)

            textViewName = itemView.findViewById(R.id.textViewUsername) as TextView
            tv_post_date = itemView.findViewById(R.id.tv_post_date) as TextView

            tv_business_name = itemView.findViewById(R.id.tv_business_name) as TextView
            tv_business_cat_name = itemView.findViewById(R.id.tv_business_cat_name) as TextView
            tvAddress = itemView.findViewById(R.id.tvAddress) as TextView
            tvMobileNo = itemView.findViewById(R.id.tvMobileNo) as TextView


            profile_image = itemView.findViewById(R.id.profile_image) as ImageView
            bc_iv_share = itemView.findViewById(R.id.bc_iv_share) as ImageView
            viewPager = itemView.findViewById(R.id.rh_viewPager) as ViewPager
            iv_post_delete = itemView.findViewById(R.id.iv_post_delete) as ImageView
            lyn_main = itemView.findViewById(R.id.lyn_main) as LinearLayout



            tv_business_name!!.text = CardData[9]
            //tv_business_cat_name!!.text = CardData[5]
            Glide.with(mcc).load(CardData[12]).error(R.drawable.ic_avatar).into(profile_image)
            if (!CardData[10].isNullOrBlank() && !CardData[10].isNullOrEmpty()) {
                textViewName!!.text = CardData[10]
            } else {
                textViewName!!.text = "Admin"
            }
           // business_post_text!!.text = CardData[7]

            tvAddress!!.text = CardData[3]+" ,"+CardData[4]+" ,"+CardData[5]
            tvMobileNo!!.text = CardData[6]

            lyn_main!!.setOnClickListener {
                val intent = Intent(mcc, UserProfileActivity::class.java)
                intent.putExtra("UserID", mSPF.getString("id", ""))
                intent.putExtra("ProfileID", CardData[1])
                mcc.startActivity(intent)
            }

            tv_post_date!!.text = mUtil.getDateDifference(CardData[13])

            if (CardData[1] == mSPF.getString("id", "")) {
                iv_post_delete!!.visibility = View.VISIBLE
                iv_post_delete!!.setOnClickListener {

                    android.app.AlertDialog.Builder(mcc)
                            .setMessage("Are you sure? You want to delete this business card.")
                            .setCancelable(false)
                            .setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                                override fun onClick(dialog: DialogInterface, which: Int) {
                                    DeleteMakeVolleyRequest(mcc, CardData[0])
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

            bc_iv_share!!.setOnClickListener {
                val share = Intent(Intent.ACTION_SEND)
                share.type = "text/plain"
                share.putExtra(Intent.EXTRA_SUBJECT, "Kelybro")
                val sAux = CardData[8]
                share.putExtra(Intent.EXTRA_TEXT, sAux)
                mcc.startActivity(Intent.createChooser(share, "choose one"))
            }


            sliderDotspanel = itemView.findViewById<LinearLayout>(R.id.rh_SliderDots)

            val businessCardViewPagerAdapter = BusinessCardViewPagerAdapter(mcc, CardImage)
            if (CardImage.get(0).length == 0) {
                viewPager.visibility = View.GONE
            }
            viewPager.adapter = businessCardViewPagerAdapter

            dotscount = businessCardViewPagerAdapter.count
            dots = arrayOfNulls(dotscount)

            for (i in 0 until dotscount) {

                dots!![i] = ImageView(mcc)
                dots!![i]!!.setImageDrawable(ContextCompat.getDrawable(mcc, R.drawable.round_non_dots))

                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

                params.setMargins(8, 0, 8, 0)

                sliderDotspanel.addView(dots!![i], params)

            }

            dots!![0]!!.setImageDrawable(ContextCompat.getDrawable(mcc, R.drawable.rounded_dots))

            viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                }

                override fun onPageSelected(position: Int) {

                    for (i in 0 until dotscount) {
                        dots!![i]!!.setImageDrawable(ContextCompat.getDrawable(mcc, R.drawable.round_non_dots))
                    }

                    dots!![position]!!.setImageDrawable(ContextCompat.getDrawable(mcc, R.drawable.rounded_dots))

                }

                override fun onPageScrollStateChanged(state: Int) {

                }
            })

        }


        private fun DeleteMakeVolleyRequest(mcc: Context, Post_id: String) {

            val mURL = mcc.getString(R.string.server_url) + "BusinessCard/deleteVisitingCard"
            val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
                Log.e("DeletePostResponse", s)
                try {
                    mOBJECT = JSONObject(s)
                    if (mOBJECT.getBoolean("success")) {
                        Log.e("ErrorResponse", s)

                        mOBJECT.getBoolean("data")
                        Toast.makeText(mcc, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(mcc, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("ErrorsDeletePost", e.toString())
                }
            }, Response.ErrorListener { error -> Toast.makeText(mcc, error.message, Toast.LENGTH_LONG).show() }) {
                override fun getParams(): Map<String, String> {
                    val map = java.util.HashMap<String, String>()
                    map["post_id"] = Post_id
                    map["user_id"] = mSPF.getString("id", "")
                    map["post_type"] = "0"
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