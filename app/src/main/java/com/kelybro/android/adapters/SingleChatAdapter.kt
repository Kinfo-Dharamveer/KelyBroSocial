package com.kelybro.android.adapters

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.bumptech.glide.Glide
import com.kelybro.android.AppConstants
import com.kelybro.android.customviews.mUtitily
import com.kelybro.android.R
import com.kelybro.android.activities.ViewImageActivity

/**
 * Created by Krishna on 06-04-2018.
 */

class SingleChatAdapter(private val context: Context, private val SingleChatlist: List<List<String>>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    internal var mUtil: mUtitily
    internal var mSPF: SharedPreferences

    init {
        this.mUtil = mUtitily(context)
        this.mSPF = context.getSharedPreferences("AppData", 0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.chat_two_side_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mCVH = holder as MyViewHolder
        val row = SingleChatlist[position]
        if (row[2] == mSPF.getString("id", "")) {
            mCVH.lyn_receiver.visibility = View.VISIBLE
            mCVH.lyn_sender.visibility = View.GONE
            mCVH.tv_chat_receiver.text = row[3]
            if (row[4] != AppConstants.imgUrl) {
                Glide.with(context)
                        .load(row[4])
                        .into(mCVH.iv_receiver_image)
                mCVH.iv_receiver_image.visibility = View.VISIBLE
                mCVH.iv_receiver_image.setOnClickListener {
                    val i = Intent(context, ViewImageActivity::class.java)
                    i.putExtra("ImagePath", row[4])
                    context.startActivity(i)
                }
            } else {
                mCVH.iv_receiver_image.visibility = View.GONE
            }
            mCVH.tv_chat_receiver_date.text = mUtil.getDateDifference(row[6])
        } else {
            mCVH.lyn_sender.visibility = View.VISIBLE
            mCVH.lyn_receiver.visibility = View.GONE
            mCVH.tv_chat_sender.text = row[3]
            Log.e("Chat",row[3])
            if (row[4] != AppConstants.imgUrl) {
                Glide.with(context)
                        .load(row[4])
                        .into(mCVH.iv_sender_image)
                mCVH.iv_sender_image.visibility = View.VISIBLE
                mCVH.iv_sender_image.setOnClickListener {
                    val i = Intent(context, ViewImageActivity::class.java)
                    i.putExtra("ImagePath", row[4])
                    context.startActivity(i)
                }
            } else {
                mCVH.iv_sender_image.visibility = View.GONE
            }
            mCVH.tv_chat_sender_date.text = mUtil.getDateDifference(row[6])

        }

    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var iv_sender_image: ImageView
        internal var iv_receiver_image: ImageView
        internal var tv_chat_sender: TextView
        internal var tv_chat_receiver: TextView
        internal var tv_chat_receiver_date: TextView
        internal var tv_chat_sender_date: TextView
        internal var lyn_sender: LinearLayout
        internal var lyn_receiver: LinearLayout

        init {
            iv_sender_image = itemView.findViewById<View>(R.id.iv_sender_image) as ImageView
            tv_chat_sender = itemView.findViewById<View>(R.id.tv_chat_sender) as TextView
            iv_receiver_image = itemView.findViewById<View>(R.id.iv_receiver_image) as ImageView
            tv_chat_receiver = itemView.findViewById<View>(R.id.tv_chat_receiver) as TextView
            tv_chat_sender_date = itemView.findViewById<View>(R.id.tv_chat_sender_date) as TextView
            tv_chat_receiver_date = itemView.findViewById<View>(R.id.tv_chat_receiver_date) as TextView
            lyn_sender = itemView.findViewById<View>(R.id.lyn_sender) as LinearLayout
            lyn_receiver = itemView.findViewById<View>(R.id.lyn_receiver) as LinearLayout
        }
    }

    override fun getItemCount(): Int {
        return SingleChatlist.size
    }
}
