package com.example.xspace;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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

    public void validateUser(final String username, final String password, final OnCompleteListener<QuerySnapshot> listener) {
        usersCollection.whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {
                                boolean isPasswordCorrect = false;
                                for (QueryDocumentSnapshot document : querySnapshot) {
                                    String storedPassword = document.getString("password");
                                    if (password.equals(storedPassword)) {
                                        // Password matches, user validated
                                        Log.d("Validation", "User validated successfully");
                                        isPasswordCorrect = true;
                                        break;
                                    }
                                }
                                if (isPasswordCorrect) {
                                    listener.onComplete(task);
                                } else {
                                    // Password does not match
                                    Log.d("Validation", "Invalid password");
                                    Exception e = new Exception("Invalid password");
                                    listener.onComplete(Tasks.forException(e));
                                }
                            } else {
                                // Username not found
                                Log.d("Validation", "Username not found");
                                Exception e = new Exception("Username not found");
                                listener.onComplete(Tasks.forException(e));
                            }
                        } else {
                            // Handle the error
                            Log.e("ValidationError", "Error executing query", task.getException());
                            listener.onComplete(task);
                        }
                    }
                });
    }




    public void getUserIdByEmail(String email, final OnCompleteListener<QuerySnapshot> listener) {
        usersCollection.whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        // Assuming userId is a field in your document
                        String userId = task.getResult().getDocuments().get(0).getString("userId");
                        listener.onComplete(task);  // You can also pass userId back here if needed
                    } else {
                        // Handle case where the document is not found or task failed
                        // For example, you could log an error or call listener with a failure
                    }
                });
    }

    public Task<String> getUserIdByEmail(String email) {
        // Create a TaskCompletionSource to manage the task
        TaskCompletionSource<String> taskCompletionSource = new TaskCompletionSource<>();

        // Query the Firestore collection for a document with the specified email
        usersCollection.whereEqualTo("email", email).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                // Retrieve the userId from the first document found
                String userId = task.getResult().getDocuments().get(0).getString("userId");
                // Set the result to the TaskCompletionSource
                taskCompletionSource.setResult(userId);
            } else {
                // Handle case where the document is not found or task failed
                taskCompletionSource.setException(new Exception("User not found"));
            }
        });

        // Return the Task from the TaskCompletionSource
        return taskCompletionSource.getTask();
    }

}




