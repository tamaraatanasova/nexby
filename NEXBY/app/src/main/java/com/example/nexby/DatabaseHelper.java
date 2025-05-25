package com.example.nexby;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "hexby";
    private static final int DATABASE_VERSION = 2; // Updated version

    // Tables
    private static final String TABLE_USERS = "users";
    private static final String TABLE_CATEGORIES = "categories";
    private static final String TABLE_COMPANIES = "companies";

    // Common keys
    private static final String KEY_ID = "id";

    // Users table keys
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NAME = "name";
    private static final String KEY_RW = "rw"; // password
    private static final String KEY_TYPE = "type"; // user/admin

    // Categories table keys
    private static final String KEY_CATEGORY_NAME = "name";

    // Companies table keys
    private static final String KEY_COMPANY_NAME = "name";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_WEBPAGE = "webpage";
    private static final String KEY_CATEGORY_ID = "category_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DatabaseHelper", "Creating tables...");

        // Users table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " ("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_USERNAME + " TEXT NOT NULL UNIQUE, "
                + KEY_EMAIL + " TEXT NOT NULL UNIQUE, "
                + KEY_NAME + " TEXT, "
                + KEY_RW + " TEXT, "
                + KEY_TYPE + " TEXT DEFAULT 'user')";
        db.execSQL(CREATE_USERS_TABLE);

        // Categories table
        String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + " ("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_CATEGORY_NAME + " TEXT NOT NULL UNIQUE)";
        db.execSQL(CREATE_CATEGORIES_TABLE);

        // Companies table
        String CREATE_COMPANIES_TABLE = "CREATE TABLE " + TABLE_COMPANIES + " ("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_COMPANY_NAME + " TEXT NOT NULL, "
                + KEY_ADDRESS + " TEXT, "
                + KEY_LONGITUDE + " REAL, "
                + KEY_LATITUDE + " REAL, "
                + KEY_EMAIL + " TEXT, "
                + KEY_PHONE + " TEXT, "
                + KEY_WEBPAGE + " TEXT, "
                + KEY_CATEGORY_ID + " INTEGER, "
                + "FOREIGN KEY(" + KEY_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + KEY_ID + "))";
        db.execSQL(CREATE_COMPANIES_TABLE);

        insertDefaultCategories(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DatabaseHelper", "Upgrading database...");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPANIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        onCreate(db);
    }

    private void insertDefaultCategories(SQLiteDatabase db) {
        String[] categories = {
                "Авторсервиси",
                "Аптеки",
                "Болници/Ординации",
                "Градинки",
                "Маркет/Супермаркет",
                "Осигурителни компании",
                "Продавница за чевли",
                "Продавници за облека",
                "Салони за убавина",
                "Теретани",
                "Училишта"
        };
        ContentValues values = new ContentValues();
        for (String category : categories) {
            values.clear();
            values.put(KEY_CATEGORY_NAME, category);
            db.insert(TABLE_CATEGORIES, null, values);
        }
    }
    public String getUserRoleById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String role = "user"; // default

        Cursor cursor = db.rawQuery("SELECT " + KEY_TYPE + " FROM " + TABLE_USERS + " WHERE " + KEY_ID + " = ?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            role = cursor.getString(0);
        }
        cursor.close();
        return role;
    }

    // Insert user
    public long insertUser(String username, String email, String name, String rw, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, username);
        values.put(KEY_EMAIL, email);
        values.put(KEY_NAME, name);
        values.put(KEY_RW, rw);
        values.put(KEY_TYPE, type);
        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    // Insert company

    public List<String> getCompaniesByCategoryId(int categoryId) {
        List<String> companies = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT name FROM companies WHERE category_id = ?", new String[]{String.valueOf(categoryId)});

        if (cursor.moveToFirst()) {
            do {
                String companyName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                companies.add(companyName);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return companies;
    }
    public List<Company> getAllCompanies() {
        List<Company> companies = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM companies", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
                double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));
                int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow("category_id"));

                companies.add(new Company(id, name, latitude, longitude, categoryId));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return companies;
    }

    public List<Company> getCompaniesByCategoryId2(int categoryId) {
        List<Company> companies = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM companies WHERE category_id = ?", new String[]{String.valueOf(categoryId)});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
                double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));
                int catId = cursor.getInt(cursor.getColumnIndexOrThrow("category_id"));

                companies.add(new Company(id, name, latitude, longitude, catId));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return companies;
    }

    public ArrayList<Category> getAllCategoryObjects() {
        ArrayList<Category> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Add ORDER BY clause here to sort alphabetically by category name
        Cursor cursor = db.rawQuery("SELECT " + KEY_ID + ", " + KEY_CATEGORY_NAME +
                " FROM " + TABLE_CATEGORIES +
                " ORDER BY " + KEY_CATEGORY_NAME + " ASC", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(KEY_CATEGORY_NAME));
                categories.add(new Category(id, name));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return categories;
    }

    public boolean validateLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, KEY_USERNAME + "=? AND " + KEY_RW + "=?",
                new String[]{username, password}, null, null, null);

        boolean isValid = cursor != null && cursor.getCount() > 0;

        if (cursor != null) cursor.close();
        return isValid;
    }

    public boolean doesDatabaseExist(Context context) {
        try {
            String dbPath = context.getDatabasePath(DATABASE_NAME).getAbsolutePath();
            return new java.io.File(dbPath).exists();
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error checking if database exists", e);
            return false;
        }
    }


    public ArrayList<String> getAllCategories() {
        ArrayList<String> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("categories", new String[]{"name"}, null, null, null, null, "name ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                categories.add(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            }
            cursor.close();
        }

        db.close();
        return categories;
    }
    public String getUserType(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("users", new String[]{"type"}, "username=?", new String[]{username}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String userType = cursor.getString(cursor.getColumnIndexOrThrow("type"));
            cursor.close();
            return userType;
        }
        return "user";
    }

    public long insertCompany(String name, String address, double longitude, double latitude,
                              String email, String phone, String webpage, int categoryId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("address", address);
        values.put("longitude", longitude);
        values.put("latitude", latitude);
        values.put("email", email);
        values.put("phone", phone);
        values.put("webpage", webpage);
        values.put("category_id", categoryId);

        // Insert into the 'companies' table
        return db.insert("companies", null, values);
    }



}
