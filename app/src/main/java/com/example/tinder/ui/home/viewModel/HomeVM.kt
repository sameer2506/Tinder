package com.example.tinder.ui.home.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tinder.model.MessageData
import com.example.tinder.model.ReceiverListData
import com.example.tinder.model.UserProfile
import com.example.tinder.network.Results
import com.example.tinder.repositories.AuthRepositories
import com.example.tinder.repositories.HomeRepositories
import com.google.firebase.auth.PhoneAuthCredential
import kotlinx.coroutines.launch

class HomeVM(
    private val repository: HomeRepositories
) : ViewModel() {

    var messageList = MutableLiveData<ArrayList<MessageData>>()

    private val _listOfAllUser: MutableLiveData<Results<ArrayList<UserProfile>>> = MutableLiveData()
    val listOfAllUser: LiveData<Results<ArrayList<UserProfile>>>
        get() = _listOfAllUser

    fun listOfAllUser() = viewModelScope.launch {
        _listOfAllUser.value = Results.Loading
        _listOfAllUser.value = repository.getListOfAllUsers()
    }

    private val _sentSenderMessage: MutableLiveData<Results<Boolean>> = MutableLiveData()

    fun sentSenderMessage(senderId: String, receiverId: String, data: HashMap<String, Any>) =
        viewModelScope.launch {
            _sentSenderMessage.value = Results.Loading
            _sentSenderMessage.value = repository.sentSenderMessage(senderId, receiverId, data)
        }

    private val _sentReceiverMessage: MutableLiveData<Results<Boolean>> = MutableLiveData()

    fun sentReceiverMessage(senderId: String, receiverId: String, data: HashMap<String, Any>) =
        viewModelScope.launch {
            _sentReceiverMessage.value = Results.Loading
            _sentReceiverMessage.value = repository.sentReceiverMessage(senderId, receiverId, data)
        }

    fun messageId(data: HashMap<String, Any>) = viewModelScope.launch {
        repository.messageId(data)
    }

    private val _getReceiverList: MutableLiveData<Results<ArrayList<ReceiverListData>>> =
        MutableLiveData()
    val receiverList: LiveData<Results<ArrayList<ReceiverListData>>>
        get() = _getReceiverList

    fun getReceiverList() = viewModelScope.launch {
        _getReceiverList.value = Results.Loading
        _getReceiverList.value = repository.getReceiverList()
    }

    fun fetchMessageList(senderId: String, receiverId: String) = viewModelScope.launch {
        repository.fetchMessageList(messageList, senderId, receiverId)
    }


}