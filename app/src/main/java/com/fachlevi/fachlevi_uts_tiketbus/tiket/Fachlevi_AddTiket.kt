package com.fachlevi.fachlevi_uts_tiketbus.tiket

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.fachlevi.fachlevi_uts_tiketbus.Fachlevi_MainActivity
import com.fachlevi.fachlevi_uts_tiketbus.data.Fachlevi_DB
import com.fachlevi.fachlevi_uts_tiketbus.data.tiket.Fachlevi_Tiket
import com.fachlevi.fachlevi_uts_tiketbus.databinding.FragmentAddTiketBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class Fachlevi_AddTiket : BottomSheetDialogFragment() {

    private var _binding: FragmentAddTiketBinding ? = null
    private val binding get() = _binding!!

    private val REQ_CAM = 100
    private var data_gambar: Bitmap? = null
    private var saved_image_url: String = ""

    private val STORAGE_PERMISSION_CODE = 102
    private val TAG = "PERMISSION TAG"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddTiketBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun addTiket() {
        val nama_pembeli = binding.TxtNama.text.toString()
        val nik = binding.TxtNIK.text.toString()
        val kelas = binding.TxtKelas.text.toString()
        val no_kursi = binding.TxtNoKursi.text.toString()
        val service_tambahan = binding.TxtServiceTambahan.text.toString()

        lifecycleScope.launch {
            val makanan = Fachlevi_Tiket(nama_pembeli, nik, kelas,no_kursi,service_tambahan, saved_image_url)
            Fachlevi_DB(requireContext()).getFachlevi_TiketDao().addTiket(makanan)
        }

        dismiss()
    }

    fun saveMediaToStorage(bitmap: Bitmap) : String {
        val filename = "${System.currentTimeMillis()}.jpg"

        var fos: OutputStream? = null
        var image_save = ""

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            activity?.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let {resolver.openOutputStream(it)}
                image_save = "${Environment.DIRECTORY_PICTURES}/${filename}"
            }
        } else {
            val permission = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)

            if (permission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this.requireActivity(), arrayOf(
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
        if (requestCode == REQ_CAM && resultCode == AppCompatActivity.RESULT_OK){
            data_gambar = data?.extras?.get("data") as Bitmap
            val image_save_uri: String = saveMediaToStorage(data_gambar!!)
            binding.BtnImgPembeli.setImageBitmap(data_gambar)
            saved_image_url = image_save_uri
        }
    }

    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            this.activity?.packageManager?.let {
                intent?.resolveActivity(it).also {
                    startActivityForResult(intent, REQ_CAM)
                }
            }

        }
    }

    override fun onDetach(){
        super.onDetach()
        (activity as Fachlevi_MainActivity?)?.loadDataTiket()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.BtnImgPembeli.setOnClickListener {
            openCamera()
        }

        binding.BtnAddTiket.setOnClickListener{
            if(saved_image_url != "") {
                addTiket()
            }
        }
    }
}