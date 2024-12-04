package com.example.xspace;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ProfileFirebase {

    private String name;
    private String company;
    private String location;
    private String email;
    private String phone;
    private String role;

    public ProfileFirebase() {
        // Default constructor required for calls to DataSnapshot.getValue(Profile.class)
    }

    public ProfileFirebase(String name, String company, String location, String email, String phone, String role) {
        this.name = name;
        this.company = company;
        this.location = location;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    // Getters and setters for each field
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // Method to fetch email from Warehouse and then add or update profile
    public void fetchEmailAndAddOrUpdateProfile(ProfileFirebase profile) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Warehouse")
                .document("warehouseDocumentId")  // Replace with actual document ID or query method
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String warehouseEmail = document.getString("email");
                            profile.setEmail(warehouseEmail);
                            addOrUpdateProfile(profile);
                        } else {
                            System.err.println("No such document in Warehouse");
                        }
                    } else {
                        System.err.println("Failed to fetch email from Warehouse: " + task.getException());
                    }
                });
    }

    // Add or update profile in Firestore
    public void addOrUpdateProfile(ProfileFirebase profile) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Profile")
                .whereEqualTo("email", profile.getEmail())  // Assuming email is unique
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Profile exists, update it
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("Profile").document(document.getId())
                                    .set(profile)
                                    .addOnSuccessListener(aVoid -> System.out.println("Profile updated successfully"))
                                    .addOnFailureListener(e -> System.err.println("Error updating profile: " + e.getMessage()));
                        }
                    } else {
                        // Profile does not exist, add it
                        db.collection("Profile")
                                .add(profile)
                                .addOnSuccessListener(documentReference -> System.out.println("Profile added with ID: " + documentReference.getId()))
                                .addOnFailureListener(e -> System.err.println("Error adding profile: " + e.getMessage()));
                    }
                })
                .addOnFailureListener(e -> System.err.println("Error querying profiles: " + e.getMessage()));
    }
}



