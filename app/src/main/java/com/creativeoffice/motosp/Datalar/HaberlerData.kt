package com.creativeoffice.motosp.Datalar

class HaberlerData {
    var haber_baslik:String?=null
    var haber_icerik:String?=null
    var haber_video:String?=null
    var haber_eklenme_zamani:Long?=null

    constructor(haber_baslik: String?, haber_icerik: String?, haber_video: String?, haber_eklenme_zamani: Long?) {
        this.haber_baslik = haber_baslik
        this.haber_icerik = haber_icerik
        this.haber_video = haber_video
        this.haber_eklenme_zamani = haber_eklenme_zamani
    }

    constructor()
}