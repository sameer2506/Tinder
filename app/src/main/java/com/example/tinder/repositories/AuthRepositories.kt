package com.example.tinder.repositories

import android.content.Context
import com.example.tinder.AppPreferences
import com.example.tinder.network.Results
import com.example.tinder.network.dataSource.AuthDS
import com.example.tinder.utils.log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthRepositories(context: Context): AuthDS {

    private val auth: FirebaseAuth = Firebase.auth

    private val firebaseFirestore = Firebase.firestore

    private val appPreferences = AppPreferences(context)

    override suspend fun verifyOtp(credential: PhoneAuthCredential): Results<Boolean> =
        suspendCoroutine { cont ->
            auth
                .signInWithCredential(credential)
                .addOnSuccessListener {
                    //appPreferences.saveId(auth.uid!!)
                    cont.resume(Results.Success(true))
                }
                .addOnFailureListener {
                    cont.resume(Results.Error(it))
                }
        }

    override suspend fun saveUserDetails(data: HashMap<String, Any>): Results<Boolean> =
        suspendCoroutine { cont ->
            firebaseFirestore
                .collection("users")
                .document(appPreferences.getId()!!)
                .set(data)
                .addOnSuccessListener {
                    cont.resume(Results.Success(true))
                }
                .addOnFailureListener {
                    cont.resume(Results.Error(it))
                }
        }

    override suspend fun getUserDetails(): Results<Boolean> =
        suspendCoroutine { cont ->
            firebaseFirestore
                .collection("users")
                .document(appPreferences.getId()!!)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        if (document.data.isNullOrEmpty()) {
                            log("No such data")
                            cont.resume(Results.Success(false))
                        } else {
                            val detail = document.data
                            appPreferences.saveName(detail!!["name"].toString())
                            cont.resume(Results.Success(true))
                        }
                    } else {
                        log("No such document")
                    }
                }
                .addOnFailureListener {
                    cont.resume(Results.Error(it))
                }
        }

    override suspend fun checkUserLogin(): Results<Boolean> =
        suspendCoroutine { cont ->
            if (auth.currentUser != null) {
                cont.resume(Results.Success(true))
            } else {
                cont.resume(Results.Success(false))
            }
        }



}