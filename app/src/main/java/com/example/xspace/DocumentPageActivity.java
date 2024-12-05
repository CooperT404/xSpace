package com.example.xspace;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class DocumentPageActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.documentpageactivity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        String userEmail = getIntent().getStringExtra("EXTRA_EMAIL");

        navView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    Intent homeIntent = new Intent(DocumentPageActivity.this, HomePage.class);
                    Log.d("Login", "Starting HomePage Activity with email: " + userEmail);
                    homeIntent.putExtra("EXTRA_EMAIL", userEmail);
                    startActivity(homeIntent);
                    break;
                case R.id.nav_profile:
                    Intent profileIntent = new Intent(DocumentPageActivity.this, Profile.class);
                    profileIntent.putExtra("EXTRA_EMAIL", userEmail);
                    startActivity(profileIntent);
                    break;
                case R.id.nav_inventory:
                    Intent invIntent = new Intent(DocumentPageActivity.this, Inventory.class);
                    invIntent.putExtra("EXTRA_EMAIL", userEmail);
                    startActivity(invIntent);
                    break;
                case R.id.nav_Orders:
                    Intent ordIntent = new Intent(DocumentPageActivity.this, Orders.class);
                    ordIntent.putExtra("EXTRA_EMAIL", userEmail);
                    startActivity(ordIntent);
                    break;
                case R.id.nav_Map:
                    Intent mapIntent = new Intent(DocumentPageActivity.this, mapTracker.class);
                    mapIntent.putExtra("EXTRA_EMAIL", userEmail);
                    startActivity(mapIntent);
                    break;
                case R.id.nav_Store:
                    Intent storeIntent = new Intent(DocumentPageActivity.this, Store.class);
                    storeIntent.putExtra("EXTRA_EMAIL", userEmail);
                    startActivity(storeIntent);
                    break;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        String wareID = getIntent().getStringExtra("WARE_ID");

        if (wareID != null) {
            fetchDocumentsWithWareID(wareID);
        }
    }

    private void fetchDocumentsWithWareID(String wareID) {
        // Log the current wareID
        Log.d("DocumentPageActivity", "Current wareID: " + wareID);

        db.collection("Warehouse")
                .whereEqualTo("wareID", wareID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        LinearLayout inColumn = findViewById(R.id.inColumn);
                        LinearLayout outColumn = findViewById(R.id.outColumn);

                        List<QueryDocumentSnapshot> documents = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Boolean isIn = document.getBoolean("in");
                            String name = document.getString("name");
                            long units = document.getLong("units");
                            double pricePerUnit = document.getDouble("pricePerUnit");
                            double total = units * pricePerUnit;

                            Button documentButton = new Button(DocumentPageActivity.this);
                            documentButton.setText(name + " - Units: " + units);
                            documentButton.setBackgroundResource(R.drawable.bubble_background);
                            documentButton.setOnClickListener(v -> showDocumentDetails(document.getId()));

                            if (isIn != null && isIn) {
                                inColumn.addView(documentButton);
                            } else {
                                outColumn.addView(documentButton);
                            }
                        }
                    } else {
                        // Handle fetch failure
                        Log.e("DocumentPageActivity", "Error fetching documents: ", task.getException());
                    }
                });
    }

    private void showDocumentDetails(String documentID) {
        db.collection("Warehouse").document(documentID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                String name = document.getString("name");
                long units = document.getLong("units");
                double pricePerUnit = document.getDouble("pricePerUnit");
                double total = units * pricePerUnit;

                // Create an alert dialog to show the details
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Document Details");
                builder.setMessage("Name: " + name + "\n" +
                        "Units: " + units + "\n" +
                        "Price per Unit: $" + pricePerUnit + "\n" +
                        "Total: $" + total);
                builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                Toast.makeText(this, "Failed to fetch document details", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

