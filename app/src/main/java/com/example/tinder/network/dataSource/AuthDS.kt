package com.example.tinder.network.dataSource

import android.net.wifi.hotspot2.pps.Credential
import com.example.tinder.network.Results
import com.google.firebase.auth.PhoneAuthCredential

interface AuthDS {

    suspend fun verifyOtp(credential: PhoneAuthCredential) : Results<Boolean>

    suspend fun saveUserDetails(data: HashMap<String, Any>): Results<Boolean>

    suspend fun getUserDetails() : Results<Boolean>

    suspend fun checkUserLogin(): Results<Boolean>
}