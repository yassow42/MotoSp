package com.creativeoffice.motosp.Datalar

class UserDetails {
    var puan:Int? = null
    var biyografi:String? = null
    var kullanilan_motor_marka:String? = null
    var kullanilan_motor_model:String? = null
    var profile_picture:String?=null




    constructor()
    constructor(puan: Int?, biyografi: String?, kullanilan_motor_marka: String?, kullanilan_motor_model: String?, profile_picture: String?) {
        this.puan = puan
        this.biyografi = biyografi
        this.kullanilan_motor_marka = kullanilan_motor_marka
        this.kullanilan_motor_model = kullanilan_motor_model
        this.profile_picture = profile_picture
    }

}