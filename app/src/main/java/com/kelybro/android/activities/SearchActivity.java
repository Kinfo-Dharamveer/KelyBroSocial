package com.kelybro.android.activities;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.kelybro.android.R;
import com.kelybro.android.adapters.SearchAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.android.volley.toolbox.Volley.newRequestQueue;

/**
 * Created by Krishna on 31-03-2018.
 */

public class SearchActivity extends AppCompatActivity {
    EditText SEARCH;
    Toolbar toolbar;
    ImageView IV;
    RequestQueue queue;
    LinearLayoutManager layoutManager;
    JSONObject mOBJECT;
    ProgressDialog pDialog;
    SharedPreferences mSPF;
    RecyclerView recyclerView;
    int LimitStart=0;
    public static int i = 0;
    SearchAdapter adapter;

    ArrayList<String> businessCard;

    ArrayList<ArrayList<String>> SearchResult = new ArrayList<>();
    private AdView mAdView;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSPF = getSharedPreferences("AppData", 0);


        businessCard = new ArrayList<>();

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, getResources().getString(R.string.banner_ad_unit_id));
        mAdView = (AdView) findViewById(R.id.ad_view);

        // Start loading the ad in the background.
        mAdView.loadAd(new AdRequest.Builder().build());

        mAdView.setVisibility(View.GONE);

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                // Stuff that updates the UI

            }
        });


        SEARCH =  findViewById(R.id.editText);
        SEARCH.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(IV.getVisibility()== View.INVISIBLE)
                    IV.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        SEARCH.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    if (SEARCH.getText().toString() != null) {
                        SearchResult.clear();
                        LimitStart=0;
                        MakeVolleyRequest();
                    }
                }
                return false;
            }
        });

        IV =  findViewById(R.id.imageView2);
        IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SEARCH.setText("");
            }
        });

        recyclerView =  findViewById(R.id.my_recycler_view);
        adapter = new SearchAdapter(this, SearchResult);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int scrollDy = 0;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int maxScroll = recyclerView.computeVerticalScrollRange();
                scrollDy += dy;
                int currentScroll = recyclerView.computeVerticalScrollOffset() + recyclerView.computeVerticalScrollExtent();
                if(currentScroll>=maxScroll)
                    MakeVolleyRequest();
            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void MakeVolleyRequest() {
        showpDialog();
        String mURL = getString(R.string.server_url) + "search/get_search";
        final StringRequest req = new StringRequest(Request.Method.POST, mURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.e("Search Result",s);
                    mOBJECT = new JSONObject(s);
                    if (mOBJECT.getBoolean("success")) {
                        JSONObject data = mOBJECT.getJSONObject("data");
                        LimitStart=LimitStart+10;
                        JSONArray users = data.getJSONArray("users");
                        if(users.length()>0) {
                            for (int i = 0; i < users.length(); i++) {
                                mOBJECT = users.getJSONObject(i);
                                ArrayList<String> mRow = new ArrayList<>();
                                mRow.add(mOBJECT.getString("user_id"));
                                mRow.add(mOBJECT.getString("user_name"));
                                mRow.add(mOBJECT.getString("profilephoto"));
                                mRow.add("users");
                                SearchResult.add(mRow);
                            }
                        }
                        JSONArray jobs = data.getJSONArray("jobs");
                        if(jobs.length()>0) {
                            for (int i = 0; i < jobs.length(); i++) {
                                mOBJECT = jobs.getJSONObject(i);
                                ArrayList<String> mRow = new ArrayList<>();
                                mRow.add(mOBJECT.getString("user_id"));
                                mRow.add(mOBJECT.getString("user_name"));
                                mRow.add(mOBJECT.getString("profilephoto"));
                                mRow.add("job");
                                SearchResult.add(mRow);
                            }
                        }

                        JSONArray userposts = data.getJSONArray("user_posts");
                        if(userposts.length()>0) {
                            for (int i = 0; i < userposts.length(); i++) {
                                mOBJECT = userposts.getJSONObject(i);
                                ArrayList<String> mRow = new ArrayList<>();
                                mRow.add(mOBJECT.getString("user_id"));
                                mRow.add(mOBJECT.getString("user_name"));
                                mRow.add(mOBJECT.getString("profilephoto"));
                                mRow.add("post");
                                SearchResult.add(mRow);
                            }
                        }

                        JSONArray business_card = data.getJSONArray("business_card");
                        if(business_card.length()>0) {
                            for (int i = 0; i < business_card.length(); i++) {
                                mOBJECT = business_card.getJSONObject(i);
                                ArrayList<String> mRow = new ArrayList<>();
                                mRow.add(mOBJECT.getString("user_id"));
                                mRow.add(mOBJECT.getString("user_name"));
                                mRow.add(mOBJECT.getString("profilephoto"));
                                mRow.add("business");
                                SearchResult.add(mRow);
                            }
                        }
                        JSONArray products = data.getJSONArray("products");
                        if(products.length()>0) {
                            for (int i = 0; i < products.length(); i++) {
                                mOBJECT = products.getJSONObject(i);
                                ArrayList<String> mRow = new ArrayList<>();
                                mRow.add(mOBJECT.getString("user_id"));
                                mRow.add(mOBJECT.getString("user_name"));
                                mRow.add(mOBJECT.getString("profilephoto"));
                                mRow.add("products");
                                SearchResult.add(mRow);
                            }
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(), mOBJECT.getString("message"), Toast.LENGTH_SHORT).show();

                    }
                    adapter.notifyDataSetChanged();
                    hidepDialog();
                } catch (JSONException e) {
                    hidepDialog();
                    Log.e("Search",e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                hidepDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("user_id", mSPF.getString("id", ""));
                map.put("search_word", SEARCH.getText().toString());
                map.put("start", LimitStart+"");
                map.put("total", 10+"");
                return map;
            }
        };
        queue = newRequestQueue(getApplicationContext());
        queue.add(req);
        req.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
        super.onDestroy();
    }
}
