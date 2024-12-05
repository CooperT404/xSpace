package com.example.xspace;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

    private EditText etName, etCompany, etLocation, etEmail, etPhone, etRole;
    private Button btnSave;

    private ProfileFirebase profileFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

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
                    Intent homeIntent = new Intent(Profile.this, HomePage.class);
                    homeIntent.putExtra("EXTRA_EMAIL", userEmail);
                    startActivity(homeIntent);
                    break;
                case R.id.nav_profile:
                    Intent profileIntent = new Intent(Profile.this, Profile.class);
                    profileIntent.putExtra("EXTRA_EMAIL", userEmail);
                    startActivity(profileIntent);
                    break;
                case R.id.nav_inventory:
                    Intent invIntent = new Intent(Profile.this, Inventory.class);
                    invIntent.putExtra("EXTRA_EMAIL", userEmail);
                    startActivity(invIntent);
                    break;
                case R.id.nav_Orders:
                    Intent ordIntent = new Intent(Profile.this, Orders.class);
                    ordIntent.putExtra("EXTRA_EMAIL", userEmail);
                    startActivity(ordIntent);
                    break;
                case R.id.nav_Map:
                    Intent mapIntent = new Intent(Profile.this, mapTracker.class);
                    mapIntent.putExtra("EXTRA_EMAIL", userEmail);
                    startActivity(mapIntent);
                    break;
                case R.id.nav_Store:
                    Intent storeIntent = new Intent(Profile.this, Store.class);
                    storeIntent.putExtra("EXTRA_EMAIL", userEmail);
                    startActivity(storeIntent);
                    break;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Initialize EditTexts and Button
        etName = findViewById(R.id.Name_Edit);
        etCompany = findViewById(R.id.Com_Edit);
        etLocation = findViewById(R.id.Location_Edit);
        etEmail = findViewById(R.id.Email_Edit);
        etPhone = findViewById(R.id.Phone_Edit);
        etRole = findViewById(R.id.Role_Edit);
        btnSave = findViewById(R.id.Save);

        // Initialize ProfileFirebase
        profileFirebase = new ProfileFirebase();

        // Set Save button click listener
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
    }

    private void saveProfile() {
        String name = etName.getText().toString();
        String company = etCompany.getText().toString();
        String location = etLocation.getText().toString();
        String email = etEmail.getText().toString();
        String phone = etPhone.getText().toString();
        String role = etRole.getText().toString();

        ProfileFirebase profile = new ProfileFirebase(name, company, location, email, phone, role);
        profileFirebase.fetchEmailAndAddOrUpdateProfile(profile); // or use updateProfile method if updating existing profile
    }
}

