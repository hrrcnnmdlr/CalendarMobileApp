package com.example.calendar.reminder

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.calendar.R
import com.example.calendar.activity.ActivityNavigationDrawer
import com.example.calendar.database.Event
import com.example.calendar.database.MainDB
import java.util.*

class EventService : Service() {

    private lateinit var timer: Timer

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val currentTimeMillis = System.currentTimeMillis()
                val events = getEventsFromDatabase()
                for (event in events) {
                    if (event.remind5MinutesBefore && currentTimeMillis >= event.startDateTime - 330000
                        && currentTimeMillis <= event.startDateTime - 270000) {
                        createReminder(event, this@EventService)
                    }
                    if (event.remind15MinutesBefore && currentTimeMillis >= event.startDateTime - 930000
                        && currentTimeMillis <= event.startDateTime - 870000) {
                        createReminder(event, this@EventService)
                    }
                    if (event.remind30MinutesBefore && currentTimeMillis >= event.startDateTime - 1830000
                        && currentTimeMillis <= event.startDateTime - 1770000) {
                        createReminder(event, this@EventService)
                    }
                    if (event.remind1HourBefore && currentTimeMillis >= event.startDateTime - 3630000
                        && currentTimeMillis <= event.startDateTime - 3570000) {
                        createReminder(event, this@EventService)
                    }
                    if (event.remind1DayBefore && currentTimeMillis >= event.startDateTime - 86430000
                        && currentTimeMillis <= event.startDateTime - 86370000) {
                        createReminder(event, this@EventService)
                    }
                    if (event.startDateTime - 30000 <= currentTimeMillis && event.startDateTime + 30000 >= currentTimeMillis) {
                        // Handle event
                        createReminder(event, this@EventService)
                    }
                }
            }
        }, 0, 1000 * 30) // Check every minute
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    private fun getEventsFromDatabase(): List<Event> {
        val db = MainDB.getDatabase(applicationContext)
        val eventDao = db.getDao()
        val events = eventDao.getAllEvents()
        return events.value ?: emptyList()
    }

}

private fun createReminder(event: Event, context: Context) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val notificationIntent = Intent(context, ActivityNavigationDrawer::class.java)
    notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
    val pendingIntent = PendingIntent.getActivity(context, event.id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

    val cal = Calendar.getInstance()
    cal.timeInMillis = event.startDateTime - System.currentTimeMillis()
    val min = (cal.timeInMillis / 60000).toInt()
    val notificationBuilder = NotificationCompat.Builder(context, "EVENT_REMINDER")
        .setContentTitle(event.eventName)
        .setContentText("${event.eventName} starts in $min minutes")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel("EVENT_REMINDER", "Event Reminder", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)
        notificationBuilder.setChannelId(channel.id)
    }
    notificationManager.notify(event.id, notificationBuilder.build())
}