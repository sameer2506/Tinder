package com.example.tinder.network.dataSource

import com.example.tinder.model.ReceiverListData
import com.example.tinder.model.UserProfile
import com.example.tinder.network.Results

interface HomeDS {

    suspend fun getListOfAllUsers(): Results<ArrayList<UserProfile>>

    suspend fun sentSenderMessage(
        senderId: String,
        receiverId: String,
        data: HashMap<String, Any>
    ): Results<Boolean>

    suspend fun sentReceiverMessage(
        senderId: String,
        receiverId: String,
        data: HashMap<String, Any>
    ): Results<Boolean>

    suspend fun messageId(
        data: HashMap<String, Any>
    ): Results<Boolean>

    suspend fun getReceiverList(): Results<ArrayList<ReceiverListData>>


}