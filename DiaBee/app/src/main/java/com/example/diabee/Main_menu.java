package com.example.diabee;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Main_menu extends AppCompatActivity {

    private static final int RC_NOTIFICATION = 99;
    private Switch onOffSwitch;
    private EditText editInterval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel(); // Create notification channel
        setContentView(R.layout.main_menu); // Ensure this matches the name of your XML layout file

        // Find buttons by their layout ids
        findViewById(R.id.buttonCategories).setOnClickListener(this::openCategories);
        findViewById(R.id.buttonFavorites).setOnClickListener(this::openFavorites);

        // Find the switch and EditText by their ids
        onOffSwitch = findViewById(R.id.onOff);
        editInterval = findViewById(R.id.editCas);

        onOffSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // The switch is enabled
                Toast.makeText(Main_menu.this, "Upozornenia ZAPNUTÉ", Toast.LENGTH_SHORT).show();
                startRepeatingAlarm();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, RC_NOTIFICATION);
                }
            } else {
                // The switch is disabled
                Toast.makeText(Main_menu.this, "Upozornenia VYPNUTÉ", Toast.LENGTH_SHORT).show();
                cancelAlarm();
            }
        });
    }
    

    // Method to open Categories activity
    public void openCategories(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // Method to open Favorites activity
    public void openFavorites(View view) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("category", "favorite");
        startActivity(intent);
    }

    // Overriding onRequestPermissionsResult
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == RC_NOTIFICATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                Toast.makeText(this, "POVOLENIE ZAMIETNUTE", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startRepeatingAlarm() {
        Intent intent = new Intent(Main_menu.this, ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(Main_menu.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Retrieve the interval from EditText
        String intervalString = editInterval.getText().toString();
        long intervalMillis;
        try {
            // Parse the input as a float and convert hours to milliseconds
            float intervalHours = Float.parseFloat(intervalString);
            intervalMillis = (long) (intervalHours * 3600000L); // Convert hours to milliseconds
            Toast.makeText(Main_menu.this, "Čas nastavený na " + intervalString + "h", Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            Toast.makeText(Main_menu.this, "Neplatný časový interval", Toast.LENGTH_SHORT).show();
            return;
        }

        long triggerTime = System.currentTimeMillis() + intervalMillis;

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime, intervalMillis, pendingIntent);
        }
    }

    private void cancelAlarm() {
        Intent intent = new Intent(Main_menu.this, ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(Main_menu.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    private void createNotificationChannel() {
        CharSequence name = "VodaReminder";
        String description = "Oznamenie pre napitie sa vody";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("napiSaVody", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
