package com.example.rma_spirala

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "biljkaBitmap",
    foreignKeys = [ForeignKey(
        entity = Biljka::class,
        parentColumns = ["id"],
        childColumns = ["idBiljke"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class BiljkaBitmap (
    @ColumnInfo(name = "bitmap")
    val bitmap: Bitmap,

    @ColumnInfo(name = "idBiljke")
    var idBiljke: Long,

    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
)
