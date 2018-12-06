package com.kelybro.android.adapters

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.RequestQueue
import com.bumptech.glide.Glide
import com.kelybro.android.customviews.mUtitily
import com.kelybro.android.R
import com.kelybro.android.activities.UserProfileActivity
import org.json.JSONObject

/**
 * Created by Krishna on 15-02-2018.
 */
class MyPostBusinessCardAdapter(var BusinessList:  MutableList<MutableList<String>>?, var ImageCardList: MutableList<ArrayList<String>>?, var mc: Context) : RecyclerView.Adapter<MyPostBusinessCardAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPostBusinessCardAdapter.ViewHolder {
        val v = LayoutInflater.from(mc).inflate(R.layout.row_businesscard_list, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: MyPostBusinessCardAdapter.ViewHolder, position: Int) {
        holder.bindItems(BusinessList!![position],ImageCardList!![position], mc)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return BusinessList!!.size
    }


    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var textViewName: TextView? = null
        private var textViewUsername: TextView? = null
        private var tv_post_date: TextView? = null
        private var business_post_text: TextView? = null
        private var profile_image: ImageView? = null

        private var tvAddress: TextView? = null
        private var tvMobileNo: TextView? = null


        private lateinit var viewPager: ViewPager
        private lateinit var sliderDotspanel: LinearLayout
        private var dotscount: Int = 0
        private var dots: Array<ImageView?>? = null
        private var tv_business_name: TextView? = null
        private var tv_business_cat_name: TextView? = null
        private var bc_iv_share: ImageView? = null
        internal lateinit var mOBJECT: JSONObject
        internal lateinit var SOBJECT: JSONObject
        internal lateinit var queue: RequestQueue
        private lateinit var  mUtil:mUtitily
        private lateinit var mSPF: SharedPreferences
        internal lateinit var mEDT: SharedPreferences.Editor
        fun bindItems(CardData: MutableList<String>, CardImage: ArrayList<String>, mcc: Context) {
            mUtil= mUtitily(mcc)
            mSPF = mcc.getSharedPreferences("AppData", 0)
            mEDT = mSPF.edit()
            textViewName = itemView.findViewById<TextView>(R.id.textViewUsername) as TextView
            tv_post_date = itemView.findViewById<TextView>(R.id.tv_post_date) as TextView

            tvAddress = itemView.findViewById(R.id.tvAddress) as TextView
            tvMobileNo = itemView.findViewById(R.id.tvMobileNo) as TextView


            //business_post_text = itemView.findViewById<TextView>(R.id.business_post_text) as TextView
            textViewUsername = itemView.findViewById<TextView>(R.id.textViewUsername) as TextView
            profile_image = itemView.findViewById<ImageView>(R.id.profile_image) as ImageView
            bc_iv_share = itemView.findViewById<ImageView>(R.id.bc_iv_share) as ImageView
            viewPager = itemView.findViewById<ViewPager>(R.id.rh_viewPager) as ViewPager
            tv_business_name = itemView.findViewById<TextView>(R.id.tv_business_name) as TextView
            tv_business_cat_name = itemView.findViewById<TextView>(R.id.tv_business_cat_name) as TextView

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



            tv_post_date!!.text = mUtil.getDateDifference(CardData[13])

            Glide.with(mcc).load(CardData[10]).error(R.drawable.ic_avatar).into(profile_image)
            textViewName!!.text = CardData[9]
            tv_post_date!!.text=mUtil.getDateDifference(CardData[6])

            textViewUsername!!.text = CardData[9]
            business_post_text!!.text=CardData[7]
            tv_business_name!!.text=CardData[2]
            tv_business_cat_name!!.text=CardData[13]

            if (CardData[14] == "1")
                profile_image!!.setBackgroundResource(R.drawable.rounded_image_border_corner)




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


    }
}