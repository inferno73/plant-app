package com.example.rma_spirala

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

const val MEDICINSKI: String = "Medicinski"
const val KUHARSKI: String = "Kuharski"
const val BOTANICKI: String = "Botanicki"

val spinnerColors: Array<String> =
    arrayOf("red", "blue", "yellow", "orange", "purple", "brown", "green")
//todo kad dodje do promjene moda trenutnaLista je relevantna za prikaz
class MainActivity : AppCompatActivity() {
    private lateinit var biljkeRV: RecyclerView
    private lateinit var biljkeAdapter: BiljkaListAdapter
    private lateinit var resetButton: Button
    private lateinit var novaBiljkaButton: Button
    private lateinit var spinner_modes: Spinner
    private lateinit var colorSP: Spinner
    private lateinit var searchButton: Button
    private lateinit var searchImg: ImageView
    private lateinit var searchET: EditText
    private lateinit var dao: TrefleDAO
    val modes = arrayOf(MEDICINSKI, KUHARSKI, BOTANICKI)
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var colorAdapter: ArrayAdapter<String>

    private var biljkeLista: MutableList<Biljka> = mutableListOf()

    //ova lista je trenutna, u njoj su biljke koje su trenutno prikazane na ekranu/filtrirane etc
    private var trenutnaLista: MutableList<Biljka> = mutableListOf()

    //if searcBTN is pressed filtriranje biljaka sa APIJA flowerColor je disabled
    private var isRVItemClickable = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fillBiljkaDatabaseIfNeeded() //populate db ako treba

        dao = TrefleDAO()
        dao.setContext(this)

        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, modes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //adapter to spinner
        spinner_modes = findViewById(R.id.modSpinner)
        spinner_modes.adapter = adapter
        spinner_modes.setSelection(adapter.getPosition(MEDICINSKI))

        spinner_modes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                isRVItemClickable = true
                val selectedMode = modes[position]
                displayBiljkeForMode(selectedMode)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Initialize RecyclerView and adapter with an empty list
        initRecyclerView()

        //colorSpinner
        colorAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerColors)
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        colorSP = findViewById(R.id.bojaSPIN)
        colorSP.adapter = colorAdapter
        searchImg = findViewById(R.id.searchButton)
        searchButton = findViewById(R.id.brzaPretraga)
        searchET = findViewById(R.id.pretragaET)

        searchButton.setOnClickListener {
            if (searchET.text.toString().isNullOrEmpty() || colorSP.selectedItem.toString()
                    .isNullOrEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Provjerite da li ste ukucali rijec u pretragu i ispravno selektovali boju u spinneru!",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this, "Pretraga pokrenuta, molim sacekajte rezultate!", Toast.LENGTH_LONG
                ).show()
                isRVItemClickable = false
                pokreniPretraguPoBojiIRijeci()
            }
        }
        searchImg.visibility = View.INVISIBLE
        searchET.visibility = View.INVISIBLE
        searchButton.visibility = View.INVISIBLE
        colorSP.visibility = View.INVISIBLE

        resetButton = findViewById(R.id.resetBtn)
        resetButton.setOnClickListener {
            //reset prikaz
            isRVItemClickable = true
            val selectedMode = spinner_modes.selectedItem.toString()
            trenutnaLista = biljkeLista
            displayBiljkeForMode(selectedMode)
        }

        novaBiljkaButton = findViewById(R.id.novaBiljkaBtn)
        novaBiljkaButton.setOnClickListener {
            //otvara novu aktivnost
            val intent = Intent(this, NovaBiljkaActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        spinner_modes.setSelection(adapter.getPosition(MEDICINSKI))
        loadBiljkeFromDatabase()
    }
    private fun loadBiljkeFromDatabase() {
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        scope.launch {
            val result = BiljkaRepository.getAllBiljkasFromDB(this@MainActivity)
            when (result) {
                is List<Biljka> -> {
                    biljkeLista = result.toMutableList()
                    trenutnaLista = biljkeLista
                    isRVItemClickable = true
                    displayBiljkeForMode(MEDICINSKI)
                }
                else -> onErrorDB()
            }
        }
    }
    private fun initRecyclerView() {
        biljkeRV = findViewById(R.id.biljkeRV)
        biljkeRV.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
        biljkeAdapter = BiljkaListAdapter(trenutnaLista, MEDICINSKI, dao) { biljka -> filterBiljke(biljka, spinner_modes.selectedItem.toString()) }
        biljkeRV.adapter = biljkeAdapter
    }

    private fun displayBiljkeForMode(mode: String) {
        // Filter and display biljke based on the selected mode
        // Update biljkeAdapter with the filtered biljke list

        if (mode != BOTANICKI) {
            searchImg.visibility = View.GONE
            searchET.visibility = View.GONE
            searchButton.visibility = View.GONE
            colorSP.visibility = View.GONE
        } else {
            searchImg.visibility = View.VISIBLE
            searchET.visibility = View.VISIBLE
            searchButton.visibility = View.VISIBLE
            colorSP.visibility = View.VISIBLE
        }
        biljkeRV = findViewById(R.id.biljkeRV)
        biljkeRV.layoutManager =
            LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
        biljkeAdapter =
            BiljkaListAdapter(listOf(), mode, dao) { biljka -> filterBiljke(biljka, mode) }
        biljkeRV.adapter = biljkeAdapter
        biljkeAdapter.updateBiljke(trenutnaLista)
    }

    //funkcija koja se izvrsava nakon klika na listItem
    private fun filterBiljke(biljka: Biljka, spMode: String) {

        if (!isRVItemClickable) return

        trenutnaLista = mutableListOf()

        when (spMode) {
            MEDICINSKI -> {
                for (biljkaObj in biljkeLista)
                    if (biljkaObj.medicinskeKoristi!!.any { it in biljka.medicinskeKoristi!! })
                        trenutnaLista.add(biljkaObj)
            }

            KUHARSKI -> {
                for (biljkaObj in biljkeLista)
                    if (biljkaObj.jela!!.any { it in biljka.jela!! } || biljkaObj.profilOkusa == biljka.profilOkusa)
                        trenutnaLista.add(biljkaObj)
            }

            BOTANICKI -> {
                for (biljkaObj in biljkeLista)
                    if (biljkaObj.klimatskiTipovi!!.any { it in biljka.klimatskiTipovi!! } &&
                        biljkaObj.zemljisniTipovi!!.any { it in biljka.zemljisniTipovi!! }
                        && biljkaObj.porodica == biljka.porodica)
                        trenutnaLista.add(biljkaObj)
            }
        }
        biljkeAdapter.updateBiljke(trenutnaLista)
    }

    private fun pokreniPretraguPoBojiIRijeci() {
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        scope.launch {
            try {
                trenutnaLista = dao.getPlantsWithFlowerColor(
                    colorSP.selectedItem.toString(), searchET.text.toString()
                ).toMutableList()
                displayBiljkeForMode(BOTANICKI)
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                onError()
            }
        }
    }

    fun onSuccess() {
        Toast.makeText(this, "Uspjesno dohvaceni podaci sa API-ja", Toast.LENGTH_SHORT).show()
    }

    fun onError() {
        Toast.makeText(this, "Greska prilikom dohvatanja podataka sa API-ja", Toast.LENGTH_SHORT)
            .show()
    }

    fun onSuccessDB(l: List<Biljka>) {
        Toast.makeText(this, "Uspjesno dohvaceni podaci iz baze", Toast.LENGTH_SHORT).show()
        biljkeLista = l.toMutableList()
    }

    fun onErrorDB() {
        Toast.makeText(this, "Greska prilikom dohvatanja podataka iz baze", Toast.LENGTH_SHORT)
            .show()
    }
    private fun fillBiljkaDatabaseIfNeeded() {
        val prefs = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val isDbPopulated = prefs.getBoolean("isDbPopulated", false)

        if (!isDbPopulated) {
            val scope = CoroutineScope(Job() + Dispatchers.IO)
            scope.launch {
                BiljkaRepository.fillBiljkaDatabase(this@MainActivity)
                prefs.edit().putBoolean("isDbPopulated", true).apply()
            }
        }
    }

}

