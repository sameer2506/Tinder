package com.example.tinder.repositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.tinder.AppPreferences
import com.example.tinder.model.MessageData
import com.example.tinder.model.ReceiverListData
import com.example.tinder.model.UserProfile
import com.example.tinder.network.Results
import com.example.tinder.network.dataSource.HomeDS
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class HomeRepositories(context: Context): HomeDS {

    private val firebaseFirestore = Firebase.firestore

    private val appPreferences = AppPreferences(context)

    override suspend fun getListOfAllUsers(): Results<ArrayList<UserProfile>> =
        suspendCoroutine { cont ->
            firebaseFirestore
                .collection("users")
                .whereNotEqualTo("id", appPreferences.getId())
                .get()
                .addOnSuccessListener { documents ->
                    try {
                        val list =
                            documents.toObjects(UserProfile::class.java) as ArrayList<UserProfile>
                        cont.resume(Results.Success(list))
                    } catch (e: Exception) {
                        cont.resume(Results.Error(e))
                    }
                }
                .addOnFailureListener {
                    cont.resume(Results.Error(it))
                }
        }

    override suspend fun sentSenderMessage(
        senderId: String,
        receiverId: String,
        data: HashMap<String, Any>
    ): Results<Boolean> =
        suspendCoroutine { cont ->
            firebaseFirestore
                .collection("users/${senderId}/message/chat/${receiverId}")
                .document()
                .set(data)
                .addOnFailureListener {
                    cont.resume(Results.Error(it))
                }
                .addOnSuccessListener {
                    cont.resume(Results.Success(true))
                }
        }

    override suspend fun sentReceiverMessage(
        senderId: String,
        receiverId: String,
        data: HashMap<String, Any>
    ): Results<Boolean> =
        suspendCoroutine { cont ->
            firebaseFirestore
                .collection("users/${receiverId}/message/chat/${senderId}")
                .document()
                .set(data)
                .addOnSuccessListener {
                    cont.resume(Results.Success(true))
                }
                .addOnFailureListener {
                    cont.resume(Results.Error(it))
                }
        }

    override suspend fun messageId(data: HashMap<String, Any>): Results<Boolean> =
        suspendCoroutine { cont ->
            firebaseFirestore
                .collection("message")
                .document()
                .set(data)
                .addOnFailureListener {
                    cont.resume(Results.Error(it))
                }
                .addOnSuccessListener {
                    cont.resume(Results.Success(true))
                }
        }

    override suspend fun getReceiverList(): Results<ArrayList<ReceiverListData>> =
        suspendCoroutine { cont ->
            firebaseFirestore
                .collection("message")
                .whereEqualTo("id", appPreferences.getId())
                .get()
                .addOnFailureListener {
                    cont.resume(Results.Error(it))
                }
                .addOnSuccessListener {
                    try {
                        val data =
                            it.toObjects(ReceiverListData::class.java) as ArrayList<ReceiverListData>
                        cont.resume(Results.Success(data))
                    } catch (e: Exception) {
                        cont.resume(Results.Error(e))
                    }
                }
        }

    suspend fun fetchMessageList(
        messageList: MutableLiveData<ArrayList<MessageData>>,
        senderId: String,
        receiverId: String
    ) {
        val list = ArrayList<MessageData>()
        withContext(Dispatchers.IO) {
            firebaseFirestore
                .collection("users/${senderId}/message/chat/${receiverId}")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.i("listener", "Listen Failed ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        list.clear()
                        val docs = snapshot.documents
                        docs.forEach {
                            val item = MessageData(
                                it["message"].toString(),
                                it["status"].toString()
                            )
                            list.add(item)
                        }
                        messageList.value = list
                    }
                }
        }
    }


}