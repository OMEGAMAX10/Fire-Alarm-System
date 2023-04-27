package com.mta.firealarm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    Button buttonLogout;
    TextView textView;
    ListView listView;
    ArrayList<Alert> alertArrayList = new ArrayList<>();
    FirebaseUser user;
    DatabaseReference databaseReference;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        buttonLogout = findViewById(R.id.btn_logout);
        textView = findViewById(R.id.userDetails);
        user = mAuth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
        textView.setText("You are logged in as: " + user.getEmail());
        buttonLogout.setOnClickListener(v -> {
            mAuth.signOut();
            FirebaseMessaging.getInstance().unsubscribeFromTopic("fireAlarm").addOnCompleteListener(task -> {
                String message = "Unsubscribed from fireAlarm";
                if (!task.isSuccessful()) {
                    message = "Failed to unsubscribe from fireAlarm";
                }
                Log.d("INFO", message);
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            });
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.cancelAll();
            }
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });
        askForPermission();
        // check if the user is logged in, if it is, then subscribe to the topic
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w("INFO", "Fetching FCM registration token failed", task.getException());
                return;
            }
            FirebaseMessaging.getInstance().subscribeToTopic("fireAlarm").addOnCompleteListener(task_topic -> {
                String message = "Subscribed to fireAlarm";
                if (!task_topic.isSuccessful()) {
                    message = "Failed to subscribe to fireAlarm";
                }
                Log.d("INFO", message);
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            });
        });
        databaseReference = FirebaseDatabase.getInstance().getReference("fire_alert");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                alertArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Alert alert = new Alert(
                            Objects.requireNonNull(dataSnapshot.child("event").getValue()).toString(),
                            Objects.requireNonNull(dataSnapshot.child("source").getValue()).toString(),
                            Objects.requireNonNull(dataSnapshot.child("timestamp").getValue()).toString(),
                            Objects.requireNonNull(dataSnapshot.child("image_url").getValue()).toString()
                    );
                    // add the alert to the list at the beginning
                    alertArrayList.add(0, alert);
                }
                initializeListView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("INFO", "Failed to read value.", error.toException());
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void askForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }

    private void initializeListView() {
        listView = findViewById(R.id.alertHistory);
        AlertHistoryAdapter adapter = new AlertHistoryAdapter(this, alertArrayList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Alert alert = alertArrayList.get(position);
            // create alert dialog that contains the image of the alert
            ImageView imageView = new ImageView(this);
            Glide.with(this).load(alert.getImageUrl()).placeholder(R.drawable.flame_icon_large).error(android.R.drawable.stat_notify_error).into(imageView);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(alert.getEventType());
            builder.setMessage("Source " + alert.getSource() + " at " + alert.getTimestamp());
            builder.setView(imageView);
            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            builder.create().show();
        });
    }
}