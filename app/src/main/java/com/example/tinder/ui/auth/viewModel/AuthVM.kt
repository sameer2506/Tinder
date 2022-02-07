package com.example.tinder.ui.auth.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tinder.network.Results
import com.example.tinder.repositories.AuthRepositories
import com.google.firebase.auth.PhoneAuthCredential
import kotlinx.coroutines.launch

class AuthVM(
    private val repository: AuthRepositories
) : ViewModel() {

    private val _verifyOtp: MutableLiveData<Results<Boolean>> = MutableLiveData()
    val verifyOtp: LiveData<Results<Boolean>>
        get() = _verifyOtp

    fun verifyOtp(credential: PhoneAuthCredential) = viewModelScope.launch {
        _verifyOtp.value = Results.Loading
        _verifyOtp.value = repository.verifyOtp(credential)
    }

    private val _saveUserDetails: MutableLiveData<Results<Boolean>> = MutableLiveData()
    val saveUserDetails: LiveData<Results<Boolean>>
        get() = _saveUserDetails

    fun saveUserDetails(data: HashMap<String, Any>) = viewModelScope.launch {
        _saveUserDetails.value = Results.Loading
        _saveUserDetails.value = repository.saveUserDetails(data)
    }

    private val _getUserDetails: MutableLiveData<Results<Boolean>> = MutableLiveData()
    val getUserDetails: LiveData<Results<Boolean>>
        get() = _getUserDetails

    fun getUserDetails() = viewModelScope.launch {
        _getUserDetails.value = Results.Loading
        _getUserDetails.value = repository.getUserDetails()
    }

    private val _checkUserLogin: MutableLiveData<Results<Boolean>> = MutableLiveData()
    val checkUserLogin: LiveData<Results<Boolean>>
        get() = _checkUserLogin

    fun checkUserLogin() = viewModelScope.launch {
        _checkUserLogin.value = Results.Loading
        _checkUserLogin.value = repository.checkUserLogin()
    }



}