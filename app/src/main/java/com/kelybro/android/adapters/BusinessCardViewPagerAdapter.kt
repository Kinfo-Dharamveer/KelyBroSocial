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
 * Created by Krishna on 10-01-2018.
 */
class BusinessCardViewPagerAdapter(private val context: Context, private var CardImages: ArrayList<String>) : PagerAdapter() {
    private var layoutInflater: LayoutInflater? = null
//    private val images = arrayOf("http://images.all-free-download.com/images/graphiclarge/beautiful_nature_landscape_05_hd_picture_166223.jpg","http://images.all-free-download.com/images/graphiclarge/beautiful_nature_landscape_05_hd_picture_166223.jpg","http://images.all-free-download.com/images/graphiclarge/beautiful_nature_landscape_05_hd_picture_166223.jpg")
//    private val images = arrayListOf<String>(user.image,user.image1,user.image2)
    override fun getCount(): Int {
        return CardImages.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater!!.inflate(R.layout.business_card_imagelayout, null)
        val imageView = view.findViewById<View>(R.id.imageViewBusiness) as ImageView
        Glide.with(context).load(CardImages[position].replace("main","thumb")).override(300,150) .into(imageView)

        imageView.setOnClickListener {
            val intent = Intent(context, ZoomActivity::class.java)
            intent.putStringArrayListExtra("Array",CardImages)
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
