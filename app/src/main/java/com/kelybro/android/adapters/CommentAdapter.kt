package com.kelybro.android.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.kelybro.android.R

/**
 * Created by Krishna on 18-01-2018.
 */
class CommentAdapter(var CommentList: MutableList<MutableList<String>>?, var mc: Context) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.ViewHolder {
        val v = LayoutInflater.from(mc).inflate(R.layout.row_comment_list, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: CommentAdapter.ViewHolder, position: Int) {
        holder.bindItems(CommentList!![position], mc)

       /* val editor = mc.getSharedPreferences("PREF_COMMENT", Context.MODE_PRIVATE).edit()
        editor.putString("lastcomment", CommentList!!.get(CommentList!!.size - 1).toString())
        editor.apply()*/

    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return CommentList!!.size
    }


    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var iv_cm_user_name: TextView? = null
        private var iv_cm_user_comment: TextView? = null
        private var iv_cm_user_profile: de.hdodenhof.circleimageview.CircleImageView? = null

        fun bindItems(CommentLists: MutableList<String>, mcc: Context) {
            iv_cm_user_comment = itemView.findViewById<TextView>(R.id.iv_cm_user_comment) as TextView
            iv_cm_user_name = itemView.findViewById<TextView>(R.id.iv_cm_user_name) as TextView
            iv_cm_user_profile = itemView.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.iv_cm_user_profile) as de.hdodenhof.circleimageview.CircleImageView
            iv_cm_user_name!!.text = CommentLists[2]
            iv_cm_user_comment!!.text = CommentLists[3]
            Glide.with(mcc).load(CommentLists[8]).error(R.drawable.ic_avatar).into(iv_cm_user_profile)

        }
    }
}