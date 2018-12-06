package com.kelybro.android.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import com.kelybro.android.R

/**
 * Created by Krishna on 08-02-2018.
 */

class SplashScreenActivity : Activity() {
    internal lateinit var mSPF: SharedPreferences
    internal lateinit var mEDT: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        mSPF = getSharedPreferences("AppData", 0)
        mEDT = mSPF.edit()

        val timerThread = object : Thread() {
            override fun run() {
                try {
                    Thread.sleep(3000)

                    if (mSPF.getString("id","") == "") {
                        val i = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(i)
                        finish()
                    } else {
                        val i = Intent(applicationContext, MainActivity::class.java)
                        startActivity(i)
                        finish()
                    }

                } catch (i: Exception) {
                    i.printStackTrace()
                }

            }
        }

//        val appUpdaterUtils = AppUpdaterUtils(this)
//        appUpdaterUtils.setUpdateFrom(UpdateFrom.GOOGLE_PLAY).withListener(object : AppUpdaterUtils.UpdateListener {
//            override fun onSuccess(update: Update, isUpdateAvailable: Boolean?) {
//                if (isUpdateAvailable!!) {
//                    if (isOnline())
//                        timerThread.start()
//                } else {
//                    AlertDialog.Builder(this@SplashScreenActivity)
//                            .setTitle(resources.getString(R.string.app_name))
//                            .setMessage("New update available in Play Store. Please update first.")
//                            .setPositiveButton("UPDATE") { dialogInterface, i -> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName))) }
//                            .setNegativeButton("CANCEL") { dialogInterface, i -> finish() }
//                            .setCancelable(false)
//                            .show()
//                }
//            }
//
//            override fun onFailed(appUpdaterError: AppUpdaterError) {
//                Log.d("AppUpdater Error", "Something went wrong")
//            }
//        })
//        appUpdaterUtils.start()
        if (isOnline())
            timerThread.start()

    }

    private fun isOnline(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        if (netInfo != null && netInfo.isConnectedOrConnecting) {
            return true
        } else {
            AlertDialog.Builder(this@SplashScreenActivity)
                    .setTitle(resources.getString(R.string.app_name))
                    .setMessage("No internet connection found.")
                    .setPositiveButton("OK") { dialogInterface, i -> finish() }
                    .setCancelable(false)
                    .show()
        }
        return false
    }
}
