package com.example.tinder.ui.auth.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tinder.repositories.AuthRepositories

class AuthVMF(private val repositories: AuthRepositories): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AuthVM(repositories) as T
    }

}