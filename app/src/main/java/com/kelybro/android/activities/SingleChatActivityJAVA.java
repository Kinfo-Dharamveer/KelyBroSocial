package com.kelybro.android.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.kelybro.android.AppConstants;
import com.kelybro.android.R;
import com.kelybro.android.adapters.SingleChatAdapter;
import com.kelybro.android.model.VolleyMultipartRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.volley.toolbox.Volley.newRequestQueue;

/**
 * Created by Krishna on 06-04-2018.
 */

public class SingleChatActivityJAVA extends AppCompatActivity {
    Toolbar ma_toolbar;
    RecyclerView recyclerView;
    SingleChatAdapter adapter;
    JSONObject mOBJECT;
    List<List<String>> SingleChatList = new ArrayList<>();
    de.hdodenhof.circleimageview.CircleImageView iv_single_chat_profile;
    TextView tv_single_chat_name;
    EditText et_chat_message;
    ImageView bt_chat_send;
    ImageView iv_choose_image,iv_chat_image;
    SharedPreferences mSPF;
    SharedPreferences.Editor mEDT;
    String sendId = "";
    String receiveId = "";
    String actionImage = "";
    String actionNmae = "";
    RequestQueue queue;
    LinearLayoutManager layoutManager;
    Handler handler;
    private Bitmap bitmap;
    String chat_image="";
    private ProgressDialog pDialog;
    private AdView mAdView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_chat);

        Intent intent = getIntent();
        sendId = intent.getStringExtra("send_id");
        receiveId = intent.getStringExtra("receive_id");
        actionImage = intent.getStringExtra("action_image");
        actionNmae = intent.getStringExtra("action_name");

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, getResources().getString(R.string.banner_ad_unit_id));
        mAdView = (AdView) findViewById(R.id.ad_view);

        // Start loading the ad in the background.
        mAdView.loadAd(new AdRequest.Builder().build());



        mSPF = getSharedPreferences("AppData", 0);
        mEDT = mSPF.edit();
        ma_toolbar = (Toolbar) findViewById(R.id.ma_toolbar);
        setSupportActionBar(ma_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        iv_single_chat_profile = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.iv_single_chat_profile);
        tv_single_chat_name = (TextView) findViewById(R.id.tv_single_chat_name);

        Glide.with(getApplicationContext()).load(actionImage).error(R.drawable.ic_avatar).into(iv_single_chat_profile);
        tv_single_chat_name.setText(actionNmae);

        et_chat_message = (EditText) findViewById(R.id.et_chat_message);
        bt_chat_send = (ImageView) findViewById(R.id.bt_chat_send);
        iv_choose_image = (ImageView) findViewById(R.id.iv_choose_image);
        iv_chat_image = (ImageView) findViewById(R.id.iv_chat_image);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_single_chat);
        adapter = new SingleChatAdapter(this, SingleChatList);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int currentScroll = recyclerView.computeVerticalScrollOffset() + recyclerView.computeVerticalScrollExtent();
                if(currentScroll>=recyclerView.computeVerticalScrollRange())
                    timer();
                else
                    handler.removeMessages(0);
            }
        });
        iv_choose_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] mimeTypes = {"image/jpeg", "image/png"};
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), 111);
            }
        });

        bt_chat_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!et_chat_message.getText().toString().equals("") || chat_image!="") {
                    SendDataMakeVolleyRequest();
                }
            }
        });
        ma_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),UserProfileActivity.class);
                i.putExtra("UserID",mSPF.getString("id",""));
                i.putExtra("ProfileID",receiveId);
                startActivity(i);
            }
        });
        timer();


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                chat_image = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            }catch (Exception e){
                Log.e("Post Images",e.getMessage());
            }
        }
    }
    private void MakeVolleyRequest() {
        String mURL = getString(R.string.server_url) + "Users/single_chat_history ";
        final StringRequest req = new StringRequest(Request.Method.POST, mURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    mOBJECT = new JSONObject(s);
                    if (mOBJECT.getBoolean("success")) {
                        SingleChatList.clear();
                        JSONArray data = mOBJECT.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject mData = data.getJSONObject(i);
                            List<String> chatlist = new ArrayList<>();
                            chatlist.add(mData.getString("id"));
                            chatlist.add(mData.getString("send_id"));
                            chatlist.add(mData.getString("receive_id"));
                            chatlist.add(mData.getString("message"));
                            chatlist.add(AppConstants.imgUrl + mData.getString("chat_image"));
                            chatlist.add(mData.getString("read"));
                            chatlist.add(mData.getString("created_date"));
                            chatlist.add(mData.getString("user_name"));
                            chatlist.add(mData.getString("profilephoto"));
                            SingleChatList.add(chatlist);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), mOBJECT.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                    adapter.notifyDataSetChanged();
                    layoutManager.scrollToPosition(SingleChatList.size() - 1);
                } catch (JSONException e) {
                    e.printStackTrace();
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
                map.put("user_id", mSPF.getString("id", ""));
                map.put("other_id", receiveId);
                return map;
            }
        };
        queue = newRequestQueue(getApplicationContext());
        queue.add(req);
        req.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void SendDataMakeVolleyRequest() {
        showpDialog();
        String mURL = getString(R.string.server_url) + "Users/send_message";
        VolleyMultipartRequest req = new VolleyMultipartRequest (Request.Method.POST, mURL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse  response) {
                String resultResponse = new String(response.data);
                Log.d("Data", resultResponse);
                try {
                    mOBJECT = new JSONObject(resultResponse);
                    if (mOBJECT.getBoolean("success")==true){
                        et_chat_message.setText("");
                        chat_image="";
                        MakeVolleyRequest();
                        Toast.makeText(getApplicationContext(), mOBJECT.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), mOBJECT.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("Error", e.toString());
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidepDialog();
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("user_id", mSPF.getString("id", ""));
                map.put("message", et_chat_message.getText().toString());
                map.put("sender_id", receiveId);
                map.put("chat_image", chat_image);
                map.put("type", "PHOTO");
                return map;
            }
        };
        queue = newRequestQueue(getApplicationContext());
        queue.add(req);
        req.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void timer() {
        handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                MakeVolleyRequest();
                handler.postDelayed(this, 3000);
            }
        };
        handler.postDelayed(runnable, 3000);
    }

    private void showpDialog() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        if(handler!=null)
            handler.removeMessages(0);
        super.onDestroy();
    }
}