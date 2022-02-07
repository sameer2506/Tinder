package com.example.tinder

import android.app.Application
import com.example.tinder.repositories.AuthRepositories
import com.example.tinder.repositories.HomeRepositories
import com.example.tinder.ui.auth.viewModel.AuthVMF
import com.example.tinder.ui.home.viewModel.HomeVMF
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class MyApplication: Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidXModule(this@MyApplication))

        bind() from singleton { AppPreferences(instance()) }

        bind() from singleton { AuthRepositories(instance()) }
        bind() from provider { AuthVMF(instance()) }

        bind() from singleton { HomeRepositories(instance()) }
        bind() from provider { HomeVMF(instance()) }

    }
}