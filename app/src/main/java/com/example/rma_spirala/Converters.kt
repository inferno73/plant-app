package com.example.rma_spirala

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class Converters {

    @TypeConverter
    fun fromBitmap(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    @TypeConverter
    fun toBitmap(encodedString: String): Bitmap {
        val decodedBytes = Base64.decode(encodedString, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    @TypeConverter
    fun fromMedicinskaKorist(medicinskaKorist: MedicinskaKorist?): String? {
        return medicinskaKorist?.name
    }

    @TypeConverter
    fun toMedicinskaKorist(medicinskaKorist: String?): MedicinskaKorist? {
        return medicinskaKorist?.let { MedicinskaKorist.valueOf(it) }
    }

    @TypeConverter
    fun fromProfilOkusaBiljke(profilOkusa: ProfilOkusaBiljke?): String? {
        return profilOkusa?.name
    }

    @TypeConverter
    fun toProfilOkusaBiljke(profilOkusa: String?): ProfilOkusaBiljke? {
        return profilOkusa?.let { ProfilOkusaBiljke.valueOf(it) }
    }

    @TypeConverter
    fun fromZemljiste(zemljiste: Zemljiste?): String? {
        return zemljiste?.name
    }

    @TypeConverter
    fun toZemljiste(zemljiste: String?): Zemljiste? {
        return zemljiste?.let { Zemljiste.valueOf(it) }
    }

    @TypeConverter
    fun fromKlimatskiTip(klimatskiTip: KlimatskiTip?): String? {
        return klimatskiTip?.name
    }

    @TypeConverter
    fun toKlimatskiTip(klimatskiTip: String?): KlimatskiTip? {
        return klimatskiTip?.let { KlimatskiTip.valueOf(it) }
    }

    @TypeConverter
    fun fromMedicinskaKoristList(list: List<MedicinskaKorist>?): String? {
        return list?.joinToString(separator = ",") { it.name }
    }

    @TypeConverter
    fun toMedicinskaKoristList(data: String?): List<MedicinskaKorist>? {
        return data?.split(",")?.map { MedicinskaKorist.valueOf(it) }
    }

    @TypeConverter
    fun fromKlimatskiTipList(list: List<KlimatskiTip>?): String? {
        return list?.joinToString(separator = ",") { it.name }
    }

    @TypeConverter
    fun toKlimatskiTipList(data: String?): List<KlimatskiTip>? {
        return data?.split(",")?.map { KlimatskiTip.valueOf(it) }
    }

    @TypeConverter
    fun fromZemljisteList(list: List<Zemljiste>?): String? {
        return list?.joinToString(separator = ",") { it.name }
    }

    @TypeConverter
    fun toZemljisteList(data: String?): List<Zemljiste>? {
        return data?.split(",")?.map { Zemljiste.valueOf(it) }
    }

    @TypeConverter
    fun fromJelaList(list: List<String>?): String? {
        return list?.joinToString(separator = ",")
    }

    @TypeConverter
    fun toJelaList(data: String?): List<String>? {
        return data?.split(",")
    }
}
