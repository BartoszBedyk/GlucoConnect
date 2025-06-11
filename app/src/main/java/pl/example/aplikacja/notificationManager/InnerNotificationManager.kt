package pl.example.aplikacja.notificationManager

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getString
import androidx.core.content.FileProvider
import pl.example.aplikacja.R
import java.io.File

class InnerNotificationManager(private val context: Context) {

    @SuppressLint("MissingPermission")
    public fun createDownloadNotification(fileName: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = createChannel()
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            channel?.let {
                manager.createNotificationChannel(it)
            }
        }

        val intent = openDownloadedFile(context, fileName)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(
                context.applicationInfo.icon
            )
            .setContentTitle("Pobieranie raportu")
            .setContentText("Zamówiony raport jest właśnie generowany. Po zakończeniu zostanie on zapisany w folderze Pobrane.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOnlyAlertOnce(true)
            .setContentIntent(intent)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(context)


        notificationManager.notify(1, builder.build())
    }

    private fun createChannel(): NotificationChannel? {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = getString(context, R.string.notification_name_download)
            val descriptionText = getString(context, R.string.notification_description_download)
            val importance = android.app.NotificationManager.IMPORTANCE_HIGH
            val downloadChannel = NotificationChannel(CHANNEL_ID, name, importance)
            downloadChannel.description = descriptionText

            return downloadChannel
        }else
            return null
    }

    fun openDownloadedFile(context: Context, fileName: String): PendingIntent? {
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)

        //if (!file.exists()) return null

        val fileUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(fileUri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }



    companion object {
        const val CHANNEL_ID = "glucose_channel"
    }
}
