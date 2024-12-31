package com.example.myrecipeapp.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.myrecipeapp.MainActivity
import com.example.myrecipeapp.R
//import com.example.myrecipeapp.receiver.SnoozeReceiver

// Notification ID.
private var NOTIFICATION_ID = 0
private const val REQUEST_CODE = 0
private const val FLAGS = 0

/**
 * Builds and delivers the notification.
 *
 * @param messageBody The message to display in the notification.
 * @param applicationContext The application context.
 */
fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {
    // Create the content intent for the notification, which launches this activity
    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        REQUEST_CODE,
        contentIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    //customizing the notification

    //adding the picture
    val recipeImage = BitmapFactory.decodeResource(
        applicationContext.resources,
        R.drawable.food
    )

    //creating the style
    val bigPicStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(recipeImage)
        .bigLargeIcon(null as Bitmap?)

    // Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    )
        .setSmallIcon(R.drawable.food) // Ensure this drawable exists
        .setContentTitle(applicationContext.getString(R.string.notification_title)) // Define title in strings.xml
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true) // Dismiss notification on tap
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setStyle(bigPicStyle)

    // Deliver the notification
    notify(NOTIFICATION_ID, builder.build())
}


