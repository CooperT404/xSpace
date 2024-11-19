package com.example.xspace;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

public class Profile extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navView;

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
                        Intent homeIntent = new Intent(Profile.this, HomePage.class);
                        startActivity(homeIntent);
                        break;
                    case R.id.nav_profile:
                        Intent profileIntent = new Intent(Profile.this, Profile.class);
                        startActivity(profileIntent);
                        break;
                    case R.id.nav_inventory:
                        Intent invIntent = new Intent(Profile.this, Inventory.class);
                        startActivity(invIntent);
                        break;

                    case R.id.nav_Orders:
                        Intent ordIntent = new Intent(Profile.this, Orders.class);
                        startActivity(ordIntent);
                        break;
                    case R.id.nav_Map:
                        Intent mapIntent = new Intent(Profile.this, mapTracker.class);
                        startActivity(mapIntent);
                        break;
                    case R.id.nav_Store:
                        Intent storeIntent = new Intent(Profile.this, Store.class);
                        startActivity(storeIntent);
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }
}

