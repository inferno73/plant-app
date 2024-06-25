package com.example.rma_spirala

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {
    // Search plants by Latin name
    @GET("plants/search")
    suspend fun searchPlantsByLatinName(
        @Query("q") latinName: String,
        @Query("token") apiKey: String = BuildConfig.TREFLE_API_KEY
    ): Response<PlantsResponse>

    //https://trefle.io/api/v1/plants/search?q=Rosa&token=GMbFe9fwtDUJ3PvwZybjNmLbNxZ4f5mluqZBnW9Z3iE
    //vraca data NIZ biljaka koje u svom lat.nazivu sadrze trazeni q string
    @GET("plants/{id}")
    suspend fun searchPlantsByID(
        @Path("id") id: Int,
        @Query("token") apiKey: String = BuildConfig.TREFLE_API_KEY
    ): Response<PlantsDetailedResponse>

    //https://trefle.io/api/v1/plants/265032?token=GMbFe9fwtDUJ3PvwZybjNmLbNxZ4f5mluqZBnW9Z3iE
    //vraca data ali dosta opsirnije
    @GET("plants/search")
    suspend fun searchPlantsByFlowerColorAndSubstr(
        @Query("filter[flower_color]") flowerColor: String,
        @Query("q") latinName: String,
        @Query("token") apiKey: String = BuildConfig.TREFLE_API_KEY
    ): Response<PlantsResponse>

    //https://trefle.io/api/v1/plants?filter[flower_color]=red&q=rosa&token=GMbFe9fwtDUJ3PvwZybjNmLbNxZ4f5mluqZBnW9Z3iE
}
