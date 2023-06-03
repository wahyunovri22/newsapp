package com.semicolon.newsapp

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaeger.library.StatusBarUtil
import com.semicolon.newsapp.adapter.NewsAdapter
import com.semicolon.newsapp.databinding.ActivityMainBinding
import com.semicolon.newsapp.helper.HelperClass
import com.semicolon.newsapp.model.NewsItem
import com.semicolon.newsapp.model.ResponseNews
import com.semicolon.newsapp.network.ApiConfig
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        main()
    }

    private fun main(){
        supportActionBar?.hide()
        HelperClass().hideBar(this)
        StatusBarUtil.setColor(this,ContextCompat.getColor(this,R.color.white),0)
        getDataNews()
    }

    private fun getDataNews(){
        val dialog = ProgressDialog.show(this,"Loading","",true)
        dialog.show()
        ApiConfig.getInstanceRetrofit().news().enqueue(object : Callback<ResponseNews>{
            override fun onResponse(call: Call<ResponseNews>, response: Response<ResponseNews>) {
                dialog.dismiss()
                if (response.isSuccessful){
                    val data = response.body()
                    if (data?.error == false){
                        showData(list = data.news)
                        return
                    }
                }
            }

            override fun onFailure(call: Call<ResponseNews>, t: Throwable) {
                dialog.dismiss()
                Toasty.error(this@MainActivity,t.message.toString(),Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showData(list:ArrayList<NewsItem>?){
        with(binding.rvNews){
            this.layoutManager = LinearLayoutManager(this@MainActivity)
            this.adapter = list?.let { NewsAdapter(this@MainActivity, it) }
        }
    }
}