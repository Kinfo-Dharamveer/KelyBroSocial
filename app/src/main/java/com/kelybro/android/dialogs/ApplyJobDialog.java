package com.kelybro.android.dialogs;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kelybro.android.model.FilePath;
import com.kelybro.android.R;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.android.volley.toolbox.Volley.newRequestQueue;

/**
 * Created by Krishna on 12-02-2018.
 */

public class ApplyJobDialog extends Activity {
    JSONObject mOBJECT;
    SharedPreferences mSPF;
    SharedPreferences.Editor mEDT;
    private Button SelectButton, UploadButton;
    private TextView txt_filename;
    private File myFile;
    public int PDF_REQ_CODE = 1;
    Uri uri;
    String PdfNameHolder, PdfPathHolder, PdfID;
    EditText PdfNameEditText;
    String JobId = "";
    public static final String PDF_UPLOAD_HTTP_URL = "http://androidblog.esy.es/AndroidJSon/file_upload.php";

//    Button SelectButton, UploadButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.job_apply_dialog);

        mSPF = getSharedPreferences("AppData", 0);
        mEDT = mSPF.edit();

        JobId = getIntent().getStringExtra("JobId");

        AllowRunTimePermission();
//        MakeVolleyRequestUploadResume();
        SelectButton = (Button) findViewById(R.id.btn_browse_pdf_id);
        UploadButton = (Button) findViewById(R.id.btn_submit_pdf_id);
//        txt_filename = (TextView) findViewById(R.id.txt_filename);
        PdfNameEditText = (EditText) findViewById(R.id.editText);


        SelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PDF_REQ_CODE);
//                startActivity(intent);
//                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/example.pdf");
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setDataAndType(Uri.fromFile(file), "application/pdf");
//                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                getContext().startActivity(intent);

            }
        });

        UploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PdfUploadFunction();
//                MakeVolleyRequestUploadResume();
            }
        });


    }

    private void MakeVolleyRequestUploadResume() {

        String mURL = getString(R.string.server_url) + "Job/new_job_request";
        StringRequest req = new StringRequest(Request.Method.POST, mURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.e("NewJobResponse", s);
                try {
                    mOBJECT = new JSONObject(s);
                    if (mOBJECT.getBoolean("success")) {

                        mOBJECT.getString("id");

                        JSONObject data = mOBJECT.getJSONObject("data");
                        mEDT.putString("job_id", data.getString("job_id"));
                        mEDT.putString("user_id", data.getString("user_id"));
                        mEDT.putString("resume", data.getString("resume"));
                        mEDT.putString("status", data.getString("status"));
                        mEDT.commit();

                        Toast.makeText(getApplicationContext(), mOBJECT.getString("message"), Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(getApplicationContext(), ApplyJobDialog.class);
//                        startActivity(intent);
//                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), mOBJECT.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("Error", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("Error", volleyError.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("job_id", JobId);
                map.put("user_id", mSPF.getString("id", ""));
                map.put("resume", PdfPathHolder);
                return map;
            }
        };
        RequestQueue queue = newRequestQueue(getApplicationContext());
        queue.add(req);
        req.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    /*public void UploadFile(){
        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        Toast.makeText(getApplicationContext(), R.string.alert_comment_sukses, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        smr.addStringParam("param string", " data text");
        smr.addFile("param file", imagePath);
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PDF_REQ_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            uri = data.getData();

            // After selecting the PDF set PDF is Selected text inside Button.
            SelectButton.setText("PDF is Selected");
        }

    }

    public void PdfUploadFunction() {

        PdfNameHolder = PdfNameEditText.getText().toString().trim();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            PdfPathHolder = FilePath.getRealPathFromURI_API19(this, uri);
        } else {
            PdfPathHolder = FilePath.getPath(this, uri);
        }

        if (PdfPathHolder == null) {

            Toast.makeText(this, "Please move your PDF file to internal storage & try again.", Toast.LENGTH_LONG).show();

        } else {

            try {

                PdfID = UUID.randomUUID().toString();
                String mURL = getString(R.string.server_url) + "Job/new_job_request";

                new MultipartUploadRequest(this, PdfID, mURL)
                        .addFileToUpload(PdfPathHolder, "resume")
                        .addParameter("name", PdfNameHolder)
                        .addParameter("job_id", JobId)
                        .addParameter("user_id", mSPF.getString("id", ""))
                        .setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(5)
                        .startUpload();

            } catch (Exception exception) {
                Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void AllowRunTimePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(ApplyJobDialog.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            Toast.makeText(ApplyJobDialog.this, "READ_EXTERNAL_STORAGE permission Access Dialog", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(ApplyJobDialog.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] Result) {

        switch (RC) {

            case 1:

                if (Result.length > 0 && Result[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(ApplyJobDialog.this, "Permission Granted", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(ApplyJobDialog.this, "Permission Canceled", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }


}
