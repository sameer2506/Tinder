package com.example.tinder.network

import java.lang.Exception

sealed class Results<out R> {
    data class Success<out T>(val data: T) : Results<T>()
    data class Error(val exception: Exception) : Results<Nothing>()
    object Loading : Results<Nothing>()
}