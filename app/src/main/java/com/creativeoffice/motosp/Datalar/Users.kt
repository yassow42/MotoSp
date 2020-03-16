package com.creativeoffice.motosp.Datalar

class Users {
    var email: String? = null
    var password: String? = null
    var user_name: String? = null
    var user_id: String? = null

    var user_details:UserDetails? = null

    constructor()
    constructor(email: String?, password: String?, user_name: String?, user_id: String?, user_detail: UserDetails?) {
        this.email = email
        this.password = password
        this.user_name = user_name
        this.user_id = user_id
        this.user_details = user_detail
    }

}