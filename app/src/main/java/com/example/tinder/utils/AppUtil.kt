package com.example.tinder.utils

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.tinder.R
import com.example.tinder.model.ScreenWidthHeight
import com.google.android.material.snackbar.Snackbar

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun log(message: String) {
    Log.d("app_data", message)
}

fun logError(message: String) {
    Log.e("app_data", message)
}


fun View.showSnackbar(
    view: View,
    msg: String,
    length: Int,
    actionMessage: CharSequence?,
    action: (View) -> Unit
) {
    val snackbar = Snackbar.make(view, msg, length)
    if (actionMessage != null) {
        snackbar.setAction(actionMessage) {
            action(this)
        }.show()
    } else {
        snackbar.show()
    }
}


fun Activity.getHWofScreen(): ScreenWidthHeight {
    val displayMetrics = DisplayMetrics()
    this.windowManager.defaultDisplay.getMetrics(displayMetrics)
    val height = displayMetrics.heightPixels
    val width = displayMetrics.widthPixels
    val density: Float = this.resources.displayMetrics.density
    val heightDP: Float = height / density
    var widthDP: Float = (width / density)
    widthDP = (width - widthDP * 0.1).toFloat()
    return ScreenWidthHeight(widthDP.toInt(), heightDP.toInt())
}

fun showUploadFinishedNotification(context: Context, title: String, downloadUrl: Uri?){

    Notifier
        .dismissNotification(context, title.hashCode())

    if (downloadUrl != null) return

    val caption = "Image upload failed..."

    Notifier.show(context){
        notificationId = title.hashCode()
        contentTitle = title
        contentText = caption
    }

}

fun showProgressNotification(context: Context, title: String, caption: String, percent: Int){
    Notifier
        .progressable(
            context,
            100, percent
        ){
            notificationId = title.hashCode()
            contentTitle = title
            contentText = caption
            smallIcon = R.drawable.notif_add_a_photo_blue_24dp
        }
}
