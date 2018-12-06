package com.kelybro.android.activities;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.kelybro.android.R;
import com.kelybro.android.fragment.ProfileFragment;


/**
 * Created by Krishna on 02-02-2018.
 */

public class ProfileFollowingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_following);

        FragmentManager fragmentManager = ProfileFollowingActivity.this.getFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        ProfileFragment fragment = new ProfileFragment();
        fragmentTransaction.add(R.id.frame_container, fragment); //ERROR ON THIS LINE
        fragmentTransaction.commit();

//        FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        ProfileFragment profileFragment = new ProfileFragment();
//        fragmentTransaction.add(R.id.frame_container_main, ProfileFragment, "profileFragment");
//        fragmentTransaction.commit();

//        FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        ProfileFragment fragment = new ProfileFragment();
//        fragmentTransaction.add(R.id.fragment_container, fragment);
//        fragmentTransaction.commit();

//        ProfileFragment profileFragment = new ProfileFragment();
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        transaction.add(R.id.frame_container, profileFragment, "profileFragment");
//        transaction.commit();

    }
}
