package com.chavez.eduardo.udbtour;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MarkersList extends AppCompatActivity implements CategoryItemAdapter.Callback {
    ArrayList<Place> placesMkr = new ArrayList<>();
    ArrayList<String> categories = new ArrayList<>();
    ArrayList<Place> placesGral = new ArrayList<>();

    RecyclerView recyclerViewMarkers;
    RecyclerView recyclerViewCategory;
    LinearLayoutManager verticalRecycler;
    LinearLayoutManager horizontalRecycler;
    CategoryItemAdapter categoryItemAdapter;
    String categoriaBandera = "";

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
        placesGral.add(new Place(1, "Estadio Felix Charlaix", "Estadio polideportivo", -88.183087, 13.475367, "http://cdn-static.denofgeek.com/sites/denofgeek/files/2017/01/red-dead-redemption-2-ps4_0.jpg", "http://cdn-static.denofgeek.com/sites/denofgeek/files/2017/01/red-dead-redemption-2-ps4_0.jpg", "Centro de deportes"));
        placesGral.add(new Place(2, "Item 2", " polideportivo", -88.183087, 13.475367, "http://cdn-static.denofgeek.com/sites/denofgeek/files/2017/01/red-dead-redemption-2-ps4_0.jpg", "http://cdn-static.denofgeek.com/sites/denofgeek/files/2017/01/red-dead-redemption-2-ps4_0.jpg", "Centro de deportes"));
        placesGral.add(new Place(2, "Item 2", " Playa", -88.183087, 13.475367, "http://assets.vg247.com/current//2016/06/sniper_elite_4-5-600x338.jpg", "http://cdn-static.denofgeek.com/sites/denofgeek/files/2017/01/red-dead-redemption-2-ps4_0.jpg", "Playa"));
        placesGral.add(new Place(2, "Item 2", " Volcan", -88.183087, 13.475367, "http://nerdist.com/wp-content/uploads/2016/12/The-Last-of-Us-Part-II.jpg", "http://cdn-static.denofgeek.com/sites/denofgeek/files/2017/01/red-dead-redemption-2-ps4_0.jpg", "Volcan"));

        for (Place tmp : placesGral){
            categories.add(tmp.getCategoria());
        }
        Set<String> unique = new HashSet<>();
        unique.addAll(categories);
        categories.clear();
        categories.addAll(unique);
        categoriaBandera = placesGral.get(0).getCategoria();

        refreshRecycler();
    }

    private void refreshRecycler() {
        placesMkr.clear();
        recyclerViewMarkers.removeAllViews();
        for (Place tmp2: placesGral){
            if (tmp2.getCategoria().equals(categoriaBandera)){
                placesMkr.add(tmp2);
            }
        }
        recyclerViewMarkers.invalidate();
    }


    private void setCategoryList() {
        recyclerViewCategory.setLayoutManager(horizontalRecycler);
        categoryItemAdapter = new CategoryItemAdapter(placesMkr,this,categories);
        recyclerViewCategory.setAdapter(categoryItemAdapter);
        categoryItemAdapter.setCallback(this);

    }


    private void setMarkersList() {
        recyclerViewMarkers.setLayoutManager(verticalRecycler);
        recyclerViewMarkers.setAdapter(new MarkerItemAdapter(placesMkr, this));

    }

    @Override
    public void onButtonClicked(String categoria) {
        if (!categoriaBandera.equals(categoria)) {
        categoriaBandera = categoria;
        Toast.makeText(this,"Seleccion" + categoriaBandera,Toast.LENGTH_SHORT).show();
        refreshRecycler();
        }
    }
}
