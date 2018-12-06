package com.kelybro.android.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.kelybro.android.adapters.SellitemViewPagerAdapter;
import com.kelybro.android.customviews.mUtitily;
import com.kelybro.android.R;
import com.kelybro.android.activities.UserProfileActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Krishna on 07-04-2018.
 */

public class SellingItemDialog extends Dialog {
    Context context;
    List<String> sellingDatalists = new ArrayList<String>();
    ArrayList<String> postImageList = new ArrayList<String>();
    de.hdodenhof.circleimageview.CircleImageView profile_image;
    TextView text_Username;
    TextView tv_post_date;
    ImageView iv_post_delete;
    TextView post_text;
    ViewPager viewPager;
    LinearLayout sliderDotspanel,lyn_main ;
    mUtitily mUtil;
    private int dotscount;
    private ImageView[] dots;
    private SharedPreferences mSPF;
    public SellingItemDialog(@NotNull Context mcc, @NotNull List<String> sellingDatalists, @NotNull ArrayList<String> postImageList) {
        super(mcc);
        this.context=mcc;
        this.sellingDatalists = sellingDatalists;
        this.postImageList = postImageList;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.row_sellitem_view);

        mUtil= new mUtitily(context);
        mSPF = context.getSharedPreferences("AppData", 0);
        profile_image = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.profile_image);
        text_Username = (TextView) findViewById(R.id.text_Username);
        tv_post_date = (TextView) findViewById(R.id.tv_post_date);
        post_text = (TextView) findViewById(R.id.post_text);
        iv_post_delete = (ImageView) findViewById(R.id.iv_post_delete);
        viewPager = (ViewPager) findViewById(R.id.rh_viewPager);
        sliderDotspanel  = (LinearLayout) findViewById(R.id.rh_SliderDots);
        lyn_main = (LinearLayout) findViewById(R.id.lyn_main);
        lyn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, UserProfileActivity.class);
                i.putExtra("UserID", mSPF.getString("id",""));
                i.putExtra("ProfileID", sellingDatalists.get(1));
                context.startActivity(i);
                dismiss();
            }
        });
        text_Username.setText(sellingDatalists.get(12));
        Glide.with(context).load(sellingDatalists.get(13)).error(R.drawable.ic_avatar).into(profile_image);
        tv_post_date.setText(mUtil.getDateDifference(sellingDatalists.get(11)));
        post_text.setText(sellingDatalists.get(4));
        if (sellingDatalists.get(1).equals(mSPF.getString("id",""))){
            iv_post_delete.setVisibility(View.VISIBLE);
            iv_post_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Are you sure? You want to delete product.")
                            .setPositiveButton("Yes", new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    DeleteMyPost(sellingDatalists.get(0));
                                }
                            })
                            .setNegativeButton("No", new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dismiss();
                                }
                            }).show();

                }
            });
        }
        if (sellingDatalists.get(15) == "1")
            profile_image.setBackgroundResource(R.drawable.rounded_image_border_corner);


        SellitemViewPagerAdapter viewPagerAdapter = new SellitemViewPagerAdapter(context,postImageList);

        viewPager.setAdapter(viewPagerAdapter);

        dotscount = viewPagerAdapter.getCount();
        dots = new ImageView[dotscount];

        for(int i = 0; i < dotscount; i++){

            dots[i] = new ImageView(context);
            dots[i].setImageDrawable(ContextCompat.getDrawable(context, R.drawable.round_non_dots));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            sliderDotspanel.addView(dots[i], params);

        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_dots));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for(int i = 0; i< dotscount; i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(context, R.drawable.round_non_dots));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_dots));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void DeleteMyPost(final String postion){
        String mURL = context.getString(R.string.server_url)+"Posts/delete_posts";
        StringRequest mREQ = new StringRequest(Request.Method.POST, mURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.e("Krishna Delete",s);
                try{
                    JSONObject mOBJECT = new JSONObject(s);
                    if(mOBJECT.getBoolean("success"))
                        dismiss();
                    Toast.makeText(context,mOBJECT.getString("message"),Toast.LENGTH_SHORT).show();
                }
                catch (Exception e)
                {
                    Log.e("Error",e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("Error",volleyError.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("user_id",mSPF.getString("id",""));
                map.put("post_type","3");
                map.put("post_id",postion);
                return map;
            }
        };
        Volley.newRequestQueue(context).add(mREQ);
    }


}





