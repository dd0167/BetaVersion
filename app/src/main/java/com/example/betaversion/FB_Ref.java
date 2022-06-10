package com.example.betaversion;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

class FB_Ref {
    public static FirebaseAuth mAuth=FirebaseAuth.getInstance(); // הפניה ל-Firebase Authentication
    public static FirebaseDatabase FBDB = FirebaseDatabase.getInstance(); // הפניה ל-Firebase Realtime Database
    public static FirebaseStorage FBCS = FirebaseStorage.getInstance(); // הפניה ל-Firebase Storage

    public static StorageReference referenceStorage=FBCS.getReference(); // הפניה לשורש ב-Firebase Storage

    public static DatabaseReference refUsers=FBDB.getReference("Users"); // הפניה לשורש ב-Firebase Realtime Database
    public static DatabaseReference refLists=FBDB.getReference("Lists"); // הפניה לשורש ב-Firebase Realtime Database
    public static DatabaseReference refTasksDays=FBDB.getReference("Tasks Days"); // הפניה לשורש ב-Firebase Realtime Database
}
