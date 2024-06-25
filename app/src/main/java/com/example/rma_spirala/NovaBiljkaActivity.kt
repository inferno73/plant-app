package com.example.rma_spirala

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class NovaBiljkaActivity : AppCompatActivity() {
    private lateinit var medKoristLV: ListView
    private lateinit var klimTipLV: ListView
    private lateinit var zemljTipLV: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var profilOkusaLV: ListView
    private lateinit var jelaLV: ListView
    private lateinit var jelaList: MutableList<String>
    private lateinit var dodajJeloButton: Button
    private lateinit var dodajBiljkuButton: Button
    private lateinit var uslikajBiljkuButton: Button
    private lateinit var jeloEditText: EditText
    private lateinit var nazivEditText: EditText
    private lateinit var porodicaEditText: EditText
    private lateinit var medUpozorenjeEditText: EditText
    private lateinit var slikaBiljkeImageView: ImageView

    private var selektovaniMedKoristi: MutableList<String> = mutableListOf()
    private var selektovaniKlimTipovi: MutableList<String> = mutableListOf()
    private var selektovaniZemTipovi: MutableList<String> = mutableListOf()
    private var selektovaniProfOkusa: MutableList<String> = mutableListOf()

    private var trefleDAO = TrefleDAO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nova_biljka)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        trefleDAO.setContext(this)

        //ListView dio
        medKoristLV = findViewById(R.id.medicinskaKoristLV)
        klimTipLV = findViewById(R.id.klimatskiTipLV)
        zemljTipLV = findViewById(R.id.zemljisniTipLV)
        profilOkusaLV = findViewById(R.id.profilOkusaLV)

        //LV podaci fill
        initializeEnumListViewAdapter(
            medKoristLV,
            ArrayList(MedicinskaKorist.entries.map { it.opis }.toList())
        )
        initializeEnumListViewAdapter(
            klimTipLV,
            ArrayList(KlimatskiTip.entries.map { it.opis }.toList())
        )
        initializeEnumListViewAdapter(
            zemljTipLV,
            ArrayList(Zemljiste.entries.map { it.naziv }.toList())
        )
        initializeEnumListViewAdapter(
            profilOkusaLV,
            ArrayList(ProfilOkusaBiljke.entries.map { it.opis }.toList())
        )

        profilOkusaLV.choiceMode = ListView.CHOICE_MODE_SINGLE

        //BTNS dio
        dodajJeloButton = findViewById(R.id.dodajJeloBtn)
        dodajBiljkuButton = findViewById(R.id.dodajBiljkuBtn)
        uslikajBiljkuButton = findViewById(R.id.uslikajBiljkuBtn)

        nazivEditText = findViewById(R.id.nazivET)
        porodicaEditText = findViewById(R.id.porodicaET)
        medUpozorenjeEditText = findViewById(R.id.medicinskoUpozorenjeET)

        medKoristLV.setOnItemClickListener { _, _, position, _ ->
            val adapter1 = medKoristLV.adapter as ArrayAdapter<String>
            val checkedPositions = medKoristLV.checkedItemPositions
            selektovaniMedKoristi.clear()
            // Iterate through the checked positions
            for (i in 0 until checkedPositions.size()) {
                val itemPosition = checkedPositions.keyAt(i)
                if (checkedPositions.valueAt(i)) {
                    val selectedString = adapter1.getItem(itemPosition) as String
                    selektovaniMedKoristi.add(selectedString)
                }
            }
            println("Selected MED KORISTI strings: $selektovaniMedKoristi")
        }

        klimTipLV.setOnItemClickListener { _, _, position, _ ->
            val adapter1 = klimTipLV.adapter as ArrayAdapter<String>
            val checkedPositions = klimTipLV.checkedItemPositions
            selektovaniKlimTipovi.clear()
            // Iterate through the checked positions
            for (i in 0 until checkedPositions.size()) {
                val itemPosition = checkedPositions.keyAt(i)
                if (checkedPositions.valueAt(i)) {
                    val selectedString = adapter1.getItem(itemPosition) as String
                    selektovaniKlimTipovi.add(selectedString)
                }
            }
        }

        zemljTipLV.setOnItemClickListener { _, _, position, _ ->
            val adapter1 = zemljTipLV.adapter as ArrayAdapter<String>
            val checkedPositions = zemljTipLV.checkedItemPositions
            selektovaniZemTipovi.clear()
            // Iterate through the checked positions
            for (i in 0 until checkedPositions.size()) {
                val itemPosition = checkedPositions.keyAt(i)
                if (checkedPositions.valueAt(i)) {
                    val selectedString = adapter1.getItem(itemPosition) as String
                    selektovaniZemTipovi.add(selectedString)
                }
            }
        }
        profilOkusaLV.setOnItemClickListener { _, _, position, _ ->
            val adapter1 = profilOkusaLV.adapter as ArrayAdapter<String>
            val checkedPositions = profilOkusaLV.checkedItemPositions
            selektovaniProfOkusa.clear()
            // Iterate through the checked positions
            for (i in 0 until checkedPositions.size()) {
                val itemPosition = checkedPositions.keyAt(i)
                if (checkedPositions.valueAt(i)) {
                    val selectedString = adapter1.getItem(itemPosition) as String
                    selektovaniProfOkusa.add(selectedString)
                }
            }
        }

        jeloEditText = findViewById(R.id.jeloET)
        jelaLV = findViewById(R.id.jelaLV)
        jelaList = mutableListOf()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, jelaList)
        jelaLV.adapter = adapter
        var selectedPozicija = -1 //nista nije kliknuto na listi
        dodajJeloButton.setOnClickListener {
            val novoJelo = jeloEditText.text.toString().trim()
            val containsJelo = jelaList.any { it.equals(novoJelo, ignoreCase = true) }
            val duzinaIzvanGranica =
                novoJelo.length < 2 || novoJelo.length > 20 //AKO ISPUNJENO, IDE EROR
            if (novoJelo.isNotEmpty()) {
                //val selectedPosition = jelaLV.checkedItemPosition
                if (containsJelo) {
                    jeloEditText.setError("To jelo se već nalazi u listi jela!")
                } else if (duzinaIzvanGranica) {
                    jeloEditText.setError("Dužina teksta mora biti između 2 i 20!")
                } else { //novo jelo proslo provjere
                    //provjeri je li editujemo postojece jelo
                    if (selectedPozicija != -1) {//dodajJeloButton.text.equals("Izmijeni jelo")) {
                        jelaList[selectedPozicija] = novoJelo
                    } else { //ili dodajemo potencijalno novo jelo
                        jelaList.add(novoJelo)
                    }
                }
                adapter.notifyDataSetChanged()

                //Reset
                jeloEditText.setText("")
                dodajJeloButton.text = getString(R.string.dodaj_jelo)
                selectedPozicija = -1
            }
        }

        // Handle ListView item click
        jelaLV.setOnItemClickListener { _, _, position, _ ->
            val selectedJelo = jelaList[position]
            jeloEditText.setText(selectedJelo)
            dodajJeloButton.text = getString(R.string.izmijeni_jelo)
            selectedPozicija = position
        }

        dodajBiljkuButton.setOnClickListener {

            if (validationPassed()) {
                //zapakuj liste
                val zemljisteListaEnuma: MutableList<Zemljiste> = mutableListOf()
                //povezi stringove sa odg.enum values
                selektovaniZemTipovi.forEach { selectedString ->
                    val matchingEnum = Zemljiste.values().find { it.naziv == selectedString }
                    matchingEnum?.let { zemljisteListaEnuma.add(it) }
                }

                val klimaListaEnuma: MutableList<KlimatskiTip> = mutableListOf()
                //povezi stringove sa odg.enum values
                selektovaniKlimTipovi.forEach { selectedString ->
                    val matchingEnum = KlimatskiTip.values().find { it.opis == selectedString }
                    matchingEnum?.let { klimaListaEnuma.add(it) }
                }

                val medKoristiListaEnuma: MutableList<MedicinskaKorist> = mutableListOf()
                //povezi stringove sa odg.enum values
                selektovaniMedKoristi.forEach { selectedString ->
                    val matchingEnum = MedicinskaKorist.values().find { it.opis == selectedString }
                    matchingEnum?.let { medKoristiListaEnuma.add(it) }
                }

                val profilOkusaEnum: MutableList<ProfilOkusaBiljke> = mutableListOf()
                //povezi stringove sa odg.enum values
                selektovaniProfOkusa.forEach { selectedString ->
                    val matchingEnum = ProfilOkusaBiljke.values().find { it.opis == selectedString }
                    matchingEnum?.let { profilOkusaEnum.add(it) }
                }
                var validiranaBiljka = Biljka(
                    nazivEditText.text.toString(),
                    porodicaEditText.text.toString(),
                    medUpozorenjeEditText.text.toString(),
                    medKoristiListaEnuma,
                    profilOkusaEnum[0],
                    jelaList,
                    klimaListaEnuma,
                    zemljisteListaEnuma
                )
                provjeriPodatkeBiljkeAPI(validiranaBiljka)
                Toast.makeText(this,"Dohvacanje podataka, molimo sacekajte...",Toast.LENGTH_SHORT).show()
                //while(!getBiljkeUpdated().contains(validiranaBiljka)) {}
                    //finish()

            }
        }

        uslikajBiljkuButton.setOnClickListener {
            pokreniSlikanjeBiljkeIntent()
        }

    }

    private fun validationPassed(): Boolean {
        //funkcija validacije
        //ako sve ok salje na main
        var validationPassed = true
        //EditText
        val nazivBiljke = nazivEditText.text.toString()
        val porodicaBiljke = porodicaEditText.text.toString()
        val medUpozorenjeBiljke = medUpozorenjeEditText.text.toString()

        if (nazivBiljke.length < 2 || nazivBiljke.length > 40) {
            validationPassed = false
            nazivEditText.setError("Dužina teksta mora biti između 2 i 40!")
            Toast.makeText(this, "Naziv biljke nije ispravan!", Toast.LENGTH_SHORT).show()
        }
        if (!imaLatinskiNaziv(nazivBiljke)) {
            validationPassed = false
            nazivEditText.setError("U nazivu biljke nedostaje latinski naziv naveden u malim zagradama!")
            Toast.makeText(this, "Naziv biljke nije ispravan!", Toast.LENGTH_SHORT).show()
        }
        if (porodicaBiljke.length < 2 || porodicaBiljke.length > 20) {
            validationPassed = false
            porodicaEditText.setError("Dužina teksta mora biti između 2 i 20!")
            Toast.makeText(this, "Porodica biljke nije ispravna!", Toast.LENGTH_SHORT).show()
        }
        if (medUpozorenjeBiljke.length < 2 || medUpozorenjeBiljke.length > 20) {
            validationPassed = false
            medUpozorenjeEditText.setError("Dužina teksta mora biti između 2 i 20!")
            Toast.makeText(this, "Medicinsko upozorenje biljke nije ispravno!", Toast.LENGTH_SHORT)
                .show()
        }

        //u listi jela bar jedno jelo dodano
        if (jelaList.isEmpty()) {
            validationPassed = false
            Toast.makeText(this, "Lista jela je prazna!", Toast.LENGTH_LONG).show()
        }

        if (selektovaniMedKoristi.isEmpty()) {
            Toast.makeText(
                this,
                "Nije selektovana niti jedna medicinska korist!",
                Toast.LENGTH_SHORT
            ).show()
            validationPassed = false
        }
        if (selektovaniKlimTipovi.isEmpty()) {
            Toast.makeText(this, "Nije selektovan niti jedan klimatski tip!", Toast.LENGTH_SHORT)
                .show()
            validationPassed = false
        }

        if (selektovaniZemTipovi.isEmpty()) {
            Toast.makeText(this, "Nije selektovan niti jedan zemljišni tip!", Toast.LENGTH_SHORT)
                .show()
            validationPassed = false
        }
        if (selektovaniProfOkusa.isEmpty()) {
            Toast.makeText(this, "Nije selektovan niti jedan okus!", Toast.LENGTH_SHORT).show()
            validationPassed = false
        }

        return validationPassed
    }

    private fun provjeriPodatkeBiljkeAPI(b: Biljka) {
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        scope.launch {
            try {
                val finalnaBiljka = trefleDAO.fixData(b)
                addBiljka(finalnaBiljka)
                BiljkaRepository.saveBiljkaToDB(this@NovaBiljkaActivity,b)
                BiljkaRepository.saveBiljkaImageToDB(this@NovaBiljkaActivity, finalnaBiljka)
                onSuccess(finalnaBiljka)
            } catch (e: Exception) {
                e.printStackTrace()
                onError()
            }
        }
    }

    fun onSuccess(finalnaB: Biljka) {
        if (getBiljkeUpdated().contains(finalnaB)) {
            Toast.makeText(this, "Uspjesno provjerena biljka sa API-jem", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        else
            Toast.makeText(this, "Biljka se dodaje jos uvijek...", Toast.LENGTH_SHORT).show()
    }

    fun onError() {
        Toast.makeText(
            this,
            "Greska prilikom provjere podataka biljke sa API-ja",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun initializeEnumListViewAdapter(listView: ListView, values: ArrayList<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, values)
        listView.adapter = adapter
        // enable multiple choice mode --- za tri LV treba
        listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
    }

    private fun pokreniSlikanjeBiljkeIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            slikaBiljkeImageView = findViewById(R.id.slikaIV)
            slikaBiljkeImageView.setImageBitmap(imageBitmap)
            //slikaBiljkeImageView.visibility = View.VISIBLE
        }
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
    }

    fun imaLatinskiNaziv(input: String): Boolean {
        //provjerava da li u nazivu biljke ima (nekiTekst)
        val regex = Regex("\\(.*?\\)")
        return regex.containsMatchIn(input)
    }
}