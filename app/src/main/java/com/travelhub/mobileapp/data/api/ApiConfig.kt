package com.travelhub.mobileapp.data.api

object ApiConfig {
    // Emulator talking to a Django server running on the same PC
    const val BASE_URL_EMULATOR = "http://10.0.2.2:8000/api/"

    // Swap this manually while testing on a physical device on the same Wi-Fi
    const val BASE_URL_LAN = "http://192.168.1.100:8000/api/" // replace with your PC's actual LAN IP

    // Production, once Render deployment is live
    const val BASE_URL_PRODUCTION = "https://<your-app>.onrender.com/api/"

    // Single switch point — change this one line to change environment everywhere
    const val BASE_URL = BASE_URL_EMULATOR
}
