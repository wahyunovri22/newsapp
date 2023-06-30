package com.semicolon.newsapp

import android.R.attr
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaeger.library.StatusBarUtil
import com.semicolon.newsapp.adapter.NewsAdapter
import com.semicolon.newsapp.databinding.ActivityMainBinding
import com.semicolon.newsapp.helper.HelperClass
import com.semicolon.newsapp.helper.SharedPref
import com.semicolon.newsapp.model.NewsItem
import com.semicolon.newsapp.model.ResponseNews
import com.semicolon.newsapp.network.ApiConfig
import com.semicolon.newsapp.ui.AddActivity
import com.semicolon.newsapp.ui.DetailActivity
import com.semicolon.newsapp.ui.LoginActivity
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var pref: SharedPref
    var newsAdapter = NewsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        main()
    }

    private fun main() {
        supportActionBar?.hide()
        HelperClass().hideBar(this)
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.white), 0)
        getDataNews()
        pref = SharedPref(this)

        if (HelperClass().getDataLogin(pref).user?.role != "admin") {
            binding.btnAdd.visibility = View.GONE
        }

        binding.btnAdd.setOnClickListener {
            gotoAdd()
        }

        binding.imgAccount.setOnClickListener {
            showDialog()
        }
        binding.edtSearch.addTextChangedListener {
            newsAdapter.filter.filter(it)
        }
    }

    private fun getDataNews() {
        val dialog = ProgressDialog.show(this, "Loading", "", true)
        dialog.show()
        ApiConfig.getInstanceRetrofit().news().enqueue(object : Callback<ResponseNews> {
            override fun onResponse(call: Call<ResponseNews>, response: Response<ResponseNews>) {
                dialog.dismiss()
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data?.error == false) {
                        showData(list = data.news)
                        return
                    }
                }
            }

            override fun onFailure(call: Call<ResponseNews>, t: Throwable) {
                dialog.dismiss()
                Toasty.error(this@MainActivity, t.message.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showData(list: ArrayList<NewsItem>?) {
        list?.let {
            newsAdapter.setData(this, it)
            with(binding.rvNews) {
                this.layoutManager = LinearLayoutManager(this@MainActivity)
                this.adapter = newsAdapter
            }
        }
    }

    fun goToDetail(newsItem: NewsItem) {
        val i = Intent(this, DetailActivity::class.java)
        i.putExtra(DetailActivity.DATA, newsItem)
        startActivityForResult(i, 100)
    }

    private fun gotoAdd() {
        val i = Intent(this, AddActivity::class.java)
        startActivityForResult(i, 100)
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pemberitahuan")
        builder.setMessage("Apakah anda yakin keluar dari aplikasi ?")

        builder.setPositiveButton(android.R.string.yes) { dialog, _ ->
            dialog.dismiss()
            exit()
        }

        builder.setNegativeButton(android.R.string.no) { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun exit() {
        pref.saveBoolean(pref.LOGIN, false)
        pref.clearAll()
        startActivity(
            Intent(this, LoginActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        finish()
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