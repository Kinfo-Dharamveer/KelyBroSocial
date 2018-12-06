package com.kelybro.android.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.kelybro.android.R

/**
 * Created by Krishna on 15-02-2018.
 */

class VerifyEmailActivity : Activity() {

    private var vfe_tv_yes: TextView? = null
    private var vfe_tv_no: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verified_email)

        vfe_tv_yes = findViewById<View>(R.id.vfe_tv_yes) as TextView
        vfe_tv_yes!!.setOnClickListener {

            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        vfe_tv_no!!.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}
