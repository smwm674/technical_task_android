package com.sliide.technicaltask.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CheckInternetConnection @Inject constructor(@ApplicationContext private val _context: Context) {

    fun isNetworkAvailable(): Boolean {
        return isOnline()
    }

    /*
    * isOnline() function check that the internet connection is available or not
    * function returns true when internet connection is available
    * function returns false when internet connection is not available
    * */
    fun isOnline(): Boolean {
        try {
            val p1 = Runtime.getRuntime().exec("ping -c 1 www.google.com")
            val returnVal = p1.waitFor()
            return returnVal == 0
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }


}
