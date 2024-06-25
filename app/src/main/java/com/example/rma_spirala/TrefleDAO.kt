package com.example.rma_spirala

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//changed to var in Biljka klasa
class TrefleDAO() {
    private lateinit var context: Context
    private val apiKey = "GMbFe9fwtDUJ3PvwZybjNmLbNxZ4f5mluqZBnW9Z3iE"

    fun setContext(context: Context) {
        this.context = context
    }
    suspend fun getImage(biljka: Biljka): Bitmap {
        //a ako nema latinski then returns the whole string -- nece naci -- default image postavlja
        val latinski = biljka.naziv.substringAfter('(').substringBefore(')')

        return try {
            val response = ApiAdapter.retrofit.searchPlantsByLatinName(latinski, apiKey)
            val imageUrl = response.body()?.data?.get(0)?.image_url ?: ""
            if (imageUrl.isEmpty()) {
                Toast.makeText(
                    context,
                    "Nije pronadjena odgovarajuca slika za biljku!",
                    Toast.LENGTH_LONG
                ).show()
                BitmapFactory.decodeResource(context.resources, R.drawable.default_plant_image)
            } else {
                withContext(Dispatchers.IO) {
                    val futureTarget: FutureTarget<Bitmap> = Glide.with(context)
                        .asBitmap()
                        .load(imageUrl)
                        .submit()

                    futureTarget.get()
                }
            }
        } catch (e: Exception) {
            //default bitmap ako error
            Toast.makeText(
                context,
                "Nije pronadjena odgovarajuca slika za biljku!",
                Toast.LENGTH_LONG
            ).show()
            BitmapFactory.decodeResource(context.resources, R.drawable.default_plant_image)
        }
    }

    suspend fun fixData(biljka: Biljka): Biljka {
        val latinski = biljka.naziv.substringAfter('(').substringBefore(')')
        val response = ApiAdapter.retrofit.searchPlantsByLatinName(latinski, apiKey)
        if (!response.isSuccessful || response.body()?.data.isNullOrEmpty()) {
            Toast.makeText(
                context,
                "Nisu pronadjeni odgovarajuci podaci za biljku!",
                Toast.LENGTH_LONG
            ).show()
            return biljka
        }
        val plantBezSvihPodataka = response.body()?.data?.get(0)
        val responseFull =
            plantBezSvihPodataka?.let { ApiAdapter.retrofit.searchPlantsByID(it.id, apiKey) }
        val plant = responseFull?.body()?.data

        postavljanjePodatakaBiljkeSaAPIja(biljka, plant)
        biljka.onlineChecked = true
        return biljka
    }

    //pomocna funkcija - poredi unesenu biljku sa fetchovanom i kreira finalnu
    private fun postavljanjePodatakaBiljkeSaAPIja(biljka: Biljka, plant: PlantDetails?) {
        if (plant != null && !(plant.family?.name.isNullOrEmpty() || plant.family?.name.equals(
                biljka.porodica,
                true
            ))
        ) {
            biljka.porodica = plant.family?.name.toString()
        }
        if (plant != null && !plant.main_species?.edible!!) {
            biljka.jela = emptyList()
            if (!(biljka.medicinskoUpozorenje.contains("NIJE JESTIVO", true)))
                biljka.medicinskoUpozorenje += " NIJE JESTIVO"
        }
        //ako toxicity null ne dodajemo nista
        if (plant != null && plant.main_species?.specifications?.toxicity != null && !plant.main_species?.specifications?.toxicity.equals(
                "none",
                true
            )
        ) {
            if (!(biljka.medicinskoUpozorenje.contains("TOKSIČNO", true)))
                biljka.medicinskoUpozorenje += " TOKSIČNO"
        }

        val soilTexture = plant?.main_species?.growth?.soil_texture
        //if(soilTexture!=null) biljka.zemljisniTipovi = emptyList()
        if (soilTexture == 9)
            biljka.zemljisniTipovi = listOf(Zemljiste.SLJUNKOVITO)
        else if (soilTexture == 10)
            biljka.zemljisniTipovi = listOf(Zemljiste.KRECNJACKO)
        else if (soilTexture == 7 || soilTexture == 8)
            biljka.zemljisniTipovi = listOf(Zemljiste.CRNICA)
        else if (soilTexture == 5 || soilTexture == 6)
            biljka.zemljisniTipovi = listOf(Zemljiste.ILOVACA)
        else if (soilTexture == 3 || soilTexture == 4)
            biljka.zemljisniTipovi = listOf(Zemljiste.PJESKOVITO)
        else if (soilTexture == 1 || soilTexture == 2)
            biljka.zemljisniTipovi = listOf(Zemljiste.GLINENO)
        //u protivnom ostaje prvobitno zadani tip zemljista

        val klimTipovi: MutableList<KlimatskiTip> = mutableListOf()
        val svjetlost = plant?.main_species?.growth?.light
        val vlaznost = plant?.main_species?.growth?.atmospheric_humidity
        //if(svjetlost!=null && vlaznost!=null) biljka.klimatskiTipovi = emptyList()
        if (svjetlost in 6..9 && vlaznost in 1..5) klimTipovi.add(KlimatskiTip.SREDOZEMNA)
        if (svjetlost in 8..10 && vlaznost in 7..10) klimTipovi.add(KlimatskiTip.TROPSKA)
        if (svjetlost in 6..9 && vlaznost in 5..8) klimTipovi.add(KlimatskiTip.SUBTROPSKA)
        if (svjetlost in 4..7 && vlaznost in 3..7) klimTipovi.add(KlimatskiTip.UMJERENA)
        if (svjetlost in 7..9 && vlaznost in 1..2) klimTipovi.add(KlimatskiTip.SUHA)
        if (svjetlost in 0..5 && vlaznost in 3..7) klimTipovi.add(KlimatskiTip.PLANINSKA)
        //u protivnom ostaju prvobitno selektovani tipovi

        if (!klimTipovi.isNullOrEmpty())
            biljka.klimatskiTipovi = klimTipovi
    }

    suspend fun getPlantsWithFlowerColor(flower_color: String, substr: String): List<Biljka> {
        var listaBiljaka = mutableListOf<Biljka>()
        val response =
            ApiAdapter.retrofit.searchPlantsByFlowerColorAndSubstr(flower_color, substr, apiKey)
        //ove biljke nisu detailed enough - treba fetch detailed za svaku
        if (!response.isSuccessful || response.body()?.data.isNullOrEmpty()) {
            Toast.makeText(
                context,
                "Nisu pronadjeni odgovarajuci podaci za biljku!",
                Toast.LENGTH_LONG
            ).show()
        }

        response.body()?.data?.forEach { plantBezSvihPodataka ->
            val responseFull = ApiAdapter.retrofit.searchPlantsByID(plantBezSvihPodataka.id, apiKey)
            if (responseFull.isSuccessful) {
                val plant = responseFull.body()?.data
                //ako plant has null u main name ili latinskom skip it
                if (plant != null) {
                    if(plant.common_name!=null && plant.scientific_name!=null) {
                        val nameFormatirano = plant!!.common_name + " (" + plant.scientific_name + ")"
                        val biljka = Biljka(nameFormatirano, "", "", null, null, null, null, null)
                        postavljanjePodatakaBiljkeSaAPIja(biljka, plant)
                        listaBiljaka.add(biljka)
                    }
                }
            }
        }

        return listaBiljaka
    }

}

