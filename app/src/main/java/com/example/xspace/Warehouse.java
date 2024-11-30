package com.example.xspace;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class Warehouse {

    private String name;
    private int units;
    private double pricePerUnit;
    private int outIn; // 0 for Out, 1 for In
    private String wareID;

    // Constructors, getters, and setters
    public Warehouse() {}

    public Warehouse(String name, int units, double pricePerUnit, int outIn, String wareID) {
        this.name = name;
        this.units = units;
        this.pricePerUnit = pricePerUnit;
        this.outIn = outIn;
        this.wareID = wareID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public int getOutIn() {
        return outIn;
    }

    public void setOutIn(int outIn) {
        this.outIn = outIn;
    }

    public String getWareID() {
        return wareID;
    }

    public void setWareID(String wareID) {
        this.wareID = wareID;
    }

    // Method to fetch all documents with the same WareID and filter by Out/In status
    public static void fetchAllDocuments(FirebaseFirestore db, WarehouseFetchListener listener) {
        db.collection("Warehouse")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> documentIDs = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            documentIDs.add(document.getId());
                        }
                        listener.onFetchSuccess(documentIDs);
                    } else {
                        listener.onFetchFailure(task.getException());
                    }
                });
    }





    // Listener interface for fetch results
    public interface WarehouseFetchListener {
        void onFetchSuccess(List<String> warehouseIDs);
        void onFetchFailure(Exception e);
    }


    // Determine if item is In (1) or Out (0)
    public boolean isIn() {
        return this.outIn == 1;
    }

    // Method to insert a new Warehouse entry into the database with a randomly assigned ID
    public static void insertWarehouse(Warehouse warehouse, FirebaseFirestore db, WarehouseInsertListener listener) {
        db.collection("Warehouse")
                .add(warehouse)
                .addOnSuccessListener(documentReference -> listener.onInsertSuccess(documentReference.getId()))
                .addOnFailureListener(listener::onInsertFailure);
    }

    // Listener interface for insert results
    public interface WarehouseInsertListener {
        void onInsertSuccess(String documentId);
        void onInsertFailure(Exception e);
    }
}

