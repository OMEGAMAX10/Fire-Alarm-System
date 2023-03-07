package com.mta.firealarm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

public class FireAlarmFirebaseMessagingService extends FirebaseMessagingService {
    LocationManager locationManager;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        float lat_fire = remoteMessage.getData().get("lat") != null ? Float.parseFloat(Objects.requireNonNull(remoteMessage.getData().get("lat"))) : 0;
        float lng_fire = remoteMessage.getData().get("lng") != null ? Float.parseFloat(Objects.requireNonNull(remoteMessage.getData().get("lng"))) : 0;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getBaseContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        if(remoteMessage.getNotification() != null) {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location location_network = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                float lat_phone = (float) location.getLatitude();
                float lng_phone = (float) location.getLongitude();
                float[] distance = new float[1];
                Location.distanceBetween(lat_phone, lng_phone, lat_fire, lng_fire, distance);
                // If the distance is less than 2km, send a notification
                if (distance[0] < 2000) {
                    Log.d("DISTANCE", "Distance to fire is: " + distance[0] / 1000 + "km");
                    Log.d("INFO", "Message Notification Body: " + remoteMessage.getNotification().getBody());
                    getFireBaseMessage(Objects.requireNonNull(remoteMessage.getNotification()).getTitle(), remoteMessage.getNotification().getBody());
                }
                else {
                    Log.w("DISTANCE", "Distance to fire is: " + distance[0] / 1000 + "km");
                }
            } else if (location_network != null) {
                float lat_phone = (float) location_network.getLatitude();
                float lng_phone = (float) location_network.getLongitude();
                float[] distance = new float[1];
                Location.distanceBetween(lat_phone, lng_phone, lat_fire, lng_fire, distance);
                // If the distance is less than 2km, send a notification
                if (distance[0] < 2000) {
                    Log.d("DISTANCE", "Distance to fire is: " + distance[0] / 1000 + "km");
                    Log.d("INFO", "Message Notification Body: " + remoteMessage.getNotification().getBody());
                    getFireBaseMessage(Objects.requireNonNull(remoteMessage.getNotification()).getTitle(), remoteMessage.getNotification().getBody());
                }
                else {
                    Log.w("DISTANCE", "Distance to fire is: " + distance[0] / 1000 + "km");
                }
            } else {
                Log.d("INFO", "Message Notification Body: " + remoteMessage.getNotification().getBody());
                getFireBaseMessage(Objects.requireNonNull(remoteMessage.getNotification()).getTitle(), remoteMessage.getNotification().getBody());
            }
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    public void getFireBaseMessage(String title, String message) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        String CHANNEL_ID = "fcm_default_channel";
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.baseline_local_fire_department_24)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentText(message)
                .setContentIntent(pendingIntent);
        @SuppressLint("ServiceCast") NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, getResources().getString(R.string.default_notification_channel_name), NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) getBaseContext(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
                return;
            }
        }
        manager.notify(0, builder.build());
    }
}
