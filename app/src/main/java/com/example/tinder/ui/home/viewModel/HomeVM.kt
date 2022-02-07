package com.example.tinder.ui.home.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tinder.network.Results
import com.example.tinder.repositories.AuthRepositories
import com.example.tinder.repositories.HomeRepositories
import com.google.firebase.auth.PhoneAuthCredential
import kotlinx.coroutines.launch

class HomeVM(
    private val repository: HomeRepositories
) : ViewModel() {



}