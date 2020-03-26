package com.creativeoffice.motosp.Datalar

import java.util.HashMap

class ModelDetaylariData {
    var marka: String? = null
    var model: String? = null
    var agirlik: String? = null
    var beygir: String? = null
    var devir: String? = null
    var hiz: String? = null
    var kategori: String? = null
    var motorVideo: String? = null
    var silindirHacmi: String? = null
    var tanitim: String? = null
    var tork: String? = null
    var yakitTuk: String? = null
    var yakitkap: String? = null
    var goruntulenme_sayisi: Int? = null

    var yorumlar: HashMap<String, Yorumlar>? = null
    var yy_parcalar: HashMap<String, Parcalar>? = null
    var yy_yakit_verileri: HashMap<String, YakitTuketimi>? = null


    constructor()
    constructor(
        marka: String?,
        model: String?,
        agirlik: String?,
        beygir: String?,
        devir: String?,
        hiz: String?,
        kategori: String?,
        motorVideo: String?,
        silindirHacmi: String?,
        tanitim: String?,
        tork: String?,
        yakitTuk: String?,
        yakitkap: String?,
        goruntulenme_sayisi: Int?
    ) {
        this.marka = marka
        this.model = model
        this.agirlik = agirlik
        this.beygir = beygir
        this.devir = devir
        this.hiz = hiz
        this.kategori = kategori
        this.motorVideo = motorVideo
        this.silindirHacmi = silindirHacmi
        this.tanitim = tanitim
        this.tork = tork
        this.yakitTuk = yakitTuk
        this.yakitkap = yakitkap
        this.goruntulenme_sayisi = goruntulenme_sayisi

    }


    data class Yorumlar(
        val isim: String? = null,
        val yorum: String? = null,
        val tarih: Long? = null,
        val yorum_key: String? = null,
        val yorum_yapilan_model: String? = null,
        val yorum_yapan_kisi: String? = null
    )


    data class Parcalar(
        val parca_ismi: String? = null,
        val parca_model_yorumu: String? = null,
        val parca_uyum_model_yili: String? = null,
        val kullanici_adi: String? = null,
        val parca_key: String? = null,
        val marka: String? = null,
        val model: String? = null
    )

    data class YakitTuketimi(
        val yakitTuk: Float? = null,
        val kullanici_adi: String? = null,
        val motor_yili: String? = null
    )


}