package com.kelybro.android.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PushbuttonField;
import com.kelybro.android.AppConstants;
import com.kelybro.android.R;
import com.kelybro.android.model.ModelGPJPFormDetail;
import com.orhanobut.hawk.Hawk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.android.volley.toolbox.Volley.newRequestQueue;

public class ActivityGPJPFormDownload extends AppCompatActivity {

    Context context;
    String UserData = "";
    ModelGPJPFormDetail UserFormDetail;
    TextView tvDownloadFile;
    ProgressDialog progressDialog;

    String City = "", State = "", Country = "";

    String[] StringArrGender = {"Selece Gender", "Male", "Female"};
    String[] StringArrVIP = {"IS VIP", "Yes", "No"};
    String[] StringArrMaritalStatus = {"Marital Status", "Married", "Not Married"};
    String[] StringArrIsPartyRegister = {"Is Party Registerd", "Yes", "No"};

    JSONObject mOBJECT;
    JSONArray mArray;
    ArrayList<String> CityList;
    ArrayList<String> CityListID;
    ArrayList<String> StateList;
    ArrayList<String> StateListID;
    ArrayList<String> CoutnryList;
    ArrayList<String> CoutnryListID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpjpform_download);

        context = ActivityGPJPFormDownload.this;

        CityList = new ArrayList<>();
        CityListID = new ArrayList<>();
        StateList = new ArrayList<>();
        StateListID = new ArrayList<>();
        CoutnryList = new ArrayList<>();
        CoutnryListID = new ArrayList<>();

        tvDownloadFile = findViewById(R.id.tvDownloadFile);
        CSCVolleyRequest("cities");
        CSCVolleyRequest("states");
        CSCVolleyRequest("countries");

        if (getIntent().hasExtra("data")) {
            UserData = getIntent().getStringExtra("data");
            Type type = new TypeToken<ModelGPJPFormDetail>() {
            }.getType();
            UserFormDetail = new Gson().fromJson(UserData, type);
        }

        tvDownloadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!City.equals("") && !State.equals("") && !Country.equals("")) {
                    try {
                        setData();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(context, "Please Wait Data is Loading...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setData() throws IOException, DocumentException {

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        File pdfFolder = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "KelyBro");
        if (!pdfFolder.exists()) {
            pdfFolder.mkdir();
            Log.i("GPJP FORM", "Pdf Directory created");
        }

        Date date = new Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);

        final File myFile = new File(pdfFolder + timeStamp + ".pdf");
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                PdfReader reader = null;
                try {
                    reader = new PdfReader(getResources().openRawResource(R.raw.gpjp_form));


                    AcroFields fields = reader.getAcroFields();

       /* Set<String> fldNames = fields.getFields().keySet();

        for (String fldName : fldNames) {
            System.out.println(fldName + ": " + fields.getField(fldName));
        }*/

                    OutputStream output = new FileOutputStream(myFile);

//        Base64.OutputStream output = null;
//        PdfReader reader = new PdfReader(getResources().openRawResource(R.raw.template3));
                    PdfStamper stamper = new PdfStamper(reader, output);
                    AcroFields acroFields = stamper.getAcroFields();


                    acroFields.setField("FNameF", UserFormDetail.getF_name());
                    acroFields.setField("MNameF", UserFormDetail.getL_name());
                    acroFields.setField("LNameF", UserFormDetail.getL_name());
                    acroFields.setField("ContactF", UserFormDetail.getPhone_number());
                    acroFields.setField("BirthdateF", UserFormDetail.getBirthdate());
                    acroFields.setField("AddressF", UserFormDetail.getAddress());
                    acroFields.setField("TalukaF", UserFormDetail.getTaluka());
                    acroFields.setField("VillageF", UserFormDetail.getVillage());
                    acroFields.setField("ZipCodeF", UserFormDetail.getZip_code());
                    acroFields.setField("FamilyMemberF", UserFormDetail.getTotal_family_members());
                    acroFields.setField("incomeF", UserFormDetail.getIncome());
                    acroFields.setField("PoliticalInfluenceF", UserFormDetail.getPolitical_influence());
                    acroFields.setField("AboutYourF", UserFormDetail.getAbout_urself());

                    acroFields.setField("CountryF", Country);
                    acroFields.setField("StateF", State);
                    acroFields.setField("JilloF", City);

                    if (UserFormDetail.getGender().equals("0")) {
                        acroFields.setField("GenderF", StringArrGender[1]);
                    } else {
                        acroFields.setField("GenderF", StringArrGender[2]);
                    }

                    if (UserFormDetail.getMarital_status().equals("marride")) {
                        acroFields.setField("MarriedF", StringArrMaritalStatus[1]);
                    } else {
                        acroFields.setField("MarriedF", StringArrMaritalStatus[2]);
                    }

                    InputStream ims = new URL("http://www.kelybro.com/upload/gpjp/main/" + UserFormDetail.getProfilephoto()).openStream();
                    Bitmap bmp = BitmapFactory.decodeStream(ims);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Image image = Image.getInstance(stream.toByteArray());

                    PushbuttonField ad = acroFields.getNewPushbuttonFromField("Button3");
                    ad.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                    ad.setProportionalIcon(true);
                    ad.setImage(image);
                    acroFields.replacePushbuttonField("Button3", ad.getField());

                    String counter = Hawk.get(AppConstants.SAVE_COUNTER);

                    int countInc = Integer.parseInt(counter);

                    int count = countInc+1;

                    acroFields.setField("formNo", String.valueOf(count));
//        stamper.close();
//        reader.close();

                    stamper.setFormFlattening(true);
                    stamper.close();


               progressDialog.dismiss();
//                    Toast.makeText(context, myFile.getPath(), Toast.LENGTH_SHORT).show();
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            }
        });
        t.start();
        Toast.makeText(context, "Your File is downloading at " + myFile.getPath(), Toast.LENGTH_LONG).show();
//        form.setField("address", customer.getStreetAddress());
//        form.setField("city", customer.getCity());
    }

    private void CSCVolleyRequest(final String api) {
        String mURL = getString(R.string.server_url) + "comman/" + api;
        StringRequest req = new StringRequest(Request.Method.GET, mURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.e("followChatList", s);
                try {
                    mOBJECT = new JSONObject(s);
                    if (mOBJECT.getBoolean("success")) {
                        if (mOBJECT.getJSONArray("data") != null) {
                            mArray = mOBJECT.getJSONArray("data");
                            if (api.equals("cities")) {
                                CityList.clear();
                                CityListID.clear();
                            } else if (api.equals("states")) {
                                StateList.clear();
                                StateListID.clear();
                            } else if (api.equals("countries")) {
                                CoutnryList.clear();
                                CoutnryListID.clear();
                            }
                            for (int i = 0; i < mArray.length(); i++) {
                                JSONObject mData = mArray.getJSONObject(i);
                                if (api.equals("cities")) {
                                    CityList.add(mData.getString("cityname"));
                                    CityListID.add(mData.getString("cityid"));
                                } else if (api.equals("states")) {
                                    StateList.add(mData.getString("statename"));
                                    StateListID.add(mData.getString("stateid"));

                                } else if (api.equals("countries")) {
                                    CoutnryList.add(mData.getString("countryname"));
                                    CoutnryListID.add(mData.getString("countryid"));
                                }
                            }
                            if (UserFormDetail != null) {
                                for (int i = 0; i < mArray.length(); i++) {
                                    JSONObject mData = mArray.getJSONObject(i);
                                    if (api.equals("cities")) {
                                        if (UserFormDetail.getCity().equals(mData.getString("cityid"))) {
                                            City = mData.getString("cityid");
                                        }
                                    } else if (api.equals("states")) {
                                        if (UserFormDetail.getState().equals(mData.getString("stateid"))) {
                                            State = mData.getString("stateid");
                                        }
                                    } else if (api.equals("countries")) {
                                        if (UserFormDetail.getCountry().equals(mData.getString("countryid"))) {
                                            Country = mData.getString("countryid");
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Toast.makeText(context, mOBJECT.getString("message"), Toast.LENGTH_SHORT).show();
                    }
//                    adapter.notifyDataSetChanged();
//                    adapter = new FollowChatAdapter(getContext(), ChatList);
//                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    Log.e("ErrorFollowChat", e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("ErrorFollowChat", volleyError.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                return map;
            }
        };
        RequestQueue queue = newRequestQueue(context);
        queue.add(req);
        req.setRetryPolicy(new
                DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

}
    /*// DownloadImage AsyncTask
    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(context);
            // Set progressdialog title
            mProgressDialog.setTitle("Download Image Tutorial");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // Set the bitmap into ImageView

            // Close progressdialog
            mProgressDialog.dismiss();
        }
    }*/

