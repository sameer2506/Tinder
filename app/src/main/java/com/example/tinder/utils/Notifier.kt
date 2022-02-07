package com.example.tinder.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.icu.text.CaseMap
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat

object Notifier {

    // Now we will create object for notification while uploading files to the Firebase Storage

    private const val NOTIFICATION_TAG = "OneTime"

    private const val CHANNEL_ID_DEFAULT = "default"
    private var notificationManager: NotificationManager? = null

    data class NotificationData(
        var notificationId: Int? = null,
        var contentTitle: String = "",
        var contentText: String = "",
        var pendingIntent: PendingIntent? = null,
        var isAutoCancelable: Boolean = true,
        @DrawableRes var smallIcon: Int? = null
    )

    /**
     * Shows the notification, or updates a previously shown notification of
     * this type, with the given parameters.
     */
    fun show(
        context: Context,
        init: NotificationData.() -> Unit
    ): NotificationCompat.Builder? {

        val data = NotificationData()
        data.init()

        val notificationId = data.notificationId ?: NOTIFICATION_TAG.hashCode()
        // Remove any notification with the same id.
        this.dismissNotification(context, notificationId)

        createDefaultChannel()
        val builder = notificationBuilder(context, CHANNEL_ID_DEFAULT, data)
            .setAutoCancel(data.isAutoCancelable)

        notify(notificationId, builder.build())

        return builder
    }

    fun progressable(
        context: Context,
        max: Int = 100,
        progress: Int,
        isIndeterminate: Boolean = false,
        init: NotificationData.() -> Unit
    ) {
        if (notificationManager == null) {
            notificationManager = notificationManager(context)
        }

        val data = NotificationData()
        data.init()

        val notificationId = data.notificationId ?: NOTIFICATION_TAG.hashCode()

        createDefaultChannel()
        val builder = notificationBuilder(context, CHANNEL_ID_DEFAULT, data)
            .setProgress(max, progress, isIndeterminate)
            .setOngoing(true)
            .setAutoCancel(false)

        notify(notificationId, builder.build())
    }

    /**
     * Cancels any notifications of this type previously shown.
     */
    fun dismissNotification(context: Context, id: Int) {
        if (notificationManager == null) {
        }
        notificationManager = notificationManager(context)

        notificationManager?.cancel(id)
    }

    private fun notificationManager(context: Context) = context
        .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private fun notify(id: Int, notification: Notification) {
        notificationManager?.notify(id, notification)
    }

    private fun notificationBuilder(
        context: Context,
        channelId: String? = null,
        data: NotificationData
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, channelId ?: CHANNEL_ID_DEFAULT)

            // Set appropriate defaults for the notification light, sound,
            // and vibration.
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentTitle(data.contentTitle)
            .setContentText(data.contentText)
            // All fields below this line are optional.
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setTicker(data.contentTitle)
            .setContentIntent(data.pendingIntent)
    }

    private fun createDefaultChannel() {
        // Notification channel is added since android Oreo.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID_DEFAULT,
                "Default",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager?.createNotificationChannel(channel)
        }
    }


}