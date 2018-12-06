package com.kelybro.android.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.kelybro.android.dialogs.SellingItemDialog
import com.kelybro.android.R

/**
 * Created by Krishna on 23-01-2018.
 */
class SellingAdapter(var SellingDatalist: MutableList<MutableList<String>>?, var ImageList: MutableList<ArrayList<String>>?,var mc: Context) : RecyclerView.Adapter<SellingAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellingAdapter.ViewHolder {
//        val v = LayoutInflater.from(mc).inflate(R.layout.row_seller_item, parent, false)
        val v = LayoutInflater.from(mc).inflate(R.layout.row_seller_item, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: SellingAdapter.ViewHolder, position: Int) {
        holder.bindItems(SellingDatalist!![position],ImageList!![position], mc)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return SellingDatalist!!.size
    }


    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var iv_seller_image: ImageView? = null
        private var tv_seller_price: TextView? = null
        private var seller_item_name: TextView? = null
        private var seller_item_desc: TextView? = null
        private var seller_item_contact: TextView? = null
        private var lyn_item: LinearLayout? = null

        fun bindItems(SellingDatalists: MutableList<String>,PostImageList: ArrayList<String>,mcc: Context) {
            iv_seller_image = itemView.findViewById<ImageView>(R.id.iv_seller_image) as ImageView
            tv_seller_price = itemView.findViewById<TextView>(R.id.tv_seller_price) as TextView
            seller_item_name = itemView.findViewById<TextView>(R.id.seller_item_name) as TextView
            seller_item_desc = itemView.findViewById<TextView>(R.id.seller_item_desc) as TextView
            seller_item_contact = itemView.findViewById<TextView>(R.id.seller_item_contact) as TextView
            lyn_item = itemView.findViewById<LinearLayout>(R.id.lyn_item) as LinearLayout
            Glide.with(mcc)
                    .load(PostImageList[0].replace("main","thumb"))
                    .override(300,300)
                    .centerCrop()
                    .error(R.drawable.ic_avatar)
                    .into(iv_seller_image)

            tv_seller_price!!.text = SellingDatalists[6]

            seller_item_name!!.text=SellingDatalists[3]
            seller_item_desc!!.text=SellingDatalists[4]
            seller_item_contact!!.text=(SellingDatalists[7]+" ("+SellingDatalists[8]+")")

            lyn_item!!.setOnClickListener {
                val mPBD = SellingItemDialog(mcc,SellingDatalists,PostImageList)
                mPBD.show()
            }

        }
    }
}