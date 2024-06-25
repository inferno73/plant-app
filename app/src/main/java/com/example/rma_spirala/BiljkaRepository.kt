package com.example.rma_spirala

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object BiljkaRepository {
    fun fillBiljkaDatabase(context: Context) {
        //napuni DB sa BiljkaSTatic podacima = slike za te biljke
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        scope.launch {
            try {
                val biljkaDao = BiljkaDatabase.getInstance(context).biljkaDao()
                val biljke = getBiljkeUpdated()
                //biljkaDao.clearData()
                biljke.forEach { biljka ->
                    biljkaDao.saveBiljka(biljka) //dodaje biljku u bazu
                    saveBiljkaImageToDB(context,biljka)
                }
            } catch (e: Exception) {
                //Toast.makeText(context, "Doslo je do izuzetka u dodavanju podataka u bazu!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    suspend fun saveBiljkaToDB(context: Context,biljka:Biljka) : String? {
        return withContext(Dispatchers.IO) {
            try {
                var db = BiljkaDatabase.getInstance(context)
                db.biljkaDao().saveBiljka(biljka)
               /* if() {
                    Toast.makeText(context, "Biljka uspjesno dodana u bazu!", Toast.LENGTH_SHORT).show()
                } else Toast.makeText(context, "Biljka nije dodana u bazu!", Toast.LENGTH_SHORT).show()
                */return@withContext "success"
            } catch (error: Exception) {
                return@withContext null
            }
        }
    }
    suspend fun saveBiljkaImageToDB(context: Context,biljka:Biljka): String? {
        return withContext(Dispatchers.IO) {
            try {
                var db = BiljkaDatabase.getInstance(context)
                var trefleDao = TrefleDAO()
                trefleDao.setContext(context)
                biljka.id?.let { db.biljkaDao().addImage(it, trefleDao.getImage(biljka) ) }
                return@withContext "success"
            } catch (error: Exception) {
                return@withContext null
            }
        }
    }
    suspend fun getAllBiljkasFromDB(context: Context): List<Biljka> {
        return withContext(Dispatchers.IO) {
            var db = BiljkaDatabase.getInstance(context)
            var biljke = db.biljkaDao().getAllBiljkas()
            return@withContext biljke
        }
    }
    fun deleteBiljkaDatabase(context: Context) {
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        scope.launch {
            try {
                val biljkaDao = BiljkaDatabase.getInstance(context).biljkaDao()
                biljkaDao.clearData()
            } catch (e: Exception) {
                //Toast.makeText(context, "Doslo je do izuzetka u dodavanju podataka u bazu!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}