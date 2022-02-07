package com.example.tinder.ui.home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tinder.repositories.AuthRepositories
import com.example.tinder.repositories.HomeRepositories

class HomeVMF(private val repositories: HomeRepositories): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeVM(repositories) as T
    }

}