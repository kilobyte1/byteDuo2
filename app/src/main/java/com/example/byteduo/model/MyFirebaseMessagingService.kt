package com.example.byteduo.model

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.byteduo.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "MyFirebaseMessagingService"
        private const val CHANNEL_ID = "order_update"
        const val BASE = "https://fcm.googleapis.com"
        const val SERVER_KEY = "AAAAWb68UxA:APA91bHVYFqfXpRgx671_tT4DRgTdo_zNRMVh_fMGEAvfIJjhg3avp7WBqHMn6fDy9J-j6NmfHAhMjdN4IuPOAff_-DhvUr1higU49cUiMPEHXBc_-f2iU-55b7WZGvVI_mBa48bHqwd"
        const val CONTENT_TYPE = "application/json"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Message data: ${remoteMessage.data}")

        // Extract notification data
        val textTitle = remoteMessage.notification?.title ?: "Default Title"
        val textContent = remoteMessage.notification?.body ?: "Default Content"

        // Display push notification
        showNotification(textTitle, textContent)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
        // Send token to server if needed
    }

    private fun showNotification(textTitle: String, textContent: String) {
        createNotificationChannel()

        // Create a notification builder with a unique notification ID
        val notificationId = System.currentTimeMillis().toInt() // Unique ID based on current time
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.byteduo_coffeee)
            .setContentTitle(textTitle)
            .setContentText(textContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Display the notification with a unique ID
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Your Channel Name",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
