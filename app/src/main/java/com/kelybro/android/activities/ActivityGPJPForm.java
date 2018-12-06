package com.kelybro.android.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
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

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.android.volley.toolbox.Volley.newRequestQueue;

public class ActivityGPJPForm extends AppCompatActivity {

    private static final String MY_PREFS_NAME = "formCounter";
    Context context;
    EditText etFirstName, etMiddleName, etLastName, etEmail, etContactNo, etAddress, etInCome, etFamilyMember, etAboutYourSelf, etPoliticalInfluence, etTaluka, et_zip_code, etVillage;
    Spinner sp_Gender, sp_IsVip, sp_MaritalStatus, sp_Country, sp_State, sp_City, sp_IsPartyRegisterd;
    TextView tvFileName, tvChooseFile, tvUploadProfile, tvBirthday;
    public TextView tv_form_counter;
    ImageView ivProfile;
    RelativeLayout ll_pdflayout;
    CheckBox cbAgree;
    ScrollView form_layout;

    SharedPreferences mSPF;
    SharedPreferences.Editor mEDT;

    JSONObject mOBJECT;
    JSONArray mArray;
    ArrayList<String> CityList;
    ArrayList<String> CityListID;
    ArrayList<String> StateList;
    ArrayList<String> StateListID;
    ArrayList<String> CoutnryList;
    ArrayList<String> CoutnryListID;
    ArrayList<String> GenderList;
    ArrayList<String> VIPList;
    ArrayList<String> MarriedList;
    ArrayList<String> IsPartyRegisterList;

    ArrayAdapter<String> dataAdaptercity;
    ArrayAdapter<String> dataAdapterState;
    ArrayAdapter<String> dataAdapterCountry;
    ArrayAdapter<String> dataAdapterGender;
    ArrayAdapter<String> dataAdapterVIP;
    ArrayAdapter<String> dataAdapterMaritalStatus;
    ArrayAdapter<String> dataAdapterIsPartyRegister;

    public static int REQUEST_PERMISSIONS = 1;
    boolean boolean_permission;
    boolean boolean_save;
    Bitmap bitmap;
    ProgressDialog progressDialog;
    String PdfID = "";
    int mYear, mMonth, mDay;
    String UserData = "";
    ModelGPJPFormDetail UserFormDetail;

    String[] StringArrGender = {"Selece Gender", "Male", "Female"};
    String[] StringArrVIP = {"IS VIP", "Yes", "No"};
    String[] StringArrMaritalStatus = {"Marital Status", "Married", "Not Married"};
    String[] StringArrIsPartyRegister = {"Is Party Registerd", "Yes", "No"};

    final int TAKE_PHOTO_FROM_CAMARA = 0, TAKE_PHOTO_FROM_GALLARY = 1;
    String realPath = "";
    Bitmap ImageBitMap;

    String FinalFirstName = "", FinalMiddleName = "", FinalLastName = "", FinalEmail = "", FinalContactNo = "", FinalBirthday = "", FinalAddress = "",
            FinalIncome = "", FinalFamilyMember = "", FinalAboutYourSelf = "", FinalPoliticalinflunce = "", FinalTaluka = "",
            FinalZipCode = "", FinalGender = "", FinalIsVip = "", FinalVillage = "", FinalMaritalStatus = "", FinalCountry = "", FinalState = "",
            FinalCity = "", FinalIsPartyRegisterd = "", FinalChooseFile = "", Finalprofilephotos = "";

    private static final int PERMISSION_CALLBACK_CONSTANT = 200;
    String[] permissionsRequired = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE};

    boolean fillFormStatus = false;
    SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpjpform);
        context = ActivityGPJPForm.this;
        checkPermission();
        mSPF = getSharedPreferences("AppData", 0);
        mEDT = mSPF.edit();
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        CityList = new ArrayList<>();
        CityListID = new ArrayList<>();
        StateList = new ArrayList<>();
        StateListID = new ArrayList<>();
        CoutnryList = new ArrayList<>();
        CoutnryListID = new ArrayList<>();
        GenderList = new ArrayList<>();
        VIPList = new ArrayList<>();
        MarriedList = new ArrayList<>();
        IsPartyRegisterList = new ArrayList<>();

        form_layout = findViewById(R.id.form_layout);
        tvUploadProfile = findViewById(R.id.tvUploadProfile);
        tv_form_counter = findViewById(R.id.tv_form_counter);
        tvChooseFile = findViewById(R.id.tvChooseFile);
        tvFileName = findViewById(R.id.tvFileName);
        tvBirthday = findViewById(R.id.tvBirthday);
        ll_pdflayout = findViewById(R.id.rlmain);
        etFirstName = findViewById(R.id.etFirstName);
        etMiddleName = findViewById(R.id.etMiddleName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etContactNo = findViewById(R.id.etContactNo);
        etAddress = findViewById(R.id.etAddress);
        etInCome = findViewById(R.id.etInCome);
        etFamilyMember = findViewById(R.id.etFamilyMember);
        etAboutYourSelf = findViewById(R.id.etAboutYourSelf);
        etPoliticalInfluence = findViewById(R.id.etPoliticalInfluence);
        etTaluka = findViewById(R.id.etTaluka);
        et_zip_code = findViewById(R.id.et_zip_code);
        etVillage = findViewById(R.id.etVillage);
        sp_Gender = findViewById(R.id.sp_Gender);
        sp_IsVip = findViewById(R.id.sp_IsVip);
        sp_MaritalStatus = findViewById(R.id.sp_MaritalStatus);
        sp_Country = findViewById(R.id.sp_Country);
        sp_State = findViewById(R.id.sp_State);
        sp_City = findViewById(R.id.sp_City);
        sp_IsPartyRegisterd = findViewById(R.id.sp_IsPartyRegisterd);
        cbAgree = findViewById(R.id.cbAgree);
        ivProfile = findViewById(R.id.ivProfile);

        /* VIPList = new ArrayList<>();
        MarriedList = new ArrayList<>();
        IsPartyRegisterList = new ArrayList<>();*/

        for (int i = 0; i < StringArrGender.length; i++) {
            GenderList.add(StringArrGender[i]);
        }
        for (int i = 0; i < StringArrVIP.length; i++) {
            VIPList.add(StringArrVIP[i]);
        }
        for (int i = 0; i < StringArrMaritalStatus.length; i++) {
            MarriedList.add(StringArrMaritalStatus[i]);
        }
        for (int i = 0; i < StringArrIsPartyRegister.length; i++) {
            IsPartyRegisterList.add(StringArrIsPartyRegister[i]);
        }

        dataAdaptercity = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, CityList);
        dataAdaptercity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dataAdapterState = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, StateList);
        dataAdapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dataAdapterCountry = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, CoutnryList);
        dataAdapterCountry.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dataAdapterGender = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, GenderList);
        dataAdapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_Gender.setAdapter(dataAdapterGender);

        dataAdapterVIP = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, VIPList);
        dataAdapterVIP.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_IsVip.setAdapter(dataAdapterVIP);

        dataAdapterIsPartyRegister = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, IsPartyRegisterList);
        dataAdapterIsPartyRegister.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_IsPartyRegisterd.setAdapter(dataAdapterIsPartyRegister);

        dataAdapterMaritalStatus = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, MarriedList);
        dataAdapterMaritalStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_MaritalStatus.setAdapter(dataAdapterMaritalStatus);

        if (getIntent().hasExtra("data")) {
            UserData = getIntent().getStringExtra("data");
            Type type = new TypeToken<ModelGPJPFormDetail>() {
            }.getType();
            UserFormDetail = new Gson().fromJson(UserData, type);
            setData();
        }

        CSCVolleyRequest("cities");
        CSCVolleyRequest("states");
        CSCVolleyRequest("countries");

        tvChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, TAKE_PHOTO_FROM_GALLARY);
                } else {
                    checkPermission();
                }
            }
        });

        tvBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectDate();
            }
        });

        String counter = Hawk.get(AppConstants.SAVE_COUNTER);

        if (counter!=null){
            tv_form_counter.setText(counter);

        }


        if (getFormStatus())
        //true
        {
            form_layout.setVisibility(View.GONE);
            showAlertDialog();
        } else{
            //false
            form_layout.setVisibility(View.VISIBLE);
        }

        tvUploadProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()) {
                    UploadProfile();
                }
               /* progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Please wait");
                bitmap = loadBitmapFromView(ll_pdflayout, ll_pdflayout.getWidth(), ll_pdflayout.getHeight());
                if (Build.VERSION_CODES.KITKAT < Build.VERSION.SDK_INT) {
                    createPdf();
                }*/
//                        saveBitmap(bitmap);
            }
        });
    }




    private void showAlertDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("You can upload the GPJP form only once");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });


        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void setData() {
        etFirstName.setText(UserFormDetail.getF_name());
        etMiddleName.setText(UserFormDetail.getM_name());
        etLastName.setText(UserFormDetail.getL_name());
        etEmail.setText(UserFormDetail.getEmail());
        etContactNo.setText(UserFormDetail.getPhone_number());
        etAddress.setText(UserFormDetail.getAddress());
        etInCome.setText(UserFormDetail.getIncome());
        etFamilyMember.setText(UserFormDetail.getTotal_family_members());
        etAboutYourSelf.setText(UserFormDetail.getAbout_urself());
        etPoliticalInfluence.setText(UserFormDetail.getPolitical_influence());
        etTaluka.setText(UserFormDetail.getTaluka());
        et_zip_code.setText(UserFormDetail.getZip_code());
        etVillage.setText(UserFormDetail.getVillage());
        tvBirthday.setText(UserFormDetail.getBirthdate());
        Glide.with(context).load("http://www.kelybro.com/upload/gpjp/main/" + UserFormDetail.getProfilephoto()).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(ivProfile);
        //http://www.kelybro.com



        if (UserFormDetail.getGender().equals("0")) {
            sp_Gender.setSelection(1);
        } else {
            sp_Gender.setSelection(2);
        }

        if (UserFormDetail.getIs_vip().equals("1")) {
            sp_IsVip.setSelection(1);
        } else {
            sp_IsVip.setSelection(2);
        }
        if (UserFormDetail.getMarital_status().equals("marride")) {
            sp_MaritalStatus.setSelection(1);
        } else {
            sp_MaritalStatus.setSelection(2);
        }

        if (UserFormDetail.getIs_party_registered().equals("1")) {
            sp_IsPartyRegisterd.setSelection(1);
        } else {
            sp_IsPartyRegisterd.setSelection(2);
        }
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
                                    sp_City.setAdapter(dataAdaptercity);
                                    //dataAdaptercity!!.notifyDataSetInvalidated()
                                } else if (api.equals("states")) {
                                    StateList.add(mData.getString("statename"));
                                    StateListID.add(mData.getString("stateid"));
                                    sp_State.setAdapter(dataAdapterState);
                                    //dataAdapterState!!.notifyDataSetInvalidated()
                                } else if (api.equals("countries")) {
                                    CoutnryList.add(mData.getString("countryname"));
                                    CoutnryListID.add(mData.getString("countryid"));
                                    sp_Country.setAdapter(dataAdapterCountry);
                                    //dataAdapterCountry!!.notifyDataSetInvalidated()
                                }
                            }
                            if (UserFormDetail != null) {
                                for (int i = 0; i < mArray.length(); i++) {
                                    JSONObject mData = mArray.getJSONObject(i);
                                    if (api.equals("cities")) {
                                        if (UserFormDetail.getCity().equals(mData.getString("cityid"))) {
                                            sp_City.setSelection(i);
                                        }
                                    } else if (api.equals("states")) {
                                        if (UserFormDetail.getState().equals(mData.getString("stateid"))) {
                                            sp_State.setSelection(i);
                                        }
                                    } else if (api.equals("countries")) {
                                        if (UserFormDetail.getCountry().equals(mData.getString("countryid"))) {
                                            sp_Country.setSelection(i);
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

    public void UploadProfile() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        FinalFirstName = etFirstName.getText().toString();
        FinalMiddleName = etMiddleName.getText().toString();
        FinalLastName = etLastName.getText().toString();
        FinalEmail = etEmail.getText().toString();
        FinalContactNo = etContactNo.getText().toString();
        FinalBirthday = tvBirthday.getText().toString();
        FinalAddress = etAddress.getText().toString();
        FinalTaluka = etTaluka.getText().toString();
        FinalVillage = etVillage.getText().toString();
        FinalZipCode = et_zip_code.getText().toString();
        FinalIncome = etInCome.getText().toString();
        FinalFamilyMember = etFamilyMember.getText().toString();
        FinalAboutYourSelf = etAboutYourSelf.getText().toString();
        FinalPoliticalinflunce = etPoliticalInfluence.getText().toString();
        if (sp_MaritalStatus.getSelectedItemPosition() != 0) {
            if (sp_MaritalStatus.getSelectedItem().equals(StringArrMaritalStatus[1])) {
                FinalMaritalStatus = "marride";
            } else {
                FinalMaritalStatus = "not marride";
            }
        } else {
            FinalMaritalStatus = "";
        }
        if (sp_Gender.getSelectedItemPosition() != 0) {
            if (sp_Gender.getSelectedItem().equals(StringArrGender[1])) {
                FinalGender = "0";
            } else {
                FinalGender = "1";
            }
        } else {
            FinalGender = "";
        }

        if (sp_IsVip.getSelectedItemPosition() != 0) {
            if (sp_IsVip.getSelectedItem().equals(StringArrVIP[1])) {
                FinalIsVip = "1";
            } else {
                FinalIsVip = "0";
            }
        } else {
            FinalIsVip = "";
        }
        if (sp_IsPartyRegisterd.getSelectedItemPosition() != 0) {
            if (sp_IsPartyRegisterd.getSelectedItem().equals(StringArrIsPartyRegister[1])) {
                FinalIsPartyRegisterd = "1";
            } else {
                FinalIsPartyRegisterd = "0";
            }
        } else {
            FinalIsPartyRegisterd = "";
        }

        for (int i = 0; i < CoutnryList.size(); i++) {
            if (sp_Country.getSelectedItem().toString().equals(CoutnryList.get(i))) {
                FinalCountry = CoutnryListID.get(i);
            }
        }
        for (int i = 0; i < StateList.size(); i++) {
            if (sp_State.getSelectedItem().toString().equals(StateList.get(i))) {
                FinalState = StateListID.get(i);
            }
        }
        for (int i = 0; i < CityList.size(); i++) {
            if (sp_City.getSelectedItem().toString().equals(CityList.get(i))) {
                FinalCity = CityListID.get(i);
            }
        }

        try {
            PdfID = UUID.randomUUID().toString();
            String mURL = getString(R.string.server_url) + "Gpjp/edit_users_data";
            if (UserFormDetail != null) {
                new MultipartUploadRequest(this, PdfID, mURL)
                        .addFileToUpload(realPath, "profilephoto")
                        .addParameter("id", UserFormDetail.getId())
                        .addParameter("f_name", FinalFirstName)
                        .addParameter("m_name", FinalMiddleName)
                        .addParameter("l_name", FinalLastName)
                        .addParameter("email", FinalEmail)
                        .addParameter("phone_number", FinalContactNo)
                        .addParameter("birthdate", FinalBirthday)
                        .addParameter("gender", FinalGender)
                        .addParameter("address", FinalAddress)
                        .addParameter("is_vip", FinalIsVip)
                        .addParameter("marital_status", FinalMaritalStatus)
                        .addParameter("country", FinalCountry)
                        .addParameter("state", FinalState)
                        .addParameter("city", FinalCity)
                        .addParameter("taluka", FinalTaluka)
                        .addParameter("village", FinalVillage)
                        .addParameter("zip_code", FinalZipCode)
                        .addParameter("income", FinalIncome)
                        .addParameter("total_family_members", FinalFamilyMember)
                        .addParameter("about_urself", FinalAboutYourSelf)
                        .addParameter("is_party_registered", FinalIsPartyRegisterd)
                        .addParameter("political_influence", FinalPoliticalinflunce)
                        .addParameter("profilephotos", mSPF.getString("profilephotoName", ""))
                        .addParameter("user_id", mSPF.getString("id", ""))
                        .setNotificationConfig(new UploadNotificationConfig().setClickIntent(new Intent(context, MainActivity.class)).setClearOnAction(true).setIcon(R.mipmap.ic_launcher_round))
                        .setMaxRetries(5)
                        .startUpload();
                progressDialog.dismiss();
                Toast.makeText(context, "Successfully Updated...", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                new MultipartUploadRequest(this, PdfID, mURL)
                        .addFileToUpload(realPath, "profilephoto")
                        .addParameter("f_name", FinalFirstName)
                        .addParameter("m_name", FinalMiddleName)
                        .addParameter("l_name", FinalLastName)
                        .addParameter("email", FinalEmail)
                        .addParameter("phone_number", FinalContactNo)
                        .addParameter("birthdate", FinalBirthday)
                        .addParameter("gender", FinalGender)
                        .addParameter("address", FinalAddress)
                        .addParameter("is_vip", FinalIsVip)
                        .addParameter("marital_status", FinalMaritalStatus)
                        .addParameter("country", FinalCountry)
                        .addParameter("state", FinalState)
                        .addParameter("city", FinalCity)
                        .addParameter("taluka", FinalTaluka)
                        .addParameter("village", FinalVillage)
                        .addParameter("zip_code", FinalZipCode)
                        .addParameter("income", FinalIncome)
                        .addParameter("total_family_members", FinalFamilyMember)
                        .addParameter("about_urself", FinalAboutYourSelf)
                        .addParameter("is_party_registered", FinalIsPartyRegisterd)
                        .addParameter("political_influence", FinalPoliticalinflunce)
                        .addParameter("profilephotos", mSPF.getString("profilephotoName", ""))
                        .addParameter("user_id", mSPF.getString("id", ""))
                        .setNotificationConfig(new UploadNotificationConfig().setClickIntent(new Intent(context, MainActivity.class)).setClearOnAction(true).setIcon(R.mipmap.ic_launcher_round))
                        .setMaxRetries(5)
                        .startUpload();
                progressDialog.dismiss();
                Toast.makeText(context, "Successfully Uploaded...", Toast.LENGTH_SHORT).show();
                form_layout.setVisibility(View.GONE);
                setFormStatus(true);

                finish();
            }

        } catch (Exception exception) {
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
        /*try {
            EditPDFFile();
        } catch (IOException e) {
            e.printStackTrace();
            progressDialog.dismiss();
        } catch (DocumentException e) {
            e.printStackTrace();
            progressDialog.dismiss();
        }*/
    }

    private void setFormStatus(boolean status) {
        prefs.edit().putBoolean("formStatus", status).apply();

    }
    private boolean getFormStatus(){
        return prefs.getBoolean("formStatus", false);
    }

    public void SelectDate() {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        tvBirthday.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    public void EditPDFFile() throws IOException, DocumentException {

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        PdfReader reader = null;
        try {
            reader = new PdfReader(getResources().openRawResource(R.raw.gpjp_form));
        } catch (IOException e) {
            e.printStackTrace();
            progressDialog.dismiss();
        }

        AcroFields fields = reader.getAcroFields();

       /* Set<String> fldNames = fields.getFields().keySet();

        for (String fldName : fldNames) {
            System.out.println(fldName + ": " + fields.getField(fldName));
        }*/

        File pdfFolder = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "KelyBro");
        if (!pdfFolder.exists()) {
            pdfFolder.mkdir();
            Log.i("GPJP FORM", "Pdf Directory created");
        }

        Date date = new Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);

        File myFile = new File(pdfFolder + timeStamp + ".pdf");

        OutputStream output = new FileOutputStream(myFile);

//        Base64.OutputStream output = null;
//        PdfReader reader = new PdfReader(getResources().openRawResource(R.raw.template3));
        PdfStamper stamper = new PdfStamper(reader, output);
        AcroFields acroFields = stamper.getAcroFields();

        acroFields.setField("FNameF", FinalFirstName);
        acroFields.setField("MNameF", FinalMiddleName);
        acroFields.setField("LNameF", FinalLastName);
        acroFields.setField("ContactF", FinalContactNo);
        acroFields.setField("BirthdateF", FinalBirthday);
        acroFields.setField("AddressF", FinalAddress);
        acroFields.setField("MarriedF", sp_MaritalStatus.getSelectedItem().toString());
        acroFields.setField("GenderF", sp_Gender.getSelectedItem().toString());
        acroFields.setField("CountryF", sp_Country.getSelectedItem().toString());
        acroFields.setField("StateF", sp_State.getSelectedItem().toString());
        acroFields.setField("JilloF", sp_City.getSelectedItem().toString());
        acroFields.setField("TalukaF", FinalTaluka);
        acroFields.setField("VillageF", FinalVillage);
        acroFields.setField("ZipCodeF", FinalZipCode);
        acroFields.setField("FamilyMemberF", FinalFamilyMember);
        acroFields.setField("incomeF", FinalIncome);
        acroFields.setField("PoliticalInfluenceF", FinalPoliticalinflunce);
        acroFields.setField("AboutYourF", FinalAboutYourSelf);


        InputStream ims = new FileInputStream(new File(realPath));
        Bitmap bmp = BitmapFactory.decodeStream(ims);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        Image image = Image.getInstance(stream.toByteArray());

        PushbuttonField ad = acroFields.getNewPushbuttonFromField("Button3");
        ad.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
        ad.setProportionalIcon(true);
        ad.setImage(image);
        acroFields.replacePushbuttonField("Button3", ad.getField());
//        stamper.close();
//        reader.close();

        stamper.setFormFlattening(true);
        stamper.close();


        progressDialog.dismiss();
        finish();
//        form.setField("address", customer.getStreetAddress());
//        form.setField("city", customer.getCity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("FragmentA.java", "onActivityResult called");
        switch (requestCode) {
            case TAKE_PHOTO_FROM_CAMARA:
                if (resultCode == RESULT_OK) {
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    File destination = new File(Environment.getExternalStorageDirectory(),
                            System.currentTimeMillis() + ".jpg");
                    destination.getPath();
                    FileOutputStream fo;
                    try {
                        destination.createNewFile();
                        fo = new FileOutputStream(destination);
                        fo.write(bytes.toByteArray());
                        fo.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    ivPhoto.setImageBitmap(thumbnail);
                    realPath = destination.getAbsolutePath();
                }
                break;
            case TAKE_PHOTO_FROM_GALLARY:
                if (resultCode == RESULT_OK) {
                    Bitmap bm = null;
                    if (data != null) {
                        try {
                            bm = MediaStore.Images.Media.getBitmap(context.getApplicationContext().getContentResolver(), data.getData());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ImageBitMap = bm;
                        realPath = getPath(data.getData());
                        String[] spfile = realPath.split("/");
                        tvFileName.setText(spfile[spfile.length - 1]);
                        ivProfile.setImageBitmap(bm);
                    }
                }
                break;
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public boolean validation() {
        if (etFirstName.getText().toString().equals("")) {
            Toast.makeText(context, "Please Enter First Name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (etMiddleName.getText().toString().equals("")) {
            Toast.makeText(context, "Please Enter Middle Name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (etLastName.getText().toString().equals("")) {
            Toast.makeText(context, "Please Enter Last Name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (etEmail.getText().toString().equals("")) {
            Toast.makeText(context, "Please Enter Email", Toast.LENGTH_SHORT).show();
            return false;
        } else if (tvBirthday.getText().toString().equals("")) {
            Toast.makeText(context, "Please Enter Birthday", Toast.LENGTH_SHORT).show();
            return false;
        } else if (etContactNo.getText().toString().equals("")) {
            Toast.makeText(context, "Please Enter Contact No", Toast.LENGTH_SHORT).show();
            return false;
        } else if (sp_Gender.getSelectedItemPosition() == 0) {
            Toast.makeText(context, "Please Select Gender", Toast.LENGTH_SHORT).show();
            return false;
        } else if (etAddress.getText().toString().equals("")) {
            Toast.makeText(context, "Please Enter Address", Toast.LENGTH_SHORT).show();
            return false;
        } else if (sp_IsVip.getSelectedItemPosition() == 0) {
            Toast.makeText(context, "Please Select Is Vip", Toast.LENGTH_SHORT).show();
            return false;
        } else if (etTaluka.getText().toString().equals("")) {
            Toast.makeText(context, "Please Enter Taluka", Toast.LENGTH_SHORT).show();
            return false;
        } else if (etVillage.getText().toString().equals("")) {
            Toast.makeText(context, "Please Enter Village", Toast.LENGTH_SHORT).show();
            return false;
        } else if (et_zip_code.getText().toString().equals("")) {
            Toast.makeText(context, "Please Enter ZipCode", Toast.LENGTH_SHORT).show();
            return false;
        } else if (etInCome.getText().toString().equals("")) {
            Toast.makeText(context, "Please Enter Income", Toast.LENGTH_SHORT).show();
            return false;
        } else if (etFamilyMember.getText().toString().equals("")) {
            Toast.makeText(context, "Please Enter Family Member", Toast.LENGTH_SHORT).show();
            return false;
        } else if (etAboutYourSelf.getText().toString().equals("")) {
            Toast.makeText(context, "Please Enter About Your Self", Toast.LENGTH_SHORT).show();
            return false;
        } else if (sp_IsPartyRegisterd.getSelectedItemPosition() == 0) {
            Toast.makeText(context, "Please Select Is Party Registered", Toast.LENGTH_SHORT).show();
            return false;
        } else if (etPoliticalInfluence.getText().toString().equals("")) {
            Toast.makeText(context, "Please Enter Political Influence", Toast.LENGTH_SHORT).show();
            return false;
        } else if (realPath.equals("")) {
            Toast.makeText(context, "Please Select Photo", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!cbAgree.isChecked()) {
            Toast.makeText(context, "Please Agree Term and Condition", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public void checkPermission() {
        if (ActivityCompat.checkSelfPermission(context, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permissionsRequired[1])) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions((Activity) context, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
//                        onBackPressed();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions((Activity) context, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case 200: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    onBackPressed();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
