package com.kelybro.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kelybro.android.customviews.mUtitily;
import com.kelybro.android.R;
import com.kelybro.android.activities.SingleChatActivity;

import java.util.List;

/**
 * Created by Krishna on 05-04-2018.
 */

public class FollowChatAdapter extends RecyclerView.Adapter<FollowChatAdapter.MyViewHolder> {

    private Context context;
    private List<List<String>> Chatlist;
    mUtitily mUtil;

    public FollowChatAdapter(Context context, List<List<String>> Chatlist) {
        this.context=context;
        this.Chatlist=Chatlist;
        this.mUtil= new mUtitily(this.context);
        Log.e("followchaltlistadapter",Chatlist.size()+"");
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_follow, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final MyViewHolder mCVH = (MyViewHolder) holder;
        final List<String> row = Chatlist.get(position);
        mCVH.tv_chat_name.setText(row.get(1));
        mCVH.tv_chat_follow.setText(row.get(5));
        Glide.with(context)
                .load(row.get(3))
                .error(R.drawable.ic_avatar)
                .into(mCVH.iv_chat_profile);

        mCVH.lyn_chat_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,SingleChatActivity.class);
//                intent.putExtra("send_id",row.get(1));
                intent.putExtra("receive_id",row.get(0));
                intent.putExtra("action_image",row.get(2));
                intent.putExtra("action_name",row.get(1));
                context.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return Chatlist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_chat_profile;
        TextView tv_chat_name;
        TextView tv_chat_follow;
        LinearLayout lyn_chat_main;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_chat_profile = (ImageView) itemView.findViewById(R.id.iv_chat_profile);
            tv_chat_name = (TextView) itemView.findViewById(R.id.tv_chat_name);
            tv_chat_follow = (TextView) itemView.findViewById(R.id.tv_chat_message);
            lyn_chat_main=(LinearLayout)itemView.findViewById(R.id.lyn_chat_main);

        }
    }
}
