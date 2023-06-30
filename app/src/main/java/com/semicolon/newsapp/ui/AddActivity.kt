package com.semicolon.newsapp.ui

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.jaeger.library.StatusBarUtil
import com.permissionx.guolindev.PermissionX
import com.semicolon.newsapp.R
import com.semicolon.newsapp.databinding.ActivityAddBinding
import com.semicolon.newsapp.helper.HelperClass
import com.semicolon.newsapp.model.ActionModel
import com.semicolon.newsapp.model.NewsItem
import com.semicolon.newsapp.network.ApiConfig
import com.semicolon.newsapp.network.ApiServices
import com.squareup.picasso.Picasso
import es.dmoral.toasty.Toasty
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class AddActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddBinding
    var namaFile = ""
    lateinit var fileCover: File
    companion object {
        const val DATA = "data"
    }
    private var uri: NewsItem? = null
    var id = ""
    lateinit var apiServices: Call<ActionModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        main()
    }

    private fun main() {
        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(ContextCompat.getColor(this, R.color.white))
        )
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.white), 0)
        HelperClass().hideBar(this)
        permission()

        uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(DetailActivity.DATA,NewsItem::class.java)
        } else {
            intent.getParcelableExtra(DetailActivity.DATA)
        }

        if (uri != null) setData()

        mainButton()
    }

    private fun mainButton(){
        binding.edtTime.setText(HelperClass().getTimeNow())
        binding.btnAdd.setOnClickListener {
            if (validation()) {
                if (uri != null) updateNews() else addNews()
            }
        }
        binding.imgCover.setOnClickListener {
            deletePhoto()
            EasyImage.openChooserWithGallery(this, "", 2)
        }
        binding.imgDelete.setOnClickListener {
            deletePhoto()
        }
    }

    private fun setData(){
        binding.btnAdd.text = "Edit Berita"
        id = uri?.id?:""
        namaFile = uri?.cover?:""
        fileCover = File("")
        binding.edtJudul.setText(uri?.judul?:"")
        binding.edtDeskripsi.setText(uri?.deskripsi?:"")

        Picasso.get().load(uri?.cover)
            .error(R.drawable.ic_launcher_background)
            .into(binding.imgCover)
    }

    private fun validation(): Boolean {
        if (binding.edtJudul.text.toString().isEmpty()) {
            Toasty.error(this@AddActivity, "Judul tidak boleh kosong", Toasty.LENGTH_SHORT, true)
                .show()
            return false
        }
        if (binding.edtDeskripsi.text.toString().isEmpty()) {
            Toasty.error(
                this@AddActivity,
                "Deskripsi tidak boleh kosong",
                Toasty.LENGTH_SHORT,
                true
            ).show()
            return false
        }
        if (namaFile == "") {
            Toasty.error(this@AddActivity, "Cover tidak boleh kosong", Toasty.LENGTH_SHORT, true)
                .show()
            return false
        }
        return true
    }

    private fun addNews() {
        val judul = RequestBody.create("text/plain".toMediaTypeOrNull(), binding.edtJudul.text.toString())
        val deskripsi = RequestBody.create("text/plain".toMediaTypeOrNull(), binding.edtDeskripsi.text.toString())
        val user = RequestBody.create("text/plain".toMediaTypeOrNull(), "Admin")
        val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), fileCover)
        val body = MultipartBody.Part.createFormData("uploaded_file", fileCover.name, requestFile)

        val dialog = ProgressDialog.show(this, "Loading ...", "", true)
        ApiConfig.getInstanceRetrofit().addNews(judul, deskripsi, user, body)
            .enqueue(object : Callback<ActionModel> {
                override fun onResponse(call: Call<ActionModel>, response: Response<ActionModel>) {
                    dialog.dismiss()
                    if (response.isSuccessful) {
                        if (response.body()?.kode == 1){
                            deletePhoto()
                            Toasty.info(
                                this@AddActivity,
                                response.body()?.pesan ?: "",
                                Toasty.LENGTH_SHORT
                            ).show()
                            val returnIntent = Intent()
                            setResult(RESULT_OK, returnIntent)
                            finish()
                        }else{
                            Toasty.error(
                                this@AddActivity,
                                response.body()?.pesan ?: "",
                                Toasty.LENGTH_SHORT
                            ).show()
                            val returnIntent = Intent()
                            setResult(RESULT_OK, returnIntent)
                            finish()
                        }
                    }
                }

                override fun onFailure(call: Call<ActionModel>, t: Throwable) {
                    dialog.dismiss()
                    Toasty.error(this@AddActivity, t.message.toString(), Toasty.LENGTH_SHORT, true)
                        .show()
                }
            })
    }

    private fun updateNews() {
        val judul = RequestBody.create("text/plain".toMediaTypeOrNull(), binding.edtJudul.text.toString())
        val deskripsi = RequestBody.create("text/plain".toMediaTypeOrNull(), binding.edtDeskripsi.text.toString())
        val id = RequestBody.create("text/plain".toMediaTypeOrNull(), id)
        val cover = RequestBody.create("text/plain".toMediaTypeOrNull(), namaFile)

        apiServices = if (File(fileCover.path).exists()){
            val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), fileCover)
            val body = MultipartBody.Part.createFormData("uploaded_file", fileCover.name, requestFile)
            ApiConfig.getInstanceRetrofit().updateNews(id,judul, deskripsi, cover, body)
        }else{
            ApiConfig.getInstanceRetrofit().updateNews(id,judul, deskripsi, cover, null)
        }

        val dialog = ProgressDialog.show(this, "Loading ...", "", true)
        apiServices.enqueue(object : Callback<ActionModel> {
                override fun onResponse(call: Call<ActionModel>, response: Response<ActionModel>) {
                    dialog.dismiss()
                    if (response.isSuccessful) {
                        if (response.body()?.kode == 1){
                            deletePhoto()
                            Toasty.info(
                                this@AddActivity,
                                response.body()?.pesan ?: "",
                                Toasty.LENGTH_SHORT
                            ).show()
                            val returnIntent = Intent()
                            setResult(RESULT_OK, returnIntent)
                            finish()
                        }else{
                            Toasty.error(
                                this@AddActivity,
                                response.body()?.pesan ?: "",
                                Toasty.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ActionModel>, t: Throwable) {
                    dialog.dismiss()
                    Toasty.error(this@AddActivity, t.message.toString(), Toasty.LENGTH_SHORT, true)
                        .show()
                }
            })
    }

    private fun deletePhoto() {
        if (namaFile != "") {
            val file = File(fileCover.path)
            if (file.exists()) {
                file.canonicalFile.delete()
                if (file.exists()) {
                    applicationContext.deleteFile(file.name)
                }
            }
            namaFile = ""
            binding.imgCover.setImageResource(R.drawable.ic_launcher_background)
        }
    }

    private fun permission() {
        PermissionX.init(this)
            .permissions(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .explainReasonBeforeRequest()
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(
                    deniedList,
                    "Perizinan ini digunakan untuk menambahkan file",
                    "OK",
                    "Batal"
                )
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "Anda perlu masuk ke menu pengaturan untuk mengaktifkan manual perizinan ini",
                    "OK",
                    "Batal"
                )
            }
            .request { allGranted, _, _ ->
                if (allGranted) {
                    Log.d("izin", "disetujui")
                } else {
                    Toast.makeText(this, "Perizinan ditoloak", Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onBackPressed() {
        deletePhoto()
        super.getOnBackPressedDispatcher().onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        EasyImage.handleActivityResult(
            requestCode,
            resultCode,
            data,
            this,
            object : DefaultCallback() {
                override fun onImagePickerError(
                    e: Exception?,
                    source: EasyImage.ImageSource?,
                    type: Int
                ) {
                    //Some error handling
                    Toast.makeText(applicationContext, "error " + e!!.message, Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onImagesPicked(
                    imageFiles: MutableList<File>,
                    source: EasyImage.ImageSource?,
                    type: Int
                ) {
                    namaFile = File(imageFiles[0].path).name
                    fileCover = File(imageFiles[0].path)
                    Picasso.get().load(fileCover)
                        .error(R.drawable.ic_launcher_foreground)
                        .into(binding.imgCover)
                }

                override fun onCanceled(source: EasyImage.ImageSource?, type: Int) {
                    super.onCanceled(source, type)
                    if (source == EasyImage.ImageSource.CAMERA_IMAGE) {
                        val path = EasyImage.lastlyTakenButCanceledPhoto(this@AddActivity)
                        path?.delete()
                    }
                }
            })
    }


}