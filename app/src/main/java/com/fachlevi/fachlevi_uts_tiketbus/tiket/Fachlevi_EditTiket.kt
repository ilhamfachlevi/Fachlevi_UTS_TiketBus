package com.fachlevi.fachlevi_uts_tiketbus.tiket

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.fachlevi.fachlevi_uts_tiketbus.Fachlevi_MainActivity
import com.fachlevi.fachlevi_uts_tiketbus.data.Fachlevi_DB
import com.fachlevi.fachlevi_uts_tiketbus.data.tiket.Fachlevi_Tiket
import com.fachlevi.fachlevi_uts_tiketbus.databinding.FragmentEditTiketBinding
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class Fachlevi_EditTiket: AppCompatActivity() {

    private var _binding: FragmentEditTiketBinding? = null
    private val binding get() = _binding!!

    private val REQ_CAM = 101
    private var data_gambar: Bitmap? = null
    private var old_foto_dir = ""
    private var new_foto_dir = ""

    private var id_tiket: Int = 0

    lateinit var tiketDB: Fachlevi_DB
    private val STORAGE_PERMISSION_CODE = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = FragmentEditTiketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tiketDB = Fachlevi_DB(this@Fachlevi_EditTiket)

        val intent = intent

        binding.TxtEditNama.setText(intent.getStringExtra("nama").toString())
        binding.TxtEditNIK.setText(intent.getStringExtra("nik").toString())
        binding.TxtEditKelas.setText(intent.getStringExtra("kelas").toString())
        binding.TxtEditNoKursi.setText(intent.getStringExtra("nomor_kursi").toString())
        binding.TxtEditServiceTambahan.setText(intent.getStringExtra("service_tambahan").toString())

        id_tiket = intent.getStringExtra("id").toString().toInt()

        old_foto_dir = intent.getStringExtra("foto_pembeli").toString()

        Log.e("tess","tes2")

        val imgFile = File("${Environment.getExternalStorageDirectory()}/${old_foto_dir}")
        val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)

        binding.BtnImgPembeli.setImageBitmap(myBitmap)

        if (!checkPermission()) {
            requestPermission()
        }

        binding.BtnImgPembeli.setOnClickListener {
            openCamera()
        }

        binding.BtnEditTiket.setOnClickListener{
            editTiket()
        }


    }



    private fun checkPermission() : Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                val uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
            } catch (e:Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
            }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE)
        }
    }

    fun saveMediaToStorage(bitmap: Bitmap) : String {
        val filename = "${System.currentTimeMillis()}.jpg"

        var fos: OutputStream? = null
        var image_save = ""

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                fos = imageUri?.let {resolver.openOutputStream(it)}
                image_save = "${Environment.DIRECTORY_PICTURES}/${filename}"
            }
        }
        else {
            val permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

            if (permission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE),
                    STORAGE_PERMISSION_CODE)
            }

            val imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imageDir, filename)
            fos = FileOutputStream(image)

            image_save = "${Environment.DIRECTORY_PICTURES}/${filename}"

        }
        fos?.use {bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)}
        return image_save
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CAM && resultCode == RESULT_OK){
            data_gambar = data?.extras?.get("data") as Bitmap
            val image_save_uri: String = saveMediaToStorage(data_gambar!!)
            new_foto_dir = image_save_uri
            binding.BtnImgPembeli.setImageBitmap(data_gambar)
        }
    }

    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            this.packageManager?.let {
                intent?.resolveActivity(it).also {
                    startActivityForResult(intent, REQ_CAM)
                }
            }

        }
    }

    private fun editTiket(){
        val nama_pembeli = binding.TxtEditNama.text.toString()
        val nik = binding.TxtEditNIK.text.toString()
        val kelas = binding.TxtEditKelas.text.toString()
        val nomor_kursi = binding.TxtEditNoKursi.text.toString()
        val service_tambahan = binding.TxtEditServiceTambahan.text.toString()
        var foto_final_dir : String = old_foto_dir


        if (new_foto_dir != "") {
            foto_final_dir = new_foto_dir

            val imageDir = Environment.getExternalStoragePublicDirectory("")
            val old_foto_delete = File(imageDir, old_foto_dir)

            if (old_foto_delete.exists()) {

                if (old_foto_delete.delete()) {
                    Log.e("Foto Deleted", foto_final_dir)
                }
            }
        }

        lifecycleScope.launch{
            val tiket = Fachlevi_Tiket(nama_pembeli, nik, kelas, nomor_kursi, service_tambahan, foto_final_dir)
            tiket.id = id_tiket
            tiketDB.getFachlevi_TiketDao().updateTiket(tiket)
        }

        val intentTiket = Intent(this, Fachlevi_MainActivity::class.java)
        startActivity(intentTiket)
    }
}