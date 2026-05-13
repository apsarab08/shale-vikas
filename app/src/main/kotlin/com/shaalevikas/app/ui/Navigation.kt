package com.shaalevikas.app.ui

object Routes {
    const val SPLASH        = "splash"
    const val ROLE_SELECT   = "role_select"
    const val ALUMNI_LOGIN  = "alumni_login"
    const val ALUMNI_REG    = "alumni_register"
    const val ADMIN_LOGIN   = "admin_login"
    const val ALUMNI_HOME   = "alumni_home"
    const val ADMIN_HOME    = "admin_home"
    const val NEED_DETAIL   = "need_detail/{needId}"
    const val ADD_NEED      = "add_need"
    const val EDIT_NEED     = "edit_need/{needId}"

    fun needDetail(needId: String) = "need_detail/$needId"
    fun editNeed(needId: String)   = "edit_need/$needId"
}
