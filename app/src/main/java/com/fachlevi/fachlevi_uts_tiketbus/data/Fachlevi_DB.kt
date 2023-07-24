package com.fachlevi.fachlevi_uts_tiketbus.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fachlevi.fachlevi_uts_tiketbus.data.tiket.Fachlevi_Tiket
import com.fachlevi.fachlevi_uts_tiketbus.data.tiket.Fachlevi_TiketDao

@Database(entities = [Fachlevi_Tiket::class], version = 1)
abstract class Fachlevi_DB : RoomDatabase() {
    abstract fun getFachlevi_TiketDao(): Fachlevi_TiketDao

    companion object{
        @Volatile
        private var instance: Fachlevi_DB? = null
        private var LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            Fachlevi_DB::class.java,
            "tiketbus-db"
        ).build()
    }
}