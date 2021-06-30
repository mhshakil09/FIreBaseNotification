package com.example.firebasenotification.fcm;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.firebasenotification.R;
import com.example.firebasenotification.ui_java.java_profile.JavaProfileActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Objects;

import kotlin.random.Random;

public class JavaMyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "my_channel";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d("JavaFCMservice", "From: ${remoteMessage.from}");
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("JavaFCMservice", "Message data payload: " + remoteMessage.getData());

            Intent intent = new Intent(this, JavaProfileActivity.class);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Random notificationID = new Random() {
                @Override
                public int nextBits(int i) {
                    return 0;
                }
            };

            int id = notificationID.nextInt(100);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(notificationManager);
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(Objects.requireNonNull(remoteMessage.getNotification()).getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setSmallIcon(R.drawable.ic_send)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build();

            notificationManager.notify(id, notification);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {

        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(NotificationManager notificationManager) {
        String channelName = "channelName";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription(" My channel description");
        channel.enableLights(true);
        channel.setLightColor(Color.GREEN);

        notificationManager.createNotificationChannel(channel);
    }
}

