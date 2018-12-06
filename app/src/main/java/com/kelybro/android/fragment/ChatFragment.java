package com.kelybro.android.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kelybro.android.AppConstants;
import com.kelybro.android.adapters.ChatAdapter;
import com.kelybro.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.volley.toolbox.Volley.newRequestQueue;

/**
 * Created by Krishna on 04-04-2018.
 */

public class ChatFragment extends Fragment {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    ChatAdapter adapter;
    JSONObject mOBJECT;
    List<List<String>> ChatList = new ArrayList<>();
    SharedPreferences mSPF;
    SharedPreferences.Editor mEDT;
    RequestQueue queue;
    String OtherID;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        mSPF = getActivity().getSharedPreferences("AppData", 0);
        mEDT = mSPF.edit();

        swipeRefresh =  rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimaryDark,R.color.colorAccent);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh() {
                ChatList.clear();
                MakeVolleyRequest();
            }
        });

        recyclerView = (RecyclerView) rootView.findViewById(R.id.chat_RecyclerView);
        adapter = new ChatAdapter(getContext(), ChatList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        MakeVolleyRequest();
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                List<String> chatlist = new ArrayList<>();
                chatlist = ChatList.get(viewHolder.getAdapterPosition());
                OtherID = chatlist.get(2);
                DeleteChatVolleyRequest();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        return rootView;
    }

    private void MakeVolleyRequest() {
        String mURL = getString(R.string.server_url) + "Users/all_chat_history";
        StringRequest req = new StringRequest(Request.Method.POST, mURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.e("allChat", s);
                try {
                    ChatList.clear();
                    mOBJECT = new JSONObject(s);
                    if (mOBJECT.getBoolean("success")) {
                        JSONArray data = mOBJECT.getJSONArray("data");
                        if(data.length()>0) {
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject mData = data.getJSONObject(i);
                                List<String> chatlist = new ArrayList<>();
                                chatlist.add(mData.getString("id"));
                                chatlist.add(mData.getString("send_id"));
                                chatlist.add(mData.getString("receive_id"));
                                chatlist.add(mData.getString("message"));
                                chatlist.add(mData.getString("chat_image"));
                                chatlist.add(mData.getString("created_date"));
                                chatlist.add(AppConstants.imgUrl + mData.getString("profilephoto"));
                                chatlist.add(mData.getString("user_name"));
                                ChatList.add(chatlist);
                            }
                        }
                    } else {
                        Toast.makeText(getContext(), mOBJECT.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                    swipeRefresh.setRefreshing(false);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e("ErrorChat",e.getMessage());
                    swipeRefresh.setRefreshing(false);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("user_id",mSPF.getString("id",""));
                return map;
            }
        };
        queue = newRequestQueue(getContext());
        queue.add(req);
        req.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
    private void DeleteChatVolleyRequest() {
        String mURL = getString(R.string.server_url) + "Users/delete_single_chat ";
        StringRequest req = new StringRequest(Request.Method.POST, mURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.e("Delete Chat", s);
                try {
                    mOBJECT = new JSONObject(s);
                    if (mOBJECT.getBoolean("success")) {
                        MakeVolleyRequest();
                    }
                    Toast.makeText(getContext(), mOBJECT.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Log.e("ErrorChat",e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("user_id",mSPF.getString("id",""));
                map.put("other_id",OtherID);
                return map;
            }
        };
        queue = newRequestQueue(getContext());
        queue.add(req);
        req.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

}
