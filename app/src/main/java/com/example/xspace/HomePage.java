package com.example.xspace;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Set;

public class HomePage extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                // Handle navigation view item clicks here.
                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        Intent homeIntent = new Intent(HomePage.this, HomePage.class);
                        startActivity(homeIntent);
                        break;
                    case R.id.nav_profile:
                        Intent profileIntent = new Intent(HomePage.this, Profile.class);
                        startActivity(profileIntent);
                        break;
                    case R.id.nav_inventory:
                        Intent invIntent = new Intent(HomePage.this, Inventory.class);
                        startActivity(invIntent);
                        break;
                    case R.id.nav_Orders:
                        Intent ordIntent = new Intent(HomePage.this, Orders.class);
                        startActivity(ordIntent);
                        break;
                    case R.id.nav_Map:
                        Intent mapIntent = new Intent(HomePage.this, mapTracker.class);
                        startActivity(mapIntent);
                        break;
                    case R.id.nav_Store:
                        Intent storeIntent = new Intent(HomePage.this, Store.class);
                        startActivity(storeIntent);
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // Adding warehouse ID buttons
        LinearLayout buttonContainer = findViewById(R.id.buttonContainer);
        addWarehouseIDButtons(buttonContainer);
    }

    private void addWarehouseIDButtons(LinearLayout buttonContainer) {
        Warehouse.fetchUniqueWareIDs(db, new Warehouse.UniqueWareIDFetchListener() {
            @Override
            public void onFetchSuccess(List<String> uniqueWareIDs) {
                for (String wareID : uniqueWareIDs) {
                    Button button = new Button(HomePage.this);
                    button.setText(wareID);
                    button.setBackgroundResource(R.drawable.bubble_background);
                    button.setOnClickListener(v -> openDocumentPage(wareID));
                    buttonContainer.addView(button);
                }
            }

            @Override
            public void onFetchFailure(Exception e) {
                // Handle fetch failure
            }
        });
    }


    private void openDocumentPage(String wareID) {
        Intent intent = new Intent(this, DocumentPageActivity.class);
        intent.putExtra("WARE_ID", wareID);
        startActivity(intent);
    }
}
