package com.example.xspace;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;


public class Orders extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private FirebaseFirestore db;
    private LinearLayout buttonContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orders);

        buttonContainer = findViewById(R.id.buttonContainer);
        db = FirebaseFirestore.getInstance();



        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(v -> showAddItemDialog());


        fetchAndCreateButtons(true);



//--------------------------------------------------------------------------------------------------------------Nav Drawer
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
                    Intent homeIntent = new Intent(Orders.this, HomePage.class);
                    homeIntent.putExtra("EXTRA_EMAIL", userEmail);
                    startActivity(homeIntent);
                    break;
                case R.id.nav_profile:
                    Intent profileIntent = new Intent(Orders.this, Profile.class);
                    profileIntent.putExtra("EXTRA_EMAIL", userEmail);
                    startActivity(profileIntent);
                    break;
                case R.id.nav_inventory:
                    Intent invIntent = new Intent(Orders.this, Inventory.class);
                    invIntent.putExtra("EXTRA_EMAIL", userEmail);
                    startActivity(invIntent);
                    break;
                case R.id.nav_Orders:
                    Intent ordIntent = new Intent(Orders.this, Orders.class);
                    ordIntent.putExtra("EXTRA_EMAIL", userEmail);
                    startActivity(ordIntent);
                    break;
                case R.id.nav_Map:
                    Intent mapIntent = new Intent(Orders.this, mapTracker.class);
                    mapIntent.putExtra("EXTRA_EMAIL", userEmail);
                    startActivity(mapIntent);
                    break;
                case R.id.nav_Store:
                    Intent storeIntent = new Intent(Orders.this, Store.class);
                    storeIntent.putExtra("EXTRA_EMAIL", userEmail);
                    startActivity(storeIntent);
                    break;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }
    private void fetchAndCreateButtons(boolean isIn) {
        Warehouse.fetchAllDocuments(db, false ,new Warehouse.WarehouseFetchListener() {
            @Override
            public void onFetchSuccess(List<String> documentIDs) {
                createButtons(documentIDs);
            }

            @Override
            public void onFetchFailure(Exception e) {
                Toast.makeText(Orders.this, "Failed to fetch data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createButtons(List<String> documentIDs) {
        LinearLayout buttonContainer = findViewById(R.id.buttonContainer); // Ensure this is the correct ID
        buttonContainer.removeAllViews(); // Clear existing buttons

        for (String id : documentIDs) {
            // Fetch the document from Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Warehouse").document(id).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot document = task.getResult();
                    String name = document.getString("name");

                    // Create the button with the fetched name
                    Button button = new Button(this);
                    button.setBackgroundResource(R.drawable.bubble_background);
                    button.setText(name + " - Units: " + document.getLong("units")); // Use the name and units from the document
                    button.setTag(id); // Set the tag to the document ID
                    button.setOnClickListener(v -> showDocumentDetails(id));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 10, 0, 10);
                    button.setLayoutParams(params);
                    buttonContainer.addView(button);
                } else {
                    // Handle the error
                    Toast.makeText(this, "Failed to fetch document: " + id, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void showDocumentDetails(String documentID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
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




    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Warehouse Item");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText nameInput = new EditText(this);
        nameInput.setHint("Name");
        layout.addView(nameInput);

        final EditText unitsInput = new EditText(this);
        unitsInput.setHint("Units");
        unitsInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(unitsInput);

        final EditText priceInput = new EditText(this);
        priceInput.setHint("Price per Unit");
        priceInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(priceInput);


        final CheckBox outInInput = new CheckBox(this);
        outInInput.setText("In (checked) / Out (unchecked)");
        // Set the CheckBox to unchecked by default
        outInInput.setChecked(false);
        layout.addView(outInInput);


        final EditText wareIDInput = new EditText(this);
        wareIDInput.setHint("Ware ID");
        layout.addView(wareIDInput);

        builder.setView(layout);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = nameInput.getText().toString();
            int units = Integer.parseInt(unitsInput.getText().toString());
            double pricePerUnit = Double.parseDouble(priceInput.getText().toString());
            boolean isIn = outInInput.isChecked();
            int outIn = isIn ? 1 : 0; // Convert boolean to int (1 for In, 0 for Out)
            String wareID = wareIDInput.getText().toString();

            Warehouse warehouse = new Warehouse(name, units, pricePerUnit, outIn, wareID, "WWW");
            Warehouse.insertWarehouse(warehouse, db, new Warehouse.WarehouseInsertListener() {
                @Override
                public void onInsertSuccess(String documentId) {
                    Toast.makeText(Orders.this, "Item added successfully with ID: " + documentId, Toast.LENGTH_SHORT).show();
                    fetchAndCreateButtons(true); // Refresh buttons after adding a new item, fetching "In" documents
                }

                @Override
                public void onInsertFailure(Exception e) {
                    Toast.makeText(Orders.this, "Failed to add item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }



}
