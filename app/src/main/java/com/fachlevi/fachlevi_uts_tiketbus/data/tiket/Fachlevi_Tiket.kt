package com.fachlevi.fachlevi_uts_tiketbus.data.tiket

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Fachlevi_Tiket")

data class Fachlevi_Tiket(
    @ColumnInfo(name = "nama_pembeli") var nama_pembeli: String = "",
    @ColumnInfo(name = "nik_pembeli") var nik_pembeli: String = "",
    @ColumnInfo(name = "kelas") var kelas: String = "",
    @ColumnInfo(name = "nomor_kursi") var no_kursi: String = "",
    @ColumnInfo(name = "service_tambahan") var service_tambahan: String = "",
    @ColumnInfo(name = "foto_pembeli") var foto_pembeli: String = "",

    ) : Serializable {
    @PrimaryKey(autoGenerate = true ) var id: Int = 0
}


