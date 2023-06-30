package com.semicolon.newsapp.helper

import android.app.Activity
import android.os.Build
import android.view.View
import com.google.gson.Gson
import com.semicolon.newsapp.model.LoginModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar

class HelperClass {

    fun hideBar(context: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decor = context.window.decorView
            decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    fun convertDate(date: String): String {
        var hasil = ""

        val formatLama = "yyyy-MM-dd HH:mm:ss"
        val formatBaru = "MMM dd yyyy"

        val sdf = SimpleDateFormat(formatLama)
        try {
            val dd = sdf.parse(date)
            sdf.applyPattern(formatBaru)
            hasil = sdf.format(dd!!)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return hasil
    }

    fun getTimeNow(): String {
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

        return sdf.format(c.time)
    }

    fun saveDataLogin(pref: SharedPref,data:LoginModel){
        pref.saveBoolean(pref.LOGIN,true)
        val json = Gson().toJson(data)
        pref.saveString(pref.DATALOGIN,json)
    }

    fun getDataLogin(pref: SharedPref):LoginModel{
        val json = pref.getDataLogin()
        return  Gson().fromJson(json, LoginModel::class.java)
    }
}