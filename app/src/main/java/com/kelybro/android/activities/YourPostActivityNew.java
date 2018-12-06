package com.kelybro.android.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.kelybro.android.R;
import com.kelybro.android.adapters.SelectedPostImage;
import com.kelybro.android.customviews.Utility;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pl.droidsonroids.gif.GifDrawable;

import static com.android.volley.toolbox.Volley.newRequestQueue;

/**
 * Created by Krishna on 22-03-2018.
 */

public class YourPostActivityNew extends Activity {

    JSONObject mOBJECT;
    private RequestQueue queue;
    SharedPreferences mSPF;
    SharedPreferences.Editor mEDT;
    private ImageView yp_iv_user_photo;
    private EditText edt_user_post_txt;
    private TextView yp_tv_user_name;
    private TextView tv_youtube_link;
    private ImageView you_post_image;
    private LinearLayout ll_your_post;
    private LinearLayout ll_Image_post;
    private LinearLayout ll_gif_post;
    private LinearLayout ll_video_post;
    private TextView tv_send;
    private String userChoosenTask;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private RecyclerView rv_selected_post_image;
    private SelectedPostImage postImageAdapter;
    private ArrayList<String> PostImages= new ArrayList<>();
    private ArrayList<Bitmap> PostImagesBitmap= new ArrayList<>();
    private ProgressDialog pDialog;
    private Bitmap bitmap;
    private String mType="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_post);
        mSPF = getSharedPreferences("AppData", 0);
        mEDT = mSPF.edit();
        yp_iv_user_photo = findViewById(R.id.yp_iv_user_photo);
        edt_user_post_txt = findViewById(R.id.edt_user_post_txt);
        yp_tv_user_name = findViewById(R.id.yp_tv_user_name);
        tv_youtube_link = findViewById(R.id.tv_youtube_link);
        you_post_image = findViewById(R.id.you_post_image);
        ll_Image_post = findViewById(R.id.ll_Image_post);
        ll_gif_post = findViewById(R.id.ll_gif_post);
        ll_video_post = findViewById(R.id.ll_video_post);
        tv_send = findViewById(R.id.tv_send);
        rv_selected_post_image = findViewById(R.id.rv_post_image);

        Glide.with(this)
                .load(mSPF.getString("profilephoto",""))
                .error(R.drawable.ic_avatar)
                .into(yp_iv_user_photo);

        yp_tv_user_name.setText(mSPF.getString("f_name"," ")+" "+mSPF.getString("l_name",""));
        ll_Image_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mType="PHOTO";
                rv_selected_post_image.setVisibility(View.VISIBLE);
                PostImages.clear();
                PostImagesBitmap.clear();
                selectImage();
            }
        });
        ll_gif_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mType="GIF";
                PostImages.clear();
                PostImagesBitmap.clear();
                rv_selected_post_image.setVisibility(View.VISIBLE);
                String[] mimeTypes = {"image/gif"};
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
            }
        });
        ll_video_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mType="YOUTUBE";
                PostImages.clear();
                PostImagesBitmap.clear();
                rv_selected_post_image.setVisibility(View.GONE);
                tv_youtube_link.setVisibility(View.VISIBLE);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/")));
                finish();
            }
        });
        tv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mType.equals("YOUTUBE")) {
                    Log.e("Krishna",tv_youtube_link.getText().subSequence(0,17).toString());
                    if(tv_youtube_link.getText().subSequence(0,17).equals("https://youtu.be/"))
                        MakeVolleyNewUserPostRequest();
                    else
                        Toast.makeText(getApplicationContext(),"Only share youtube link.",Toast.LENGTH_SHORT).show();
                }else{
                    MakeVolleyNewUserPostRequest();
                }
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value1 = extras.getString(Intent.EXTRA_TEXT);
            mType="YOUTUBE";
            tv_youtube_link.setText(value1);
            tv_youtube_link.setVisibility(View.VISIBLE);
            rv_selected_post_image.setVisibility(View.GONE);
            tv_youtube_link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=Hxy8BZGQ5Jo")));
                }
            });

        }


        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
        rv_selected_post_image.setLayoutManager(layoutManager);
        postImageAdapter = new SelectedPostImage(PostImagesBitmap,getApplicationContext());
        rv_selected_post_image.setAdapter(postImageAdapter);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library"};

        AlertDialog.Builder builder = new AlertDialog.Builder(YourPostActivityNew.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(YourPostActivityNew.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        String[] mimeTypes = {"image/jpeg", "image/png"};
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        byte[] imageBytes = bytes.toByteArray();
        PostImages.add(Base64.encodeToString(imageBytes, Base64.DEFAULT));
        PostImagesBitmap.add(thumbnail);
        postImageAdapter.notifyDataSetChanged();
    }

    private void onSelectFromGalleryResult(Intent data) {
        rv_selected_post_image.setVisibility(View.VISIBLE);
        ArrayList<Uri> images = new ArrayList();
        ClipData clipData = data.getClipData();
        if (clipData != null) {
            for (int i = 0; i < clipData.getItemCount(); i++) {
                ClipData.Item item = clipData.getItemAt(i);
                images.add(item.getUri());
            }
        }else{
            try {
                //if(mType=="PHOTO") {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageBytes = baos.toByteArray();
                    PostImagesBitmap.add(bitmap);
                    PostImages.add(Base64.encodeToString(imageBytes, Base64.DEFAULT));
//                }else{
//                    Log.e("Krishna","Hiii");
//                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.WEBP, 100, baos);
//                    byte[] imageBytes = baos.toByteArray();
//                    PostImagesBitmap.add(bitmap);
//                    PostImages.add(Base64.encodeToString(imageBytes, Base64.DEFAULT));
//                }

            }catch (Exception e){
                Log.e("Post Images",e.getMessage());
            }
        }

        for (int i=0;i<images.size();i++) {
            try {
                if(mType=="PHOTO") {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), images.get(i));
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageBytes = baos.toByteArray();
                    PostImagesBitmap.add(bitmap);
                    PostImages.add(Base64.encodeToString(imageBytes, Base64.DEFAULT));
                }else{
                    // my GifDrawable
                    GifDrawable gifFromBytes = new GifDrawable(getContentResolver(),images.get(i));
                    // I create a new blank Bitmap with the same dimensions as the gif
                    Bitmap bs = Bitmap.createBitmap(gifFromBytes.getMinimumWidth(), gifFromBytes.getMinimumHeight(), Bitmap.Config.RGB_565);
                    // I create a new canvas and add my bitmap to it
                    Canvas c = new Canvas(bs);
                    // I start playing my GIF and draw it to the canvas, then get rid of the gifdrawable
                    gifFromBytes.start();
                    gifFromBytes.draw(c);
                    gifFromBytes.recycle();

                    ByteArrayOutputStream bAOSThumbnail = new ByteArrayOutputStream();
                    // I then compress the resulting bitmap into a ByteArrayOutputStream
                    bs.compress(Bitmap.CompressFormat.WEBP, 100 /* ignored for PNG */, bAOSThumbnail);
                    // At this point the bitmap will contain the first frame of the Gif
                    byte[] imageBytes = bAOSThumbnail.toByteArray();
                    PostImagesBitmap.add(bs);
                    PostImages.add(Base64.encodeToString(imageBytes, Base64.DEFAULT));
                }
            }catch (Exception e){
                Log.e("Post Images",e.getMessage());
            }
        }
        postImageAdapter.notifyDataSetChanged();
    }

    private void MakeVolleyNewUserPostRequest() {
        showpDialog();
        String mURL = getString(R.string.server_url) + "Users/new_userpost";
        StringRequest req = new StringRequest(Request.Method.POST, mURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.e("NewUserPostResponse", s);
                try {
                    mOBJECT = new JSONObject(s);
                    if (mOBJECT.getBoolean("success")) {

                        Toast.makeText(getApplicationContext(), mOBJECT.getString("message"), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), mOBJECT.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                    hidepDialog();;
                } catch (Exception e) {
                    Log.e("Error", e.toString());
                    hidepDialog();
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
                map.put("user_id",  mSPF.getString("id",""));
                map.put("post_text", edt_user_post_txt.getText().toString());
                map.put("type", mType);
                map.put("images", PostImages.toString());
                map.put("video_link", tv_youtube_link.getText().toString());
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

}
