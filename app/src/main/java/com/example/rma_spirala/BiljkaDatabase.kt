package com.example.rma_spirala

import android.content.Context
import android.graphics.Bitmap
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.TypeConverters

@Dao
interface BiljkaDAO {
    @Insert
    suspend fun insertBiljka(biljka: Biljka): Long

    suspend fun saveBiljka(biljka: Biljka): Boolean {
        return insertBiljka(biljka)!= -1L
    }

    @Insert
    suspend fun insertBiljkaBitmap(biljkaBitmap: BiljkaBitmap): Long

    suspend fun addImage(idBiljke: Int, biljkaBitmap: Bitmap): Boolean {
        val biljkaBitmapEntity = BiljkaBitmap(bitmap = biljkaBitmap, idBiljke = idBiljke.toLong())
        return insertBiljkaBitmap(biljkaBitmapEntity) != -1L
    }

    //todo check false or 0
    @Query("SELECT * FROM biljka WHERE onlineChecked = false")
    suspend fun getOfflineBiljkas(): List<Biljka>

    @Transaction
    suspend fun fixOfflineBiljka(): Int {
        val offlineBiljkas = getOfflineBiljkas()
        var updatedCount = 0
        var dao = TrefleDAO()
        //dao.setContext()
        for (biljka in offlineBiljkas) {
            val oldBiljka = biljka
            dao.fixData(biljka) // fixData ce popraviti biljka objekat
            if (oldBiljka != biljka) {
                updatedCount++
            }
        }
        return updatedCount
    }
    @Query("SELECT * FROM biljka")
    suspend fun getAllBiljkas(): List<Biljka>

//todo check ovdje treba i delete from bitmap isto ig
    @Query("DELETE FROM biljka")
    suspend fun clearData()

}

@Database(entities = [Biljka::class, BiljkaBitmap::class], version = 1)
@TypeConverters(Converters::class)
abstract class BiljkaDatabase : RoomDatabase() {
    abstract fun biljkaDao(): BiljkaDAO
    companion object {
        private var INSTANCE: BiljkaDatabase? = null
        fun getInstance(context: Context): BiljkaDatabase {
            if (INSTANCE == null) {
                synchronized(BiljkaDatabase::class) {
                    INSTANCE = buildRoomDB(context)
                }
            }
            return INSTANCE!!
        }
        private fun buildRoomDB(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                BiljkaDatabase::class.java,
                "biljke-db"
            ).build()
    }
}