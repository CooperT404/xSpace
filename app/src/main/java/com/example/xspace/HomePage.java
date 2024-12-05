package com.example.xspace;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class HomePage extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private LoginDB LDB;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String email = intent.getStringExtra("EXTRA_EMAIL");

        setContentView(R.layout.homepage);

        LDB = new LoginDB(this);

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
                    Intent homeIntent = new Intent(HomePage.this, HomePage.class);
                    homeIntent.putExtra("EXTRA_EMAIL", userEmail);
                    Log.d("Login", "Starting HomePage Activity with email: " + userEmail);
                    startActivity(homeIntent);
                    break;
                case R.id.nav_profile:
                    Intent profileIntent = new Intent(HomePage.this, Profile.class);
                    profileIntent.putExtra("EXTRA_EMAIL", userEmail);
                    startActivity(profileIntent);
                    break;
                case R.id.nav_inventory:
                    Intent invIntent = new Intent(HomePage.this, Inventory.class);
                    invIntent.putExtra("EXTRA_EMAIL", userEmail);
                    startActivity(invIntent);
                    break;
                case R.id.nav_Orders:
                    Intent ordIntent = new Intent(HomePage.this, Orders.class);
                    ordIntent.putExtra("EXTRA_EMAIL", userEmail);
                    startActivity(ordIntent);
                    break;
                case R.id.nav_Map:
                    Intent mapIntent = new Intent(HomePage.this, mapTracker.class);
                    mapIntent.putExtra("EXTRA_EMAIL", userEmail);
                    startActivity(mapIntent);
                    break;
                case R.id.nav_Store:
                    Intent storeIntent = new Intent(HomePage.this, Store.class);
                    storeIntent.putExtra("EXTRA_EMAIL", userEmail);
                    startActivity(storeIntent);
                    break;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Adding warehouse ID buttons
        LinearLayout buttonContainer = findViewById(R.id.buttonContainer);
        addWarehouseIDButtons(buttonContainer, userEmail);
    }

    private void addWarehouseIDButtons(LinearLayout buttonContainer, String email) {
        LDB.getUserIdByEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String userId = task.getResult();
                // Log the userId
                Log.d("UserFetchSuccess", "Fetched userId: " + userId);
                // Fetch warehouse IDs by userId
                Warehouse.fetchWareIDsByUserId(db, userId, new Warehouse.UniqueWareIDFetchListener() {
                    @Override
                    public void onFetchSuccess(List<String> wareIDs) {
                        for (String warehouseID : wareIDs) {
                            Log.d("FetchSuccess", "WarehouseID found: " + warehouseID);
                            // Add button for each warehouseID
                            runOnUiThread(() -> addButton(buttonContainer, warehouseID));
                        }
                    }

                    @Override
                    public void onFetchFailure(Exception e) {
                        // Handle error fetching warehouse IDs
                        Log.e("UserFetchError", "Error fetching warehouse IDs", e);
                    }
                });
            } else {
                // Handle error fetching userId
                Log.e("UserFetchError", "Error fetching userId", task.getException());
            }
        });
    }


    private void addButton(LinearLayout buttonContainer, String wareID) {
        Button button = new Button(HomePage.this);
        button.setText(wareID);
        button.setBackgroundResource(R.drawable.bubble_background);
        button.setOnClickListener(v -> openDocumentPage(wareID));
        buttonContainer.addView(button);
        Log.d("ButtonAdded", "Button added for wareID: " + wareID);
    }


    private void openDocumentPage(String wareID) {
        Intent intent = new Intent(this, DocumentPageActivity.class);
        String userEmail = getIntent().getStringExtra("EXTRA_EMAIL");
        intent.putExtra("WARE_ID", wareID);
        intent.putExtra("EXTRA_EMAIL", userEmail);
        startActivity(intent);
    }
}
