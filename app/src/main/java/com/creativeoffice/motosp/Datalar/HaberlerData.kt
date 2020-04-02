package com.creativeoffice.motosp.Datalar

class HaberlerData {
    var haber_baslik: String? = null
    var haber_icerik: String? = null
    var haber_video: String? = null
    var haber_eklenme_zamani: Long? = null
    var haber_key: String? = null
    var haber_altbaslik: String? = null


    constructor()
    constructor(haber_baslik: String?, haber_icerik: String?, haber_video: String?, haber_eklenme_zamani: Long?, haber_key: String?, haber_altbaslik: String?) {
        this.haber_baslik = haber_baslik
        this.haber_icerik = haber_icerik
        this.haber_video = haber_video
        this.haber_eklenme_zamani = haber_eklenme_zamani
        this.haber_key = haber_key
        this.haber_altbaslik = haber_altbaslik
    }
}