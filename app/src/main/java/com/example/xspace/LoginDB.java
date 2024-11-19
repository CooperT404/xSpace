package com.example.xspace;

import android.content.Context;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;

public class LoginDB  {

    private static final String COLLECTION_NAME = "users";

    private FirebaseFirestore db;
    private CollectionReference usersCollection;



    public LoginDB(Context context) {
        db = FirebaseFirestore.getInstance();
        usersCollection = db.collection(COLLECTION_NAME);
    }

    public void insertData(String username, String password, String email, final OnCompleteListener<DocumentReference> listener) {
        User user = new User(username, password, email);
        usersCollection.add(user).addOnCompleteListener(listener);
    }


    public void checkEmailExists(String email, final OnCompleteListener<QuerySnapshot> listener) {
        usersCollection.whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(listener);
    }


    public void validateUser(String username, String password, final OnCompleteListener<QuerySnapshot> listener) {
        usersCollection.whereEqualTo("username", username)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(listener);
    }

}


