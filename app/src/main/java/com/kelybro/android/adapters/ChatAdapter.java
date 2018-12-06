package com.kelybro.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
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

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private Context context;
    private List<List<String>> Chatlist;
    mUtitily mUtil;

    public ChatAdapter(Context context, List<List<String>> Chatlist) {
        this.context=context;
        this.Chatlist=Chatlist;
        this.mUtil= new mUtitily(this.context);
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
        mCVH.tv_chat_name.setText(row.get(7));
        mCVH.tv_chat_message.setText(row.get(3));
        mCVH.tv_chat_date.setText(mUtil.getDateDifference(row.get(5)));
        Glide.with(context)
                .load(row.get(6))
                .error(R.drawable.ic_avatar)
                .into(mCVH.iv_chat_profile);
        mCVH.lyn_chat_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,SingleChatActivity.class);
                intent.putExtra("send_id",row.get(1));
                intent.putExtra("receive_id",row.get(2));
                intent.putExtra("action_image",row.get(6));
                intent.putExtra("action_name",row.get(7));
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
        TextView tv_chat_message;
        TextView tv_chat_date;
        LinearLayout lyn_chat_main;
        public MyViewHolder(View itemView) {
            super(itemView);
            iv_chat_profile = (ImageView) itemView.findViewById(R.id.iv_chat_profile);
            tv_chat_name = (TextView) itemView.findViewById(R.id.tv_chat_name);
            tv_chat_message = (TextView) itemView.findViewById(R.id.tv_chat_message);
            tv_chat_date = (TextView) itemView.findViewById(R.id.tv_chat_date);
            lyn_chat_main=(LinearLayout)itemView.findViewById(R.id.lyn_chat_main);
        }
    }
}