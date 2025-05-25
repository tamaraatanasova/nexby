package com.example.nexby;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.os.Build;

import com.example.nexby.Company;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.nexby.DatabaseHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

public class LocationNotificationService extends Service {
    private static final String TAG = "LocationNotification";
    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        dbHelper = new DatabaseHelper(this);  // Initialize your DB helper
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Request location updates
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(60000)  // Update every minute
                .setFastestInterval(30000);  // Minimum time between updates

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, return or request permission
            return START_STICKY;
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, new com.google.android.gms.location.LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null && !locationResult.getLocations().isEmpty()) {
                    Location location = locationResult.getLastLocation();
                    checkProximityToCompanies(location);
                }
            }
        }, getMainLooper());

        return START_STICKY;
    }

    private void checkProximityToCompanies(Location userLocation) {
        // Example: List of companies' latitudes and longitudes (this should come from your database)
        List<Company> companies = dbHelper.getAllCompanies(); // Replace with your DB call

        for (Company company : companies) {
            float[] results = new float[1];
            Location.distanceBetween(userLocation.getLatitude(), userLocation.getLongitude(),
                    company.getLatitude(), company.getLongitude(), results);

            // If within 50 meters
            if (results[0] <= 50) {
                sendPushNotification("You are near " + company.getName() + "!");
            }
        }
    }

    private void sendPushNotification(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "proximity_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Nearby Company Alert")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Create channel for Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "proximity_channel",
                    "Proximity Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Погледни компании во твоја близина");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
