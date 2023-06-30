package com.semicolon.newsapp.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.jaeger.library.StatusBarUtil
import com.semicolon.newsapp.MainActivity
import com.semicolon.newsapp.R
import com.semicolon.newsapp.databinding.ActivityLoginBinding
import com.semicolon.newsapp.helper.HelperClass
import com.semicolon.newsapp.helper.SharedPref
import com.semicolon.newsapp.model.LoginModel
import com.semicolon.newsapp.network.ApiConfig
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {

    lateinit var binding:ActivityLoginBinding
    lateinit var pref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        HelperClass().hideBar(this)
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.white),0)

        mainButton()
    }

    private fun mainButton(){
        pref = SharedPref(this)
        binding.btnLogin.setOnClickListener {
            //Toast.makeText(this,"tombol diklik",Toast.LENGTH_SHORT).show()
            if (validation()){
                login()
            }
        }
    }

    private fun validation():Boolean {
        if (binding.edtUsername.text.isEmpty()){
            Toasty.error(this, "username tidak boleng kosong", Toast.LENGTH_SHORT, true).show();
            //Toast.makeText(this,"username tidak boleng kosong",Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.edtPassword.text.isEmpty()){
            Toast.makeText(this,"password tidak boleng kosong",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun login(){
        val dialog = ProgressDialog.show(this,"Loading","Please Wait ...",true)
        dialog.show()
        ApiConfig.getInstanceRetrofit().login(binding.edtUsername.text.toString(),
        binding.edtPassword.text.toString()).enqueue(object : Callback<LoginModel>{
            override fun onResponse(call: Call<LoginModel>, response: Response<LoginModel>) {
                if (response.isSuccessful){
                    dialog.dismiss()
                    val data = response.body()
                    if (data?.error == false){
                        HelperClass().saveDataLogin(pref,data)
                        Toasty.info(this@LoginActivity, data.message.toString(), Toast.LENGTH_SHORT, true).show()
                        startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                        finish()
                        return
                    }
                    Toasty.error(this@LoginActivity, data?.message.toString(), Toast.LENGTH_SHORT, true).show()
                }
            }

            override fun onFailure(call: Call<LoginModel>, t: Throwable) {
                dialog.dismiss()
                Toasty.error(this@LoginActivity, t.message.toString(), Toast.LENGTH_SHORT, true).show()
            }
        })
    }
}