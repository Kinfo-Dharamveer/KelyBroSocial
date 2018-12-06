package com.kelybro.android.adapters

import android.content.*
import android.net.Uri
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
import com.kelybro.android.activities.UserProfileActivity
import com.kelybro.android.eventbus.LikeCountMessage
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import java.util.*

/**
 * Created by Krishna on 09-01-2018.
 */
class HomeAdapter(var PostDataList: MutableList<MutableList<String>>?, var ImageDataList: MutableList<ArrayList<String>>?, var mc: Context,var AdDataList: MutableList<MutableList<String>>?) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {


    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAdapter.ViewHolder {
        val v = LayoutInflater.from(mc).inflate(R.layout.row_home_list, parent, false)

        return ViewHolder(v)
    }


    //this method is binding the data on the list
    override fun onBindViewHolder(holder: HomeAdapter.ViewHolder, position: Int) {
        if (position % 5 == 0 && position != 0) {
            if(position==5)
                holder.bindAd(AdDataList!![0],mc)
            else if(position/5 < AdDataList!!.size)
                holder.bindAd(AdDataList!![position/5],mc)
            else
                holder.bindAd(AdDataList!![0],mc)
        } else{
            holder.bindItems(PostDataList!![position], ImageDataList!![position], mc,position)
        }




    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return PostDataList!!.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var textViewName: TextView? = null
        private lateinit var tv_total_like: TextView
        private lateinit var txtLastComment: TextView
        private lateinit var txtUsername: TextView
        private var tv_post_date: TextView? = null
        private var tv_comment_text: TextView? = null
        private lateinit var tv_total_comment: TextView
        private var profile_image: ImageView? = null
        private var iv_like_unlike: ImageView? = null
        private var iv_post_delete: ImageView? = null
        private lateinit var viewPager: ViewPager
        private lateinit var sliderDotspanel: LinearLayout
        private var dotscount: Int = 0
        private var dots: Array<ImageView?>? = null
        private var hm_iv_comment: ImageView? = null
        private var hm_iv_share: ImageView? = null
        internal lateinit var queue: RequestQueue
        private lateinit var mSPF: SharedPreferences
        internal lateinit var mEDT: SharedPreferences.Editor
        private lateinit var  mUtil:mUtitily


        private var rl_post_view : RelativeLayout?=null
        private var ll_profile_view : LinearLayout?=null
        internal lateinit var mOBJECT: JSONObject
        internal lateinit var SOBJECT: JSONObject


        private var rl_ad_view : RelativeLayout?=null
        private var tvAdDetails : TextView?=null
        private var tvAdBy : TextView?=null
        private var tvAdWithUs : TextView?=null
        private var ivAdImage : ImageView?=null
        private var btnVisit : Button?=null



        fun bindAd(AdData: MutableList<String>,mcc: Context){
            rl_post_view = itemView.findViewById<RelativeLayout>(R.id.post_row) as RelativeLayout
            rl_ad_view = itemView.findViewById<RelativeLayout>(R.id.adView_row) as RelativeLayout
            rl_post_view!!.visibility=View.GONE
            rl_ad_view!!.visibility=View.VISIBLE

            tvAdDetails = itemView.findViewById<TextView>(R.id.tv_ad_description) as TextView
            tvAdBy = itemView.findViewById<TextView>(R.id.tv_adBy) as TextView
            tvAdWithUs = itemView.findViewById<TextView>(R.id.tv_advertise_with_us) as TextView
            ivAdImage = itemView.findViewById<ImageView>(R.id.iv_ad_image) as ImageView
            btnVisit = itemView.findViewById<Button>(R.id.btn_visit) as Button

            tvAdDetails!!.text = AdData.get(2)
            tvAdBy!!.text = AdData.get(1)
            Glide.with(mcc).load(AdData.get(3)).into(ivAdImage)
            btnVisit!!.setOnClickListener {
                var browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(AdData.get(4)))
                mcc.startActivity(browserIntent)
            }
            tvAdWithUs!!.setOnClickListener {
                var browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://kelybro.com/kelybro/subscribe"))
                mcc.startActivity(browserIntent)
            }
        }


        fun bindItems(PostData: MutableList<String>, PostImage: ArrayList<String>, mcc: Context,position:Int) {
            rl_post_view = itemView.findViewById<RelativeLayout>(R.id.post_row) as RelativeLayout
            rl_ad_view = itemView.findViewById<RelativeLayout>(R.id.adView_row) as RelativeLayout
            ll_profile_view = itemView.findViewById<LinearLayout>(R.id.lyn_main) as LinearLayout
            rl_ad_view!!.visibility=View.GONE
            rl_post_view!!.visibility=View.VISIBLE

            ll_profile_view!!.setOnClickListener {
                val intent = Intent(mcc, UserProfileActivity::class.java)
                intent.putExtra("UserID", mSPF.getString("id",""))
                intent.putExtra("ProfileID", PostData[11])
                mcc.startActivity(intent)
            }

            mSPF = mcc.getSharedPreferences("AppData", 0)

            mUtil= mUtitily(mcc)
            textViewName = itemView.findViewById<TextView>(R.id.textViewUsername) as TextView
            tv_total_like = itemView.findViewById<TextView>(R.id.tv_total_like) as TextView
            txtLastComment = itemView.findViewById<TextView>(R.id.txtLastComment) as TextView
            tv_comment_text = itemView.findViewById<TextView>(R.id.comment_text) as TextView
         //   txtUsername = itemView.findViewById<TextView>(R.id.txtUsername) as TextView
            tv_post_date = itemView.findViewById<TextView>(R.id.tv_post_date) as TextView
            tv_total_comment = itemView.findViewById<TextView>(R.id.tv_total_comment) as TextView
            profile_image = itemView.findViewById<ImageView>(R.id.profile_image) as ImageView
            iv_post_delete = itemView.findViewById<ImageView>(R.id.iv_post_delete) as ImageView
            iv_like_unlike = itemView.findViewById<ImageView>(R.id.iv_heart) as ImageView
            hm_iv_comment = itemView.findViewById<ImageView>(R.id.hm_iv_comment) as ImageView
            hm_iv_share = itemView.findViewById<ImageView>(R.id.hm_iv_share) as ImageView
            viewPager = itemView.findViewById<ViewPager>(R.id.rh_viewPager) as ViewPager
            val rl_youtube_thumb = itemView.findViewById<RelativeLayout>(R.id.rl_youtube_thumb)
            val youtube_thumb = itemView.findViewById<ImageView>(R.id.youtube_thumb)

            //Show last comment
            val mURL = mcc.getString(R.string.server_url)+"Comments/getLast_comment"
            val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
                Log.e("CommentResponse", s)
                try {
                    mOBJECT = JSONObject(s)
                    if (mOBJECT.getBoolean("success")) {
                        SOBJECT = mOBJECT.getJSONObject("data")

                        txtLastComment!!.text = SOBJECT.getString("comment");
                       // txtUsername!!.text = SOBJECT.getString("user_name");

                    } else {
                      //  Toast.makeText(mcc, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("Errors", e.toString())
                }

            }, Response.ErrorListener { error ->
              //  Toast.makeText(mcc, error.message, Toast.LENGTH_LONG).show()
              }) {
                override fun getParams(): Map<String, String> {
                    val map = HashMap<String, String>()
                    map["post_id"] = PostData[6]
                    return map
                }

            }
            queue = Volley.newRequestQueue(mcc)
            queue.add(req)
            req.retryPolicy = DefaultRetryPolicy(10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

            textViewName!!.text = PostData[0]
            if(PostData[12]!=""){
                tv_comment_text!!.visibility = View.VISIBLE
                tv_comment_text!!.text =PostData[12]
            }else{
                tv_comment_text!!.visibility = View.GONE
            }
            Glide.with(mcc).load(PostData[1]).error(R.drawable.ic_avatar).into(profile_image)
            tv_post_date!!.text=mUtil.getDateDifference(PostData[10])

            if (PostData[11]==mSPF.getString("id","")){
                iv_post_delete!!.visibility=View.VISIBLE
                iv_post_delete!!.setOnClickListener {
                    android.app.AlertDialog.Builder(mcc)
                            .setMessage("Are you sure? You want to delete this post.")
                            .setCancelable(false)
                            .setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                                override fun onClick(dialog: DialogInterface, which: Int) {
                                    DeleteMakeVolleyRequest(mcc,PostData[6])
                                }
                            })
                            .setNegativeButton("No", object : DialogInterface.OnClickListener {
                                override fun onClick(dialog: DialogInterface, which: Int) {
                                    dialog.dismiss()
                                }
                            })
                            .show()

                }
            }else{
                iv_post_delete!!.visibility=View.GONE
            }


            if (PostData[3] == "1")
                profile_image!!.setBackgroundResource(R.drawable.rounded_image_border_corner)
            if (PostData[4].toBoolean()) {
                Glide.with(mcc).load(R.drawable.ic_favorite_black_24dp).into(iv_like_unlike)
                iv_like_unlike!!.setColorFilter(ContextCompat.getColor(mcc, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN)
            }
            else {
                Glide.with(mcc).load(R.drawable.ic_favorite_border_black_24dp).into(iv_like_unlike)
                iv_like_unlike!!.setColorFilter(ContextCompat.getColor(mcc, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN)
            }


            iv_like_unlike!!.setOnClickListener {
                LikeUnlikeMakeVolleyRequest(mcc,PostData[6])
            }

            hm_iv_comment!!.setOnClickListener {
                val intent = Intent(mcc, CommentActivity::class.java)
                intent.putExtra("PostType", "1")
                intent.putExtra("PostId", PostData[6])
                mcc.startActivity(intent)
            }
            hm_iv_share!!.setOnClickListener {
                val share = Intent(Intent.ACTION_SEND)
                share.type = "text/plain"
                share.putExtra(Intent.EXTRA_SUBJECT, "Kelybro")
                val sAux = PostData[5]
                share.putExtra(Intent.EXTRA_TEXT, sAux)
                mcc.startActivity(Intent.createChooser(share, "choose one"))
            }
            tv_total_like!!.text = PostData[7]
            tv_total_comment!!.text = PostData[8]
            sliderDotspanel = itemView.findViewById<LinearLayout>(R.id.rh_SliderDots)
            if (PostData[13].equals("PHOTO") || PostData[13].equals("GIF")) {
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

                    override fun onPageSelected(position: Int) {

                        for (i in 0 until dotscount) {
                            dots!![i]!!.setImageDrawable(ContextCompat.getDrawable(mcc, R.drawable.round_non_dots))
                        }

                        dots!![position]!!.setImageDrawable(ContextCompat.getDrawable(mcc, R.drawable.rounded_dots))

                    }

                    override fun onPageScrollStateChanged(state: Int) {

                    }
                })
            }else if(PostData[13].equals("YOUTUBE")){
                viewPager.visibility=View.GONE
                sliderDotspanel.visibility=View.GONE
                rl_youtube_thumb.visibility=View.VISIBLE
                var videoId = PostData[9].replace("https://www.youtube.com/watch?v=","")
                videoId = videoId.replace("https://youtu.be/","")
                Glide.with(mcc).load("https://img.youtube.com/vi/"+videoId+"/maxresdefault.jpg").error(R.drawable.defaultyoutube).into(youtube_thumb)
                rl_youtube_thumb.setOnClickListener {
                    val intent = Intent(mcc, YoutubeVideoDialog::class.java)
                    intent.putExtra("link",PostData[9])
                    mcc.startActivity(intent)
                }
            }else{
                viewPager.visibility=View.GONE
                sliderDotspanel.visibility=View.GONE
                rl_youtube_thumb.visibility = View.GONE
            }

        }


        @Subscribe(threadMode = ThreadMode.MAIN)
        fun onEvent(event: LikeCountMessage) {
            if(event.countMessage==0){

                // for comment counting
                val totalComment = tv_total_comment!!.getText()
                val tCmt = Integer.parseInt(totalComment.toString())
                tv_total_comment!!.text = ""+tCmt?.inc()
                iv_like_unlike!!.setColorFilter(R.color.colorPrimaryDark, android.graphics.PorterDuff.Mode.SRC_IN)


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
                      //  Toast.makeText(mcc, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("Errors", e.toString())
                }
            }, Response.ErrorListener { error ->
               // Toast.makeText(mcc, error.message, Toast.LENGTH_LONG).show()
            }) {
                override fun getParams(): Map<String, String> {
                    val map = HashMap<String, String>()
                    map["post_id"] = Post_id
                    map["user_id"] = mSPF.getString("id","")
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
        private fun DeleteMakeVolleyRequest(mcc: Context,Post_id:String) {

            val mURL = mcc.getString(R.string.server_url) + "Posts/delete_posts"
            val req = object : StringRequest(Request.Method.POST, mURL, Response.Listener<String> { s ->
                Log.e("DeletePostResponse", s)
                try {
                    mOBJECT = JSONObject(s)
                    if (mOBJECT.getBoolean("success")) {
                        Log.e("ErrorResponse", s)

                        mOBJECT.getBoolean("data")
                       // Toast.makeText(mcc, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()

                    } else {
                       // Toast.makeText(mcc, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("ErrorsDeletePost", e.toString())
                }
            }, Response.ErrorListener { error ->
                //Toast.makeText(mcc, error.message, Toast.LENGTH_LONG).show()
            }) {
                override fun getParams(): Map<String, String> {
                    val map = HashMap<String, String>()
                    map["post_id"] = Post_id
                    map["user_id"] = mSPF.getString("id","")
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