package com.example.rma_spirala

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiAdapter {
    val retrofit: Api = Retrofit.Builder()
        .baseUrl("http://trefle.io/api/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(Api::class.java)
}