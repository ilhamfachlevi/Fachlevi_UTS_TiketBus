package com.fachlevi.fachlevi_uts_tiketbus.data.tiket

import androidx.room.*

@Dao

interface Fachlevi_TiketDao {
    @Query("SELECT * FROM Fachlevi_Tiket WHERE nama_pembeli LIKE :namaPembeli")
    suspend fun searchTiket(namaPembeli: String) : List<Fachlevi_Tiket>

    @Insert
    suspend fun addTiket(tiket: Fachlevi_Tiket)

    @Update(entity = Fachlevi_Tiket::class)
    suspend fun updateTiket(tiket: Fachlevi_Tiket)

    @Delete
    suspend fun deleteTiket(tiket: Fachlevi_Tiket)

    @Query("SELECT * FROM Fachlevi_Tiket ORDER BY id DESC")
    suspend fun getAllTiket(): List<Fachlevi_Tiket>
}