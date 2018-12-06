package com.kelybro.android.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.kelybro.android.R;
import com.kelybro.android.customviews.VolleyMultipartRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.android.volley.toolbox.Volley.newRequestQueue;


/**
 * Created by hp on 12/14/2017.
 */

public class ViewProfileActivityNew extends AppCompatActivity {

    private TextView mVPFirstName, mVPLastName, mVPContactNo, mVPAddress, mVPZipCode, mVPCity;
    private EditText mVPEditFirstname, mVPEditLastName, mVPEditContactNo, mVPEditAddress, mVPEditCity, mVPEditZipCode;
    JSONObject mOBJECT;
    JSONObject SOBJECT;
    private Toolbar ma_toolbar;
    private RequestQueue mREQ;
    SharedPreferences mSPF;
    SharedPreferences.Editor mEDT;
    private TextView tv_update, tv_edit;
    private RelativeLayout vpf_rl_view_edit_profile,vpf_rl_view_profile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        mSPF = getSharedPreferences("AppData", 0);
        mEDT = mSPF.edit();

        ma_toolbar = (Toolbar) findViewById(R.id.ma_toolbar);
        ma_toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        setSupportActionBar(ma_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Profile");

        Reference();
        Implementation();
    }


    private void Reference() {
        mVPFirstName = (TextView) findViewById(R.id.vpf_first_name);
        mVPLastName = (TextView) findViewById(R.id.vpf_last_name);
        mVPContactNo = (TextView) findViewById(R.id.vpf_contact_no);
        mVPAddress = (TextView) findViewById(R.id.vpf_address);
        mVPCity = (TextView) findViewById(R.id.vpf_city);
        mVPZipCode = (TextView) findViewById(R.id.vpf_zip_code);
        mVPEditFirstname = (EditText) findViewById(R.id.up_et_first_name);
        mVPEditLastName = (EditText) findViewById(R.id.up_et_last_name);
        mVPEditContactNo = (EditText) findViewById(R.id.up_et_contact_no);
        mVPEditAddress = (EditText) findViewById(R.id.up_et_address);
        mVPEditCity = (EditText) findViewById(R.id.up_et_city);
        mVPEditZipCode = (EditText) findViewById(R.id.up_et_zipcode);
        tv_update = (TextView) findViewById(R.id.tv_update);
        tv_edit = (TextView) findViewById(R.id.tv_edit);
        vpf_rl_view_profile = (RelativeLayout) findViewById(R.id.vpf_rl_view_profile);
        vpf_rl_view_edit_profile = (RelativeLayout) findViewById(R.id.vpf_rl_view_edit_profile);
    }

    private void Implementation() {

        mVPFirstName.setText(mSPF.getString("f_name", ""));
        mVPLastName.setText(mSPF.getString("l_name", ""));
        mVPContactNo.setText(mSPF.getString("phone_number", ""));
        mVPAddress.setText(mSPF.getString("address", ""));
        mVPCity.setText(mSPF.getString("city", ""));
        mVPZipCode.setText(mSPF.getString("zip_code", ""));



        tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vpf_rl_view_profile.setVisibility(View.GONE);
                vpf_rl_view_edit_profile.setVisibility(View.VISIBLE);
                mVPEditFirstname.setText(mSPF.getString("f_name", ""));
                mVPEditLastName.setText(mSPF.getString("l_name", ""));
                mVPEditContactNo.setText(mSPF.getString("phone_number", ""));
                mVPEditAddress.setText(mSPF.getString("address", ""));
                mVPEditCity.setText(mSPF.getString("city", ""));
                mVPEditZipCode.setText(mSPF.getString("zip_code", ""));
            }
        });


        tv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vpf_rl_view_edit_profile.setVisibility(View.GONE);
                MakeVolleyRequest();

            }
        });

    }

    private void MakeVolleyRequest() {
        final String mURL = getString(R.string.server_url) + "Users/update_users_data";
        VolleyMultipartRequest smr1 = new VolleyMultipartRequest(Request.Method.POST, mURL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);

                Log.d("Data", resultResponse);
                try {
                    mOBJECT = new JSONObject(resultResponse);
                    if (mOBJECT.getBoolean("success")) {
//                        SOBJECT = mOBJECT.getJSONObject("data");
                        JSONObject data = mOBJECT.getJSONObject("data");
                        mEDT.putString("f_name", data.getString("f_name"));
                        mEDT.putString("l_name", data.getString("l_name"));
                        mEDT.putString("phone_number", data.getString("phone_number"));
                        mEDT.putString("address", data.getString("address"));
                        mEDT.putString("city", data.getString("city"));
                        mEDT.putString("zip_code", data.getString("zip_code"));
//                        mEDT.putString("profilephoto", SOBJECT.getString("profilephoto"));
                        mEDT.commit();
                        Toast.makeText(getApplicationContext(), mOBJECT.getString("message"), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), mOBJECT.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("Error", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("id", mSPF.getString("id", ""));
                map.put("f_name", mVPEditFirstname.getText().toString());
                map.put("l_name", mVPEditLastName.getText().toString());
                map.put("phone_number", mVPEditContactNo.getText().toString());
                map.put("address", mVPEditAddress.getText().toString());
                map.put("city", mVPEditCity.getText().toString());
                map.put("zip_code", mVPEditZipCode.getText().toString());
                return map;
            }

        };
        mREQ = newRequestQueue(getApplicationContext());
        mREQ.add(smr1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}
