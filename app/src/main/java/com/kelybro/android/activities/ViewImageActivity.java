package com.kelybro.android.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.kelybro.android.R;
import com.kelybro.android.zoomable.PhotoDraweeView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;

import org.json.JSONObject;

import static com.android.volley.toolbox.Volley.newRequestQueue;

/**
 * Created by Krishna on 21-03-2018.
 */

public class ViewImageActivity extends AppCompatActivity {
PhotoDraweeView pf_view_image;
    SharedPreferences mSPF;
    SharedPreferences.Editor mEDT;
    JSONObject mOBJECT;
    JSONObject SOBJECT;
    private Toolbar ma_toolbar;
    String ProfilePic = "";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        Intent i = getIntent();
        ProfilePic = i.getStringExtra("ImagePath");
        ma_toolbar = (Toolbar) findViewById(R.id.ma_toolbar);
        ma_toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        setSupportActionBar(ma_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");


        mSPF = getSharedPreferences("AppData", 0);
        mEDT = mSPF.edit();

        pf_view_image=findViewById(R.id.pf_view_image);

        PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
        controller.setUri(ProfilePic);
        controller.setOldController(pf_view_image.getController());
        controller.setControllerListener(new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
                if (imageInfo == null || pf_view_image == null) {
                    return;
                }
                pf_view_image.update(imageInfo.getWidth(), imageInfo.getHeight());
            }
        });
        pf_view_image.setController(controller.build());
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
