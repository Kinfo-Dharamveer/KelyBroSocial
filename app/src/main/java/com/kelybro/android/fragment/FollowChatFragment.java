package com.kelybro.android.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.kelybro.android.adapters.FollowChatAdapter;
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

public class FollowChatFragment extends Fragment {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    FollowChatAdapter adapter;
    JSONObject mOBJECT;
    List<List<String>> FollowChatList = new ArrayList<>();
    SharedPreferences mSPF;
    SharedPreferences.Editor mEDT;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_follow_chat, container, false);

        mSPF = getActivity().getSharedPreferences("AppData", 0);
        mEDT = mSPF.edit();

        swipeRefresh =  rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimaryDark,R.color.colorAccent);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh() {
                FollowChatList.clear();
                MakeVolleyRequest();
            }
        });


        recyclerView =  rootView.findViewById(R.id.follow_chat_RecyclerView);
        adapter = new FollowChatAdapter(getContext(), FollowChatList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        MakeVolleyRequest();
        return rootView;
    }
    private void MakeVolleyRequest() {
        String mURL = getString(R.string.server_url) + "Users/user_chat_list";
        StringRequest req = new StringRequest(Request.Method.POST, mURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.e("followChatList", s);
                try {
                    mOBJECT = new JSONObject(s);
                    if (mOBJECT.getBoolean("status")) {
                        JSONArray data = mOBJECT.getJSONArray("data");
                        if(data.length()>0) {
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject mData = data.getJSONObject(i);
                                List<String> followlist = new ArrayList<>();
                                followlist.add(mData.getString("user_id"));
                                followlist.add(mData.getString("user_name"));
                                followlist.add(AppConstants.imgUrl + mData.getString("user_profile_pic_thumb"));
                                followlist.add(AppConstants.imgUrl + mData.getString("user_profile_pic"));
                                followlist.add(mData.getString("user_is_vip"));
                                followlist.add(mData.getString("user_status"));
                                FollowChatList.add(followlist);
                            }
                        }
                    } else {
                        Toast.makeText(getContext(), mOBJECT.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                    adapter.notifyDataSetChanged();
                    swipeRefresh.setRefreshing(false);
//                    adapter = new FollowChatAdapter(getContext(), ChatList);
//                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    Log.e("ErrorFollowChat",e.getMessage());
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
        RequestQueue queue = newRequestQueue(getContext());
        queue.add(req);
        req.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

}
