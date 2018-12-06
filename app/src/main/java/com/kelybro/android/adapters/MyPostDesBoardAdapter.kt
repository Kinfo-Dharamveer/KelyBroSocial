package com.kelybro.android.adapters

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView
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
import com.bumptech.glide.Glide
import com.kelybro.android.activities.CommentActivity
import com.kelybro.android.customviews.mUtitily
import com.kelybro.android.dialogs.YoutubeVideoDialog
import com.kelybro.android.R
import org.json.JSONObject


/**
 * Created by Krishna on 20-01-2018.
 */
class MyPostDesBoardAdapter(var PostDataList: MutableList<MutableList<String>>?, var ImageDataList: MutableList<ArrayList<String>>?, var mc: Context) : RecyclerView.Adapter<MyPostDesBoardAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPostDesBoardAdapter.ViewHolder {
        val v = LayoutInflater.from(mc).inflate(R.layout.row_my_post_deshboard, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: MyPostDesBoardAdapter.ViewHolder, position: Int) {
        holder.bindItems(PostDataList!![position], ImageDataList!![position], mc)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return PostDataList!!.size
    }


    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var textViewName: TextView? = null
        private var comment_text: TextView? = null
        private var tv_total_like: TextView? = null
        private var tv_total_comment: TextView? = null
        private var tv_post_date: TextView? = null
        private var profile_image: ImageView? = null
        private var iv_like_unlike: ImageView? = null
        private var my_post_iv_share: ImageView? = null
        private lateinit var viewPager: ViewPager
        private lateinit var sliderDotspanel: LinearLayout
        private var dotscount: Int = 0
        private var dots: Array<ImageView?>? = null
        private var hm_iv_comment: ImageView? = null
        internal lateinit var mOBJECT: JSONObject
        internal lateinit var SOBJECT: JSONObject
        internal lateinit var queue: RequestQueue
        private lateinit var  mUtil:mUtitily
        private lateinit var mSPF: SharedPreferences
        internal lateinit var mEDT: SharedPreferences.Editor
        fun bindItems(PostData: MutableList<String>, PostImage: ArrayList<String>, mcc: Context) {
            mSPF = mcc.getSharedPreferences("AppData", 0)
            mEDT = mSPF.edit()

            mUtil= mUtitily(mcc)
            textViewName = itemView.findViewById<TextView>(R.id.textViewUsername) as TextView
            comment_text = itemView.findViewById<TextView>(R.id.comment_text) as TextView
            tv_total_like = itemView.findViewById<TextView>(R.id.tv_total_like) as TextView
            tv_total_comment = itemView.findViewById<TextView>(R.id.tv_total_comment) as TextView
            tv_post_date = itemView.findViewById<TextView>(R.id.tv_post_date) as TextView
            profile_image = itemView.findViewById<ImageView>(R.id.profile_image) as ImageView
            iv_like_unlike = itemView.findViewById<ImageView>(R.id.iv_like_unlike) as ImageView
            hm_iv_comment = itemView.findViewById<ImageView>(R.id.hm_iv_comment) as ImageView
            my_post_iv_share = itemView.findViewById<ImageView>(R.id.hm_iv_share) as ImageView
            viewPager = itemView.findViewById<ViewPager>(R.id.rh_viewPager)
            val rl_youtube_thumb = itemView.findViewById<RelativeLayout>(R.id.rl_youtube_thumb)
            val youtube_thumb = itemView.findViewById<ImageView>(R.id.youtube_thumb)

            Glide.with(mcc).load(PostData[8]).error(R.drawable.ic_avatar).into(profile_image)
            textViewName!!.text = PostData[7]
            tv_post_date!!.text=mUtil.getDateDifference(PostData[2])
            if(PostData[3]!="") {
                comment_text!!.visibility = View.VISIBLE
                comment_text!!.text = PostData[3]
            }else{
                comment_text!!.visibility = View.GONE
            }

            if (PostData[10] == "1")
                profile_image!!.setBackgroundResource(R.drawable.rounded_image_border_corner)
            if (PostData[11].toBoolean()) {
                Glide.with(mcc).load(R.drawable.ic_favorite_black_24dp).into(iv_like_unlike)
                iv_like_unlike!!.setColorFilter(ContextCompat.getColor(mcc, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN)
            }
            else {
                Glide.with(mcc).load(R.drawable.ic_favorite_border_black_24dp).into(iv_like_unlike)
                iv_like_unlike!!.setColorFilter(ContextCompat.getColor(mcc, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN)
            }

            iv_like_unlike!!.setOnClickListener {
                LikeUnlikeMakeVolleyRequest(mcc,PostData[0])
            }

            hm_iv_comment!!.setOnClickListener {
                val intent = Intent(mcc, CommentActivity::class.java)
                intent.putExtra("PostType", "1")
                intent.putExtra("PostId", PostData[0])
                mcc.startActivity(intent)
            }

            my_post_iv_share!!.setOnClickListener {

                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT, PostData[4])
                sendIntent.type = "text/plain"
                mcc.startActivity(sendIntent)
            }
            tv_total_like!!.text = PostData[12]
            tv_total_comment!!.text = PostData[13]
            sliderDotspanel = itemView.findViewById<LinearLayout>(R.id.rh_SliderDots)
            if (PostData[5].equals("PHOTO") || PostData[5].equals("GIF")) {
                viewPager.visibility=View.VISIBLE
                sliderDotspanel.visibility=View.VISIBLE
                rl_youtube_thumb.visibility=View.GONE
                val viewPagerAdapter = HomeViewPagerAdapter(mcc, PostImage)
                viewPager.adapter = viewPagerAdapter
                dotscount = viewPagerAdapter.count
                dots = arrayOfNulls(dotscount)
                sliderDotspanel.removeAllViews()
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

                    override fun onPageScrollStateChanged(state: Int) {

                    }

                    override fun onPageSelected(position: Int) {

                        for (i in 0 until dotscount) {
                            dots!![i]!!.setImageDrawable(ContextCompat.getDrawable(mcc, R.drawable.round_non_dots))
                        }
                        dots!![position]!!.setImageDrawable(ContextCompat.getDrawable(mcc, R.drawable.rounded_dots))
                    }
                })
            }else if(PostData[5].equals("YOUTUBE")){
                viewPager.visibility=View.GONE
                sliderDotspanel.visibility=View.GONE
                rl_youtube_thumb.visibility=View.VISIBLE
                var videoId = PostData[6].replace("https://www.youtube.com/watch?v=","")
                videoId = videoId.replace("https://youtu.be/","")
                Glide.with(mcc).load("https://img.youtube.com/vi/"+videoId+"/maxresdefault.jpg").error(R.drawable.defaultyoutube).into(youtube_thumb)
                rl_youtube_thumb.setOnClickListener {
                    val intent = Intent(mcc, YoutubeVideoDialog::class.java)
                    intent.putExtra("link",PostData[6])
                    mcc.startActivity(intent)
                }
            }else{
                viewPager.visibility=View.GONE
                sliderDotspanel.visibility=View.GONE
                rl_youtube_thumb.visibility=View.GONE
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
                            tv_total_like!!.text = (Integer.parseInt(tv_total_like!!.text.toString())+1).toString()
                            Glide.with(mcc).load(R.drawable.ic_favorite_black_24dp).into(iv_like_unlike)
                            iv_like_unlike!!.setColorFilter(ContextCompat.getColor(mcc, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN)
                        } else {
                            tv_total_like!!.text = (Integer.parseInt(tv_total_like!!.text.toString())-1).toString()
                            Glide.with(mcc).load(R.drawable.ic_favorite_border_black_24dp).into(iv_like_unlike)
                            iv_like_unlike!!.setColorFilter(ContextCompat.getColor(mcc, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN)
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
                    map["user_id"] =mSPF.getString("id","")
                    map["post_type"] = "1"
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