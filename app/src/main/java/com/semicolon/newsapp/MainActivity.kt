package com.semicolon.newsapp

import android.R.attr
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaeger.library.StatusBarUtil
import com.semicolon.newsapp.adapter.NewsAdapter
import com.semicolon.newsapp.databinding.ActivityMainBinding
import com.semicolon.newsapp.helper.HelperClass
import com.semicolon.newsapp.model.NewsItem
import com.semicolon.newsapp.model.ResponseNews
import com.semicolon.newsapp.network.ApiConfig
import com.semicolon.newsapp.ui.AddActivity
import com.semicolon.newsapp.ui.DetailActivity
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

        binding.btnAdd.setOnClickListener {
            gotoAdd()
        }
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

    fun goToDetail(newsItem: NewsItem){
        val i = Intent(this,DetailActivity::class.java)
        i.putExtra(DetailActivity.DATA,newsItem)
        startActivityForResult(i,100)
    }

   private fun gotoAdd(){
        val i = Intent(this,AddActivity::class.java)
        startActivityForResult(i,100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                getDataNews()
            }
        }
    }
}