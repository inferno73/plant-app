package com.example.rma_spirala

//file sa potrebnim funkcijama za rad sa podacima
private var biljke = listOf(
    Biljka(
        naziv = "Bosiljak (Ocimum basilicum)",
        porodica = "Lamiaceae (usnate)",
        medicinskoUpozorenje = "Može iritati kožu osjetljivu na sunce. Preporučuje se oprezna upotreba pri korištenju ulja bosiljka.",
        medicinskeKoristi = listOf(
            MedicinskaKorist.SMIRENJE,
            MedicinskaKorist.REGULACIJAPROBAVE
        ),
        profilOkusa = ProfilOkusaBiljke.BEZUKUSNO,
        jela = listOf("Salata od paradajza", "Punjene tikvice"),
        klimatskiTipovi = listOf(KlimatskiTip.SREDOZEMNA, KlimatskiTip.SUBTROPSKA),
        zemljisniTipovi = listOf(Zemljiste.PJESKOVITO, Zemljiste.ILOVACA)
    ),
    Biljka(
        naziv = "Nana (Mentha spicata)",
        porodica = "Lamiaceae (metvice)",
        medicinskoUpozorenje = "Nije preporučljivo za trudnice, dojilje i djecu mlađu od 3 godine.",
        medicinskeKoristi = listOf(
            MedicinskaKorist.PROTUUPALNO,
            MedicinskaKorist.PROTIVBOLOVA
        ),
        profilOkusa = ProfilOkusaBiljke.MENTA,
        jela = listOf("Jogurt sa voćem", "Gulaš"),
        klimatskiTipovi = listOf(KlimatskiTip.SREDOZEMNA, KlimatskiTip.UMJERENA),
        zemljisniTipovi = listOf(Zemljiste.GLINENO, Zemljiste.CRNICA)
    ),
    Biljka(
        naziv = "Kamilica (Matricaria chamomilla)",
        porodica = "Asteraceae (glavočike)",
        medicinskoUpozorenje = "Može uzrokovati alergijske reakcije kod osjetljivih osoba.",
        medicinskeKoristi = listOf(
            MedicinskaKorist.SMIRENJE,
            MedicinskaKorist.PROTUUPALNO
        ),
        profilOkusa = ProfilOkusaBiljke.AROMATICNO,
        jela = listOf("Čaj od kamilice"),
        klimatskiTipovi = listOf(KlimatskiTip.UMJERENA, KlimatskiTip.SUBTROPSKA),
        zemljisniTipovi = listOf(Zemljiste.PJESKOVITO, Zemljiste.KRECNJACKO)
    ),
    Biljka(
        naziv = "Ružmarin (Rosmarinus officinalis)",
        porodica = "Lamiaceae (metvice)",
        medicinskoUpozorenje = "Treba ga koristiti umjereno i konsultovati se sa ljekarom pri dugotrajnoj upotrebi ili upotrebi u većim količinama.",
        medicinskeKoristi = listOf(
            MedicinskaKorist.PROTUUPALNO,
            MedicinskaKorist.REGULACIJAPRITISKA
        ),
        profilOkusa = ProfilOkusaBiljke.AROMATICNO,
        jela = listOf("Pečeno pile", "Grah", "Gulaš"),
        klimatskiTipovi = listOf(KlimatskiTip.SREDOZEMNA, KlimatskiTip.SUHA),
        zemljisniTipovi = listOf(Zemljiste.SLJUNKOVITO, Zemljiste.KRECNJACKO)
    ),
    Biljka(
        naziv = "Lavanda (Lavandula angustifolia)",
        porodica = "Lamiaceae (metvice)",
        medicinskoUpozorenje = "Nije preporučljivo za trudnice, dojilje i djecu mlađu od 3 godine. Također, treba izbjegavati kontakt lavanda ulja sa očima.",
        medicinskeKoristi = listOf(
            MedicinskaKorist.SMIRENJE,
            MedicinskaKorist.PODRSKAIMUNITETU
        ),
        profilOkusa = ProfilOkusaBiljke.AROMATICNO,
        jela = listOf("Jogurt sa voćem"),
        klimatskiTipovi = listOf(KlimatskiTip.SREDOZEMNA, KlimatskiTip.SUHA),
        zemljisniTipovi = listOf(Zemljiste.PJESKOVITO, Zemljiste.KRECNJACKO)
    ),
    Biljka(
        naziv = "Peršun (Petroselinum crispum)",
        porodica = "Apiaceae (štitarka)",
        medicinskoUpozorenje = "Osobe koje uzimaju lijekove za razrjeđivanje krvi trebaju izbjegavati uzimanje peršina jer može pojačati učinak lijekova.",
        medicinskeKoristi = listOf(
            MedicinskaKorist.REGULACIJAPROBAVE,
            MedicinskaKorist.PODRSKAIMUNITETU
        ),
        profilOkusa = ProfilOkusaBiljke.AROMATICNO,
        jela = listOf("Juha", "Umak", "Salata"),
        klimatskiTipovi = listOf(KlimatskiTip.UMJERENA),
        zemljisniTipovi = listOf(Zemljiste.CRNICA),
    ),
    Biljka(
        naziv = "Kopriva (Urtica dioica)",
        porodica = "Urticaceae (koprive)",
        medicinskoUpozorenje = " Upotreba koprive može izazvati alergijske reakcije kod nekih osoba.",
        medicinskeKoristi = listOf(MedicinskaKorist.SMIRENJE),
        profilOkusa = ProfilOkusaBiljke.GORKO,
        jela = listOf("Čaj od koprive", "Juha"),
        klimatskiTipovi = listOf(KlimatskiTip.UMJERENA),
        zemljisniTipovi = listOf(Zemljiste.CRNICA),
    ),
    Biljka(
        naziv = "Majčina dušica (Thymus vulgaris)",
        porodica = "Lamiaceae (usnate)",
        medicinskoUpozorenje = "Ne preporučuje se kod trudnica i dojilja.",
        medicinskeKoristi = listOf(MedicinskaKorist.REGULACIJAPROBAVE),
        profilOkusa = ProfilOkusaBiljke.AROMATICNO,
        jela = listOf("Rižoto", "Mesna jela"),
        klimatskiTipovi = listOf(KlimatskiTip.UMJERENA, KlimatskiTip.SUHA),
        zemljisniTipovi = listOf(Zemljiste.PJESKOVITO),
    ),
    Biljka(
        naziv = "Korijander (Coriandrum sativum)",
        porodica = " Apiaceae (štitarka)",
        medicinskoUpozorenje = "Osobe koje imaju problema s alergijama na hranu trebaju izbjegavati korijander.",
        medicinskeKoristi = listOf(MedicinskaKorist.SMIRENJE, MedicinskaKorist.REGULACIJAPROBAVE),
        profilOkusa = ProfilOkusaBiljke.CITRUSNI,
        jela = listOf("Guacamole", "Curry"),
        klimatskiTipovi = listOf(KlimatskiTip.UMJERENA),
        zemljisniTipovi = listOf(Zemljiste.CRNICA),
    ),
    Biljka(
        naziv = "Origano (Origanum vulgare)",
        porodica = "Lamiaceae (usnate)",
        medicinskoUpozorenje = "Prekomjerna konzumacija origana može izazvati iritaciju probavnog sustava.",
        medicinskeKoristi = listOf(MedicinskaKorist.REGULACIJAPROBAVE),
        profilOkusa = ProfilOkusaBiljke.AROMATICNO,
        jela = listOf("Pizza", "Lasagne", "Umaci"),
        klimatskiTipovi = listOf(KlimatskiTip.UMJERENA),
        zemljisniTipovi = listOf(Zemljiste.PJESKOVITO),
    )
)

private var updatedBiljke = mutableListOf<Biljka>()

fun getBiljkeUpdated(): List<Biljka> {
    return biljke + updatedBiljke
}

fun addBiljka(biljka: Biljka) {
    updatedBiljke.add(biljka)
}





