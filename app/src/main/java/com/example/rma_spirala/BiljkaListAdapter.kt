package com.example.rma_spirala

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BiljkaListAdapter(
    private var biljke: List<Biljka>,
    private val spinnerMode: String,
    private val dao: TrefleDAO,
    private val onItemClicked: (biljka: Biljka) -> Unit

) : RecyclerView.Adapter<BiljkaListAdapter.BiljkaViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BiljkaViewHolder {

        val layoutResId = if (spinnerMode.equals(MEDICINSKI, true)) {
            R.layout.med_elements_list
        } else if (spinnerMode.equals(KUHARSKI, true)) {
            R.layout.kuh_elements_list
        } else {
            //ako botanicki
            R.layout.bot_elements_list
        }

        val view = LayoutInflater
            .from(parent.context)
            .inflate(layoutResId, parent, false)

        return BiljkaViewHolder(view)
    }

    override fun getItemCount(): Int = biljke.size
    override fun onBindViewHolder(holder: BiljkaViewHolder, position: Int) {
        holder.nazivBiljka.text = biljke[position].naziv
        if (spinnerMode.equals(MEDICINSKI)) {

            holder.upozorenjeBiljka!!.text = biljke[position].medicinskoUpozorenje
            val broj_koristi: Int? = biljke[position].medicinskeKoristi?.size
            //postavi u stringove prve tri med koristi (ako ih nema, prazan string onda)

            val string1 = if (broj_koristi != 0) biljke[position].medicinskeKoristi?.get(0)?.opis else ""
            val string2 = if (broj_koristi!! > 1) biljke[position].medicinskeKoristi?.get(1)?.opis else ""
            val string3 = if (broj_koristi > 2) biljke[position].medicinskeKoristi?.get(2)?.opis else ""


            holder.korist1Biljka!!.text = string1
            holder.korist2Biljka!!.text = string2
            holder.korist3Biljka!!.text = string3


        } else if (spinnerMode.equals(KUHARSKI)) {

            holder.profilOkusaBiljka!!.text = biljke[position].profilOkusa?.opis
            val broj_jela: Int = biljke[position].jela?.size ?: 0
            //postavi u stringove prve tri med koristi (ako ih nema, prazan string onda)

            val string1 = if (broj_jela != 0) biljke[position].jela?.getOrElse(0) { "" } else ""
            val string2 = if (broj_jela > 1) biljke[position].jela?.getOrElse(1) { "" } else ""
            val string3 = if (broj_jela > 2) biljke[position].jela?.getOrElse(2) { "" } else ""


            holder.jelo1Biljka!!.text = string1
            holder.jelo2Biljka!!.text = string2
            holder.jelo3Biljka!!.text = string3
        } else {
            //botanicki mod
            //EDIT PRVI klimatski i zemlj tip
            holder.porodicaBiljka!!.text = biljke[position].porodica
            holder.klimaBiljka!!.text = biljke[position].klimatskiTipovi?.get(0)?.opis
            holder.zemljisteBiljka!!.text = biljke[position].zemljisniTipovi?.get(0)?.naziv
        }

        //postavljanje ISTE slike za sve
        val context: Context = holder.slikaBiljka.context
        var imgResId: Int = context.resources
            .getIdentifier("bosiljak", "drawable", context.packageName)
        CoroutineScope(Dispatchers.Main).launch {
            val bitmap = dao.getImage(biljke[position])
            holder.slikaBiljka.setImageBitmap(bitmap)
        }
        holder.itemView.setOnClickListener { onItemClicked(biljke[position]) }

    }

    fun updateBiljke(biljke: List<Biljka>) {
        this.biljke = biljke
        notifyDataSetChanged()
    }

    inner class BiljkaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nazivBiljka: TextView = itemView.findViewById(R.id.nazivItem)
        val slikaBiljka: ImageView = itemView.findViewById(R.id.slikaItem)

        // Views for medicinski mode
        val upozorenjeBiljka: TextView? =
            if (spinnerMode == MEDICINSKI) itemView.findViewById(R.id.upozorenjeItem) else null
        val korist1Biljka: TextView? =
            if (spinnerMode == MEDICINSKI) itemView.findViewById(R.id.korist1Item) else null
        val korist2Biljka: TextView? =
            if (spinnerMode == MEDICINSKI) itemView.findViewById(R.id.korist2Item) else null
        val korist3Biljka: TextView? =
            if (spinnerMode == MEDICINSKI) itemView.findViewById(R.id.korist3Item) else null

        // Views for kuharski mode
        val profilOkusaBiljka: TextView? =
            if (spinnerMode == KUHARSKI) itemView.findViewById(R.id.profilOkusaItem) else null
        val jelo1Biljka: TextView? =
            if (spinnerMode == KUHARSKI) itemView.findViewById(R.id.jelo1Item) else null
        val jelo2Biljka: TextView? =
            if (spinnerMode == KUHARSKI) itemView.findViewById(R.id.jelo2Item) else null
        val jelo3Biljka: TextView? =
            if (spinnerMode == KUHARSKI) itemView.findViewById(R.id.jelo3Item) else null

        // Views for botanicki mode
        val porodicaBiljka: TextView? =
            if (spinnerMode == BOTANICKI) itemView.findViewById(R.id.porodicaItem) else null
        val klimaBiljka: TextView? =
            if (spinnerMode == BOTANICKI) itemView.findViewById(R.id.klimatskiTipItem) else null
        val zemljisteBiljka: TextView? =
            if (spinnerMode == BOTANICKI) itemView.findViewById(R.id.zemljisniTipItem) else null
    }
}