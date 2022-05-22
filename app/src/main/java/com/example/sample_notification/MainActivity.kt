package com.example.sample_notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    companion object {
        const val CHANNEL_ID = "sample_channel"
    }

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private val notificationManager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()
        setupStatus()

        requestPermissionLauncher =
            registerForActivityResult(RequestPermission()) {
                setupStatus()
            }

        findViewById<Button>(R.id.show_notification_button).setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        findViewById<Button>(R.id.post_sample_notification_button).setOnClickListener {
            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Sample Notification")
                .setContentText("Hello World")
            with(NotificationManagerCompat.from(this)) {
                notify(1, builder.build())
            }
        }

        findViewById<Button>(R.id.open_setting_button).setOnClickListener {
            val uriString = "package:$packageName"
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse(uriString))
            startActivity(intent)
        }

        findViewById<Button>(R.id.open_notification_setting_button).setOnClickListener {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra("android.provider.extra.APP_PACKAGE", BuildConfig.APPLICATION_ID)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        setupStatus()
    }

    private fun setupStatus() {
        findViewById<TextView>(R.id.status_text_view).text =
            if (notificationManager.areNotificationsEnabled()) "enable" else "disable"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "notification channel name",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = "description"
        }
        notificationManager.createNotificationChannel(channel)
    }
}