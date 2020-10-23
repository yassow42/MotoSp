package com.creativeoffice.motosp.utils

import com.creativeoffice.motosp.Datalar.ForumKonuData
import com.creativeoffice.motosp.Datalar.Users

class EventBusDataEvents {

    internal class KonulariGonder(var konuListEventBus: ArrayList<ForumKonuData>)
    internal class KullaniciData(var userData: Users)
}