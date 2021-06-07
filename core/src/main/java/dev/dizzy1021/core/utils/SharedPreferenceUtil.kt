package dev.dizzy1021.core.utils

import android.content.SharedPreferences
import javax.inject.Inject

class SharedPreferenceUtil @Inject constructor(
    private val pref: SharedPreferences
) {

    fun getStatusOnboard(): Boolean {
        return pref.getBoolean(PREF_ONBOARD, false)
    }

    fun setStatusOnboard(value: Boolean) {
        val editor: SharedPreferences.Editor = pref.edit()

        editor.putBoolean(PREF_ONBOARD, value)
        editor.apply()
    }

    fun getUser(): String? {
        return pref.getString(PREF_USER, null)
    }

    fun setUser() {
        val editor: SharedPreferences.Editor = pref.edit()
        val user = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InRlc3QtdXNlciJ9.ak5a7h5HIN7fbab3KKEvzzkhG8MC_gxzSoSVBOP2ZjA"

        editor.putString(PREF_USER, user)
        editor.apply()
    }
}