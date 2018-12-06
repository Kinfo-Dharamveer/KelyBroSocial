package com.kelybro.android.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.kelybro.android.R;

/**
 * Created by Krishna on 15-02-2018.
 */

public class NewsActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
    }
}
