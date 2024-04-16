package com.example.assignment4.entity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseSingleton {

    private static final String DATABASE_URL = "https://rob-ca2-default-rtdb.europe-west1.firebasedatabase.app/";
    private static FirebaseDatabase firebaseDatabaseInstance;

    private FirebaseSingleton() {
        // Private constructor to prevent instantiation
    }

    public static DatabaseReference getDatabaseReference() {
        return getInstance().getReference();
    }

    public static FirebaseDatabase getInstance() {
        if (firebaseDatabaseInstance == null) {
            synchronized (FirebaseSingleton.class) {
                if (firebaseDatabaseInstance == null) {
                    firebaseDatabaseInstance = FirebaseDatabase.getInstance(DATABASE_URL);
                }
            }
        }
        return firebaseDatabaseInstance;
    }
}
