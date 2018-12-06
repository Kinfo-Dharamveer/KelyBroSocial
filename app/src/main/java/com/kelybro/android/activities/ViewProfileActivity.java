package com.kelybro.android.activities;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.kelybro.android.AppConstants;
import com.kelybro.android.R;
import com.kelybro.android.model.VolleyMultipartRequest;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;


import static com.android.volley.toolbox.Volley.newRequestQueue;


/**
 * Created by hp on 12/14/2017.
 */

public class ViewProfileActivity extends AppCompatActivity {

    private ImageView mVPPhoto, mVPEditPhoto;
    private TextView mVPFirstName, mVPLastName, mVPContactNo, mVPAddress, mVPZipCode, mVPCity;
    private EditText mVPEditFirstname, mVPEditLastName, mVPEditContactNo, mVPEditAddress, mVPEditCity, mVPEditZipCode;
    private TextView mTvupdate;
    private LinearLayout mVPEdit, mVPSaveProfile;
    JSONObject mOBJECT;
    JSONObject SOBJECT;
    private RelativeLayout mVPRlProfile, mVPRlEditProfile;
    private Toolbar ma_toolbar;
    private Bitmap bitmap;
    String filePath;
    private RequestQueue mREQ;
    SharedPreferences mSPF;
    SharedPreferences.Editor mEDT;
    private ScrollView mVPFScrollview;
    private ScrollView mEPFScrollview;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mSPF = getSharedPreferences("AppData", 0);
        mEDT = mSPF.edit();

        ma_toolbar=(Toolbar)findViewById(R.id.ma_toolbar);
        setSupportActionBar(ma_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        Reference();
        Implementation();
    }


    private void Reference() {
        mVPPhoto = (ImageView) findViewById(R.id.vpf_UserPhoto);
        mVPEdit = (LinearLayout) findViewById(R.id.vpf_ll_edit_profile);
        mVPFirstName = (TextView) findViewById(R.id.vpf_first_name);
        mVPLastName = (TextView) findViewById(R.id.vpf_last_name);
        mVPContactNo = (TextView) findViewById(R.id.vpf_contact_no);
        mVPAddress = (TextView) findViewById(R.id.vpf_address);
        mVPCity = (TextView) findViewById(R.id.vpf_city);
        mVPZipCode = (TextView) findViewById(R.id.vpf_zip_code);

        mVPRlProfile = (RelativeLayout) findViewById(R.id.vpf_rl_view_profile);
        mVPRlEditProfile = (RelativeLayout) findViewById(R.id.vpf_rl_view_edit_profile);

        mVPEditPhoto = (ImageView) findViewById(R.id.UpUserImage);
        mVPEditFirstname = (EditText) findViewById(R.id.up_et_first_name);
        mVPEditLastName = (EditText) findViewById(R.id.up_et_last_name);
        mVPEditContactNo = (EditText) findViewById(R.id.up_et_contact_no);
        mVPEditAddress = (EditText) findViewById(R.id.up_et_address);
        mVPEditCity = (EditText) findViewById(R.id.up_et_city);
        mVPEditZipCode = (EditText) findViewById(R.id.up_et_zipcode);

        mVPSaveProfile = (LinearLayout) findViewById(R.id.vpf_save_profile);

        mVPFScrollview =(ScrollView)findViewById(R.id.vpf_scrollview);
        mEPFScrollview =(ScrollView)findViewById(R.id.epf_scrollview);
    }

    private void Implementation() {
        Glide.with(getApplicationContext())
                .load(AppConstants.imgUrl + mSPF.getString("profilephoto", ""))
                .error(R.drawable.ic_avatar)
                .into(mVPPhoto);

        mVPFirstName.setText(mSPF.getString("f_name", ""));
        mVPLastName.setText(mSPF.getString("l_name", ""));
        mVPContactNo.setText(mSPF.getString("phone_number", ""));
        mVPAddress.setText(mSPF.getString("address", ""));
        mVPCity.setText(mSPF.getString("phone_number", ""));
        mVPZipCode.setText(mSPF.getString("zip_code", ""));

        mVPEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVPRlProfile.setVisibility(View.GONE);
                mVPRlEditProfile.setVisibility(View.VISIBLE);
                mVPFScrollview.setVisibility(View.INVISIBLE);
                mEPFScrollview.setVisibility(View.VISIBLE);
                mVPEditFirstname.setText(mSPF.getString("f_name", ""));
                mVPEditLastName.setText(mSPF.getString("l_name", ""));
                mVPEditContactNo.setText(mSPF.getString("phone_number", ""));
                mVPEditAddress.setText(mSPF.getString("address", ""));
                mVPEditCity.setText(mSPF.getString("city", ""));
                mVPEditZipCode.setText(mSPF.getString("zip_code", ""));
            }
        });


        Glide.with(getApplicationContext())
                .load(AppConstants.imgUrl + mSPF.getString("profilephoto", ""))
                .error(R.drawable.ic_avatar)
                .into(mVPEditPhoto);


        mVPSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVPRlProfile.setVisibility(View.VISIBLE);
                mVPRlEditProfile.setVisibility(View.GONE);
                MakeVolleyRequest();

            }
        });
        mVPEditPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), 111);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri picUri = data.getData();
            filePath = getPath(picUri);
            mVPEditPhoto.setImageURI(picUri);
        }
    }

    private String getPath(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
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
                        SOBJECT = mOBJECT.getJSONObject("data");
                        mEDT.putString("f_name", SOBJECT.getString("f_name"));
                        mEDT.putString("l_name", SOBJECT.getString("l_name"));
                        mEDT.putString("phone_number", SOBJECT.getString("phone_number"));
                        mEDT.putString("address", SOBJECT.getString("address"));
                        mEDT.putString("city", SOBJECT.getString("city"));
                        mEDT.putString("zip_code", SOBJECT.getString("zip_code"));
                        mEDT.putString("profilephoto", SOBJECT.getString("profilephoto"));

                        Toast.makeText(getApplicationContext(), mOBJECT.getString("message"), Toast.LENGTH_SHORT).show();

//                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                        startActivity(intent);
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
                map.put("id",mSPF.getString("id", ""));
                map.put("f_name", mVPEditFirstname.getText().toString());
                map.put("l_name", mVPEditLastName.getText().toString());
                map.put("phone_number", mVPEditContactNo.getText().toString());
                map.put("address", mVPEditAddress.getText().toString());
                map.put("city", mVPEditCity.getText().toString());
                map.put("zip_code", mVPEditZipCode.getText().toString());

                return map;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("profilephoto", new DataPart(filePath, getFileDataFromDrawable(getBaseContext(), mVPEditPhoto.getDrawable()), "image/jpeg"));
                return params;
            }
        };
        mREQ = newRequestQueue(getApplicationContext());
        mREQ.add(smr1);
        smr1.setRetryPolicy(new DefaultRetryPolicy(3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private static byte[] getFileDataFromDrawable(Context context, Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}
