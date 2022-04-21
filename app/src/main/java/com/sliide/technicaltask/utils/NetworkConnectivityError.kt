package com.sliide.technicaltask.utils

import java.io.IOException

class NetworkConnectivityError : IOException() {
     override val message: String
        get() = "No Internet Connection"
}