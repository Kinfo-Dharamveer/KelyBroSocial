package com.kelybro.android.adapters

import android.content.Context
import android.content.Intent
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.kelybro.android.R
import com.kelybro.android.activities.ZoomActivity

/**
 * Created by Krishna on 07-04-2018.
 */

class SellitemViewPagerAdapter(private val context: Context, private var PostImages: ArrayList<String>) : PagerAdapter() {
    private var layoutInflater: LayoutInflater? = null
//        private val images = arrayListOf<String>("http://images.all-free-download.com/images/graphiclarge/beautiful_nature_landscape_05_hd_picture_166223.jpg","http://images.all-free-download.com/images/graphiclarge/beautiful_nature_landscape_05_hd_picture_166223.jpg","http://images.all-free-download.com/images/graphiclarge/beautiful_nature_landscape_05_hd_picture_166223.jpg")
//    private val images = arrayListOf<String>(PostImages.get(0))
    override fun getCount(): Int {
        return PostImages.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater!!.inflate(R.layout.custom_layout, null)
        val imageView = view.findViewById<View>(R.id.imageView) as ImageView

//        imageView.setOnTouchListener(ImageMatrixTouchHandler(view.context))

//        Picasso.with(context).load(PostImages[position]).error(R.drawable.ic_avatar).resize(220,220).into(imageView)

//        if (PostImages[position].endsWith(".jpg")||(PostImages[position].endsWith(".png"))) {
        Glide.with(context).load(PostImages[position].replace("main","thumb")).override(220,220) .into(imageView)
//        }else if (PostImages[position].endsWith(".gif")){
//            Glide.with(context).load("http://i.imgur.com/1ALnB2s.gif").asGif().into(imageView)
//        }else if (PostImages[position].endsWith(".mp4")) {
////            Glide.with(context).load("http://i.imgur.com/1ALnB2s.gif").asGif().into(imageView)
//        }

        imageView.setOnClickListener {
            val intent = Intent(context, ZoomActivity::class.java)
            intent.putStringArrayListExtra("Array",PostImages)
            intent.putExtra("ID",position)
            context.startActivity(intent)
        }

        val vp = container as ViewPager
        vp.addView(view, 0)
        return view

    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val vp = container as ViewPager
        val view = `object` as View
        vp.removeView(view)

    }
}