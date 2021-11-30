package com.example.betaversion;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

class FB_Ref {
    public static FirebaseAuth mAuth=FirebaseAuth.getInstance();
    public static FirebaseDatabase FBDB = FirebaseDatabase.getInstance();

    public static FirebaseUser currentUser = mAuth.getCurrentUser();

    public static DatabaseReference refUsers=FBDB.getReference("Users");
    public static DatabaseReference refLists=FBDB.getReference("Lists");
    public static DatabaseReference refTasksDays=FBDB.getReference("Tasks Days");
}
