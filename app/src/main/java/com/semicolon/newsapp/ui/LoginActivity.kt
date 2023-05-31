package com.semicolon.newsapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.semicolon.newsapp.MainActivity
import com.semicolon.newsapp.R
import com.semicolon.newsapp.databinding.ActivityLoginBinding
import es.dmoral.toasty.Toasty

class LoginActivity : AppCompatActivity() {

    lateinit var binding:ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        mainButton()
    }

    private fun mainButton(){
        binding.btnLogin.setOnClickListener {
            //Toast.makeText(this,"tombol diklik",Toast.LENGTH_SHORT).show()
            if (validation()){
                checkUsername()
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

    private fun checkUsername(){
        if (binding.edtUsername.text.toString() == "admin" && binding.edtPassword.text.toString() == "1234"){
            val pergi = Intent(this,MainActivity::class.java)
            startActivity(pergi)
        }else{

            Toast.makeText(this,"username atau password tidak sesuai",Toast.LENGTH_SHORT).show()
        }
    }
}