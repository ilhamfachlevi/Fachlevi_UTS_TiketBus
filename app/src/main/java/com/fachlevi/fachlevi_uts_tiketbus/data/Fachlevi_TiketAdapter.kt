package com.fachlevi.fachlevi_uts_tiketbus.data

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.fachlevi.fachlevi_uts_tiketbus.R
import com.fachlevi.fachlevi_uts_tiketbus.data.tiket.Fachlevi_Tiket
import com.fachlevi.fachlevi_uts_tiketbus.tiket.Fachlevi_EditTiket
import java.io.File

class Fachlevi_TiketAdapter (private val tiketList: ArrayList<Fachlevi_Tiket>) :
    RecyclerView.Adapter<Fachlevi_TiketAdapter.TiketViewHolder>()

{
    private lateinit var activity : AppCompatActivity
    class TiketViewHolder (tiketItemView: View) : RecyclerView.ViewHolder(tiketItemView) {
        val nama_pembeli : TextView = tiketItemView.findViewById(R.id.TVLNamaPembeli)
        val nik_pembeli : TextView = tiketItemView.findViewById(R.id.TVLNikPembeli)
        val kelas : TextView = tiketItemView.findViewById(R.id.TVLKelas)
        val nomor_kursi : TextView = tiketItemView.findViewById(R.id.TVLNoKursi)
        val service_tambahan : TextView = tiketItemView.findViewById(R.id.TVLServiceTambahan)

        val foto_pembeli : ImageView = itemView.findViewById(R.id.IMLGambarPembeli)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TiketViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.tiket_list_layout, parent, false)
        return TiketViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return tiketList.size
    }

    override fun onBindViewHolder(holder: TiketViewHolder, position: Int) {
        val currentItem = tiketList[position]
        val foto_dir = currentItem.foto_pembeli.toString()
        val imgFile = File("${Environment.getExternalStorageDirectory()}/${foto_dir}")
        Log.e("tes","${Environment.getExternalStorageDirectory()}/${foto_dir}")
        val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)

        holder.foto_pembeli.setImageBitmap(myBitmap)
        holder.nama_pembeli.text = currentItem.nama_pembeli.toString()
        holder.nik_pembeli.text = currentItem.nik_pembeli.toString()
        holder.kelas.text = currentItem.kelas.toString()
        holder.nomor_kursi.text = currentItem.no_kursi.toString()
        holder.service_tambahan.text = currentItem.service_tambahan.toString()

        holder.itemView.setOnClickListener{
            activity = it.context as AppCompatActivity
            activity.startActivity(Intent(activity, Fachlevi_EditTiket::class.java).apply{
                putExtra("nama", currentItem.nama_pembeli.toString())
                putExtra("nik", currentItem.nik_pembeli.toString())
                putExtra("kelas", currentItem.kelas.toString())
                putExtra("nomor_kursi", currentItem.no_kursi.toString())
                putExtra("service_tambahan", currentItem.service_tambahan.toString())
                putExtra("foto_pembeli", currentItem.foto_pembeli.toString())
                putExtra("id", currentItem.id.toString())
            })
        }
    }
}