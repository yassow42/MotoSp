package com.creativeoffice.motosp.utils

import com.creativeoffice.motosp.Datalar.ForumKonuData

class EventBusDataEvents {

    internal class KonulariGonder(var konuListEventBus: ArrayList<ForumKonuData>)
    internal class KullaniciAdi(var userName: String)
}