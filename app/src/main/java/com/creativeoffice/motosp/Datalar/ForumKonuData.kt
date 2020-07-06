package com.creativeoffice.motosp.Datalar

class ForumKonuData(
    var acilma_zamani: Long? = null,
    var son_cevap_zamani: Long? = null,
    var konu_basligi: String? = null,
    var konu_sahibi_cevap: String? = null,
    var konu_key: String? = null,
    var konuyu_acan: String? = null,
    var konuyu_acan_key: String? = null,
    val yorumlar: HashMap<String, son_cevap>? = null
) {

    data class son_cevap(
        var cevap: String? = null,
        var cevap_key: String? = null,
        var cevap_yazan: String? = null,
        var cevap_zamani: Long? = null,
        var cevap_yazan_key: String? = null,
        var cevap_yazilan_key: String? = null

    )

    data class cevaplar(

        var cevap: String? = null,
        var Foto: String? = null,
        var cevap_key: String? = null,
        var cevap_yazan: String? = null,
        var cevap_zamani: Long? = null,
        var cevap_yazan_key: String? = null,
        var cevap_yazilan_key: String? = null
    )

}