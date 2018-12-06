package com.kelybro.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kelybro.android.AppConstants;
import com.kelybro.android.customviews.mUtitily;
import com.kelybro.android.R;
import com.kelybro.android.activities.UserProfileActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Krishna on 06-04-2018.
 */

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<ArrayList<String>> SingleChatlist;
    mUtitily mUtil;
    SharedPreferences mSPF;
    public SearchAdapter(Context context, ArrayList<ArrayList<String>> Chatlist) {
        this.context=context;
        this.SingleChatlist=Chatlist;
        this.mUtil= new mUtitily(context);
        this.mSPF = context.getSharedPreferences("AppData", 0);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.row_search, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MyViewHolder mCVH = (MyViewHolder) holder;
        final List<String> row = SingleChatlist.get(position);
        mCVH.buyer_name.setText(row.get(1));
        mCVH.tv_type.setText(row.get(3));
        Glide.with(context).load(AppConstants.imgUrl+row.get(2)).error(R.drawable.ic_avatar).into(mCVH.buyer_profile_image);
        mCVH.lyn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, UserProfileActivity.class);
                i.putExtra("UserID", mSPF.getString("id",""));
                i.putExtra("ProfileID", row.get(0));
                context.startActivity(i);
            }
        });
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
       ImageView buyer_profile_image;
       TextView buyer_name;
       TextView tv_type;
       LinearLayout lyn_main;

        public MyViewHolder(View itemView) {
            super(itemView);
            buyer_profile_image = (ImageView) itemView.findViewById(R.id.buyer_profile_image);
            buyer_name = (TextView) itemView.findViewById(R.id.buyer_name);
            tv_type = (TextView) itemView.findViewById(R.id.tv_type);
            lyn_main = (LinearLayout) itemView.findViewById(R.id.lyn_main);
        }
    }
    @Override
    public int getItemCount() {
        return SingleChatlist.size();
    }
}
