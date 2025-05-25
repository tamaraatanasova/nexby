package com.example.nexby;
public class Company {
    private int id;
    private String name;
    private double latitude;
    private double longitude;
    private int categoryId;

    public Company(int id, String name, double latitude, double longitude, int categoryId) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.categoryId = categoryId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getCategoryId() {
        return categoryId;
    }
    private float distance;  // Add this field

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getDistance() {
        return distance;
    }


    // You can add setters too if needed
}

