package com.semicolon.newsapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import com.jaeger.library.StatusBarUtil
import com.semicolon.newsapp.MainActivity
import com.semicolon.newsapp.R
import com.semicolon.newsapp.helper.HelperClass
import com.semicolon.newsapp.helper.SharedPref

class SplashActivity : AppCompatActivity() {

    lateinit var pref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        main()
    }

    private fun main(){
        supportActionBar?.hide()
        StatusBarUtil.setColor(this,ContextCompat.getColor(this,R.color.white),0)
        HelperClass().hideBar(this)
        pref = SharedPref(this)
        Handler(Looper.getMainLooper()).postDelayed({
            gotoActivity()
        },3000)
    }

    private fun gotoActivity(){
        if (pref.getLOGIN()){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }else{
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }
    }
}