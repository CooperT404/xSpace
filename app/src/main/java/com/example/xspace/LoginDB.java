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
import java.util.HashMap;
import java.util.Map;

public class LoginDB  {

    private static final String COLLECTION_NAME = "users";

    private FirebaseFirestore db;
    private CollectionReference usersCollection;

    public LoginDB(Context context) {
        db = FirebaseFirestore.getInstance();
        usersCollection = db.collection(COLLECTION_NAME);
    }

    public void insertData(String username, String password, String email, final OnCompleteListener<Void> listener) {
        // Generate a unique ID for the user
        String userId = usersCollection.document().getId();

        Map<String, Object> user = new HashMap<>();
        user.put("userId", userId);
        user.put("username", username);
        user.put("password", password);
        user.put("email", email);

        usersCollection.document(userId).set(user).addOnCompleteListener(listener);
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

    public void getUserIdByEmail(String email, final OnCompleteListener<QuerySnapshot> listener) {
        usersCollection.whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(listener);
    }

}




