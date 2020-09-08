package com.creativeoffice.motosp.Datalar

class BayilerData {

    var sehirAdi: String? = null
    var ilceler: HashMap<String, ilcelerData>? = null

    constructor(sehirAdi: String?) {
        this.sehirAdi = sehirAdi
    }

    constructor()


    data class ilcelerData(
        var sehirAdi: String? = null,
        var ilceAdi: String? = null
        //  , var bayiler: HashMap<String, BayiDetaylari>? = null
    )

    data class BayiDetaylari(
        var bayiAdi: String? = null,
        var numara: String? = null,
        var adres: String? = null,
        var sehirAdi: String? = null,
        var ilceAdi: String? = null,
        var resim: String? = null
    )

    data class BayiYorumlari(
        var yildiz: Float? = null,
        var yorum: String? = null,
        var yorum_zamani: Long? = null,
        var yorum_key: String? = null,
        var yorum_yapan_key: String? = null
    )

}