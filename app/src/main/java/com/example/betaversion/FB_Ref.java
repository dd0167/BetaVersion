package com.example.betaversion;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

class FB_Ref {
    public static FirebaseAuth mAuth=FirebaseAuth.getInstance();

    public static FirebaseUser currentUser = mAuth.getCurrentUser();
}
