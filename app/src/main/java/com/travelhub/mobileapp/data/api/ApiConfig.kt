package com.travelhub.mobileapp.data.api

object ApiConfig {
    // Emulator talking to a Django server running on the same PC
    const val BASE_URL_EMULATOR = "http://10.0.2.2:8000/api/"

    // Physical device on the same Wi-Fi as lap -- Huawei Y6s
    const val BASE_URL_LAN = "http://192.168.43.202:8000/api/"

    // Production, once Render deployment is live not yet :) but in future
    const val BASE_URL_PRODUCTION = "https://<your-app>.onrender.com/api/"

    // Single switch point — change this one line to change environment everywhere
    const val BASE_URL = BASE_URL_LAN
}