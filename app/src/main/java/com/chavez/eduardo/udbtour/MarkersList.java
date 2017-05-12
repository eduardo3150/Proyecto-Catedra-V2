package com.chavez.eduardo.udbtour;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MarkersList extends AppCompatActivity {
    ArrayList<Place> placesMkr = new ArrayList<>();
    ArrayList<String> categories = new ArrayList<>();
    RecyclerView recyclerViewMarkers;
    RecyclerView recyclerViewCategory;
    LinearLayoutManager verticalRecycler;
    LinearLayoutManager horizontalRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_markers_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        recyclerViewMarkers = (RecyclerView) findViewById(R.id.recyclerViewMarkers);
        recyclerViewCategory = (RecyclerView) findViewById(R.id.recyclerViewCategories);
        verticalRecycler = new LinearLayoutManager(MarkersList.this,LinearLayoutManager.VERTICAL,false);
        horizontalRecycler = new LinearLayoutManager(MarkersList.this,LinearLayoutManager.HORIZONTAL,false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareData();
        setCategoryList();
        setMarkersList();

    }

    private void prepareData() {
        placesMkr.add(new Place(1, "Estadio Felix Charlaix", "Estadio polideportivo", -88.183087, 13.475367, "http://cdn-static.denofgeek.com/sites/denofgeek/files/2017/01/red-dead-redemption-2-ps4_0.jpg", "http://cdn-static.denofgeek.com/sites/denofgeek/files/2017/01/red-dead-redemption-2-ps4_0.jpg", "Centro de deportes"));
        placesMkr.add(new Place(2, "Item 2", " polideportivo", -88.183087, 13.475367, "http://cdn-static.denofgeek.com/sites/denofgeek/files/2017/01/red-dead-redemption-2-ps4_0.jpg", "http://cdn-static.denofgeek.com/sites/denofgeek/files/2017/01/red-dead-redemption-2-ps4_0.jpg", "Centro de deportes"));

        categories.add("Centro de deportes");
        categories.add("Centro de deportes");
        categories.add("Playa");
        Set<String> unique = new HashSet<>();
        unique.addAll(categories);
        categories.clear();
        categories.addAll(unique);
    }


    private void setCategoryList() {
        recyclerViewCategory.setLayoutManager(horizontalRecycler);
        recyclerViewCategory.setAdapter(new CategoryItemAdapter(placesMkr,this,categories));
    }


    private void setMarkersList() {

        recyclerViewMarkers.setLayoutManager(verticalRecycler);
        recyclerViewMarkers.setAdapter(new MarkerItemAdapter(placesMkr, this));

    }
}
