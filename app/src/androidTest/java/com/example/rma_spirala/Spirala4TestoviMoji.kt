package com.example.rma_spirala

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Spirala4TestoviMoji {

    private lateinit var db: BiljkaDatabase
    private lateinit var dao: BiljkaDAO

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, BiljkaDatabase::class.java)
            .allowMainThreadQueries() // Not recommended for production apps
            .build()
        dao = db.biljkaDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertBiljkaAndRetrieve() = runBlocking {
        val biljka = Biljka(
            naziv = "TestBiljka",
            porodica = "TestFamily",
            medicinskoUpozorenje = "None",
            medicinskeKoristi = null,
            profilOkusa = null,
            jela = null,
            klimatskiTipovi = listOf(KlimatskiTip.UMJERENA, KlimatskiTip.TROPSKA),
            zemljisniTipovi = null,
            onlineChecked = false
        )

        dao.insertBiljka(biljka)
        val allBilikas = dao.getAllBiljkas()
        assertThat(allBilikas.size, equalTo(1))
        assertThat(allBilikas[0].naziv, equalTo("TestBiljka"))
        assertThat(allBilikas[0].porodica, equalTo("TestFamily"))
    }

    @Test
    fun clearDataAndCheckEmpty() = runBlocking {
        val biljka = Biljka(
            naziv = "TestBiljka",
            porodica = "TestFamily",
            medicinskoUpozorenje = "None",
            medicinskeKoristi = null,
            profilOkusa = null,
            jela = null,
            klimatskiTipovi = null,
            zemljisniTipovi = null,
            onlineChecked = false
        )

        dao.insertBiljka(biljka)
        dao.clearData()
        val allBilikas = dao.getAllBiljkas()

        assertThat(allBilikas.size, equalTo(0))
    }
}
