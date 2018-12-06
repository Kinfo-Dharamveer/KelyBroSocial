package com.kelybro.android.adapters

import android.content.Context
import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.kelybro.android.R
import java.util.ArrayList

/**
 * Created by krishna on 04/04/18.
 */

class SelectedPostImage(var PostImage: ArrayList<Bitmap>?, var mc: Context): RecyclerView.Adapter<SelectedPostImage.ViewHolder>(){
    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedPostImage.ViewHolder {
        val v = LayoutInflater.from(mc).inflate(R.layout.row_selected_post_image, parent, false)
        return SelectedPostImage.ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: SelectedPostImage.ViewHolder, position: Int) {
        holder.bindItems(PostImage!![position], mc)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return PostImage!!.size
    }
    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var ivPhoto: ImageView? = null
        fun bindItems(ImagePath: Bitmap, mcc: Context) {
            ivPhoto = itemView.findViewById<ImageView>(R.id.iv_selected_post_image) as ImageView
            ivPhoto!!.setImageBitmap(ImagePath)
        }
    }
}
