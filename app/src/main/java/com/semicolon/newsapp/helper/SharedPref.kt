package com.semicolon.newsapp.helper

import android.content.Context
import android.content.SharedPreferences

class SharedPref(context:Context) {

    val APPS = "newsapp"
    val LOGIN = "login"
    var DATALOGIN = "datalogin"

    var context:Context
    var Shared: SharedPreferences
    var editor: SharedPreferences.Editor

    init {
        this.context = context
        Shared = context.getSharedPreferences(APPS, Context.MODE_PRIVATE)
        editor = Shared.edit()
    }

    fun saveBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value)
        editor.commit()
    }

    fun saveString(key: String, value: String) {
        editor.putString(key, value)
        editor.commit()
    }

    fun clearAll() {
        editor.clear()
        editor.commit()
    }

    fun getLOGIN(): Boolean {
        return Shared.getBoolean(LOGIN, false)
    }

    fun getDataLogin():String? {
        return Shared.getString(DATALOGIN, "")
    }

}