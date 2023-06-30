package com.semicolon.newsapp.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.MenuItem.OnMenuItemClickListener
import android.view.View
import android.view.WindowManager
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.semicolon.newsapp.R
import com.semicolon.newsapp.databinding.ActivityDetailBinding
import com.semicolon.newsapp.helper.HelperClass
import com.semicolon.newsapp.helper.SharedPref
import com.semicolon.newsapp.model.ActionModel
import com.semicolon.newsapp.model.NewsItem
import com.semicolon.newsapp.network.ApiConfig
import com.squareup.picasso.Picasso
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DetailActivity:AppCompatActivity() {

    companion object {
        const val DATA = "data"
    }

    lateinit var binding:ActivityDetailBinding
    lateinit var id:String
    private var uri:NewsItem? = null
    lateinit var pref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        main()
    }

    private fun main(){
        supportActionBar?.hide()
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        pref = SharedPref(this)

        uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(DATA,NewsItem::class.java)
        } else {
            intent.getParcelableExtra(DATA)
        }

        id = uri?.id?:""
        binding.tvTitle.text = uri?.judul
        binding.tvDate.text = "${uri?.userinput} . ${HelperClass().convertDate(uri?.tanggal?:"")}"
        binding.tvDeskripsi.text = uri?.deskripsi
        Picasso.get().load(uri?.cover)
            .error(R.drawable.ic_launcher_background)
            .into(binding.imgCover)

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnMore.setOnClickListener {
            showMenu()
        }

        if (HelperClass().getDataLogin(pref).user?.role != "admin"){
            binding.btnMore.visibility = View.GONE
        }
    }

    private fun showMenu(){
        val popupMenu = PopupMenu(this, binding.btnMore)
        popupMenu.menuInflater.inflate(R.menu.menu, popupMenu.menu)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            popupMenu.gravity = Gravity.END
        }
        popupMenu.setOnMenuItemClickListener(object : OnMenuItemClickListener,
            PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(menu: MenuItem): Boolean {
                if (menu.title == "Ubah"){
                    gotoUpdate()
                }else{
                    delete()
                }
                return true
            }
        })
        popupMenu.show()
    }

    private fun gotoUpdate(){
        val i = Intent(this,AddActivity::class.java)
        i.putExtra(AddActivity.DATA,uri)
        startActivityForResult(i,100)
    }

    private fun delete(){
        val dialog = ProgressDialog.show(this,"Loading","",true)
        ApiConfig.getInstanceRetrofit().deleteNews(id).enqueue(object : Callback<ActionModel>{
            override fun onResponse(call: Call<ActionModel>, response: Response<ActionModel>) {
                dialog.dismiss()
                if (response.isSuccessful){
                    Toasty.info(this@DetailActivity,response.body()?.pesan?:"",Toasty.LENGTH_SHORT).show()
                    val returnIntent = Intent()
                    setResult(RESULT_OK, returnIntent)
                    finish()
                }
            }

            override fun onFailure(call: Call<ActionModel>, t: Throwable) {
                dialog.dismiss()
                Toasty.error(this@DetailActivity,t.message.toString(),Toasty.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                val returnIntent = Intent()
                setResult(RESULT_OK, returnIntent)
                finish()
            }
        }
    }
}