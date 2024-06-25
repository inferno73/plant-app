package com.example.rma_spirala

import com.google.gson.annotations.SerializedName

data class Growth(
    @SerializedName("soil_texture") val soil_texture: Int,
    @SerializedName("light") val light: Int?,
    @SerializedName("atmospheric_humidity") val atmospheric_humidity: Int?,
)

data class MainSpecies(
    @SerializedName("image_url") val image_url: String?,
    @SerializedName("edible") val edible: Boolean,
    @SerializedName("growth") val growth: Growth?,
    @SerializedName("specifications") val specifications: Specifications
)

data class Specifications(
    @SerializedName("toxicity") val toxicity: String?
)

data class Family(
    @SerializedName("name") val name: String
)

data class Plant(
    @SerializedName("id") val id: Int,
    @SerializedName("common_name") val common_name: String,
    @SerializedName("scientific_name") val scientific_name: String,
    @SerializedName("image_url") val image_url: String?,
    @SerializedName("family") val family: String?
)

data class PlantDetails(
    @SerializedName("common_name") val common_name: String,
    @SerializedName("scientific_name") val scientific_name: String,
    @SerializedName("main_species") val main_species: MainSpecies?,
    @SerializedName("family") val family: Family?,
)

data class PlantsResponse(
    @SerializedName("data") val data: List<Plant>
)

data class PlantsDetailedResponse(
    @SerializedName("data") val data: PlantDetails
)

