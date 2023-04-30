package com.example.flashfastfood.Service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.flashfastfood.Admin.MainAdminActivity;
import com.example.flashfastfood.Data.UserProfile;
import com.example.flashfastfood.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MessageService extends FirebaseMessagingService {
    Context context;
    @SuppressLint("NewApi")
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        RemoteMessage.Notification notificationtext = remoteMessage.getNotification();
        String title = notificationtext.getTitle();
        String body = notificationtext.getBody();
        Log.d("FCMmess", "onMessageReceived: "+body);

        NotificationChannel channel = new NotificationChannel("MESSAGE","Message Notification",NotificationManager.IMPORTANCE_DEFAULT);


        Intent intent = new Intent(this, MainAdminActivity.class);
        intent.putExtra("order", "CheckOrder");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this,"MESSAGE")
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat.from(this).notify(new Random().nextInt(),notification.build());
    }
}
