package com.chavez.eduardo.udbtour;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
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
    ArrayList<Place> placesSqlite = new ArrayList<>();

    RecyclerView recyclerViewMarkers;
    RecyclerView recyclerViewCategory;
    LinearLayoutManager verticalRecycler;
    LinearLayoutManager horizontalRecycler;
    CategoryItemAdapter categoryItemAdapter;
    String categoriaBandera = "";
    String TODO = "Todo";
    String CUSTOM = "Custom";


    int[] id;
    String[] nombre;
    String[] descripcion;
    double[] latitud;
    double[] longitud;
    String[] imagen;
    String[] thumbnail;
    String[] categoria;


    int idSQ;
    String nombreSQ, descripcionSQ, imagenSQ, thumbnailSQ, categoriaSQ;
    double latitudSQ,longitudSQ;

    MapasModel mapasModel;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
                      String message = intent.getStringExtra("Status");

            id = intent.getIntArrayExtra("id");
            nombre = intent.getStringArrayExtra("nombre");
            descripcion = intent.getStringArrayExtra("descripcion");
            latitud = intent.getDoubleArrayExtra("latitud");
            longitud = intent.getDoubleArrayExtra("longitud");
            imagen = intent.getStringArrayExtra("imagen");
            thumbnail = intent.getStringArrayExtra("thumbnail");
            categoria = intent.getStringArrayExtra("categoria");

            prepareData();

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_markers_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        startService(new Intent(MarkersList.this, WebService.class));
        LocalBroadcastManager.getInstance(MarkersList.this).registerReceiver(
                mMessageReceiver, new IntentFilter("WebService"));


        mapasModel = new MapasModel(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MarkersList.this,MapsActivity.class);
                intent.putExtra("Markers",placesMkr);
                startActivity(intent);
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
        setCategoryList();
        setMarkersList();
        refreshRecycler();
    }


    private void prepareData() {
        if (nombre.length!=0){
        placesGral.clear();
        for (int i = 0; i < id.length; i++){
            placesGral.add(new Place(id[i],nombre[i],descripcion[i], latitud[i],longitud[i], imagen[i], thumbnail[i], categoria[i]));
        }
        }

        generateSQLiteData();

        for (Place tmp : placesGral){
            categories.add(tmp.getCategoria());
        }
        Set<String> unique = new HashSet<>();
        unique.clear();
        unique.addAll(categories);
        categories.clear();
        categories.add(TODO);
        categories.addAll(unique);
        unique.clear();
        categoriaBandera = TODO;

        refreshRecycler();
    }

    private void refreshRecycler() {

        placesMkr.clear();
        categories.clear();
        recyclerViewMarkers.removeAllViews();
        recyclerViewCategory.removeAllViews();
        for (Place tmp : placesGral){
            categories.add(tmp.getCategoria());
        }
        Set<String> unique = new HashSet<>();
        unique.clear();
        unique.addAll(categories);
        categories.clear();
        categories.add(TODO);
        categories.addAll(unique);
        unique.clear();

        for (Place tmp2: placesGral){
            if (tmp2.getCategoria().equals(categoriaBandera)){
                placesMkr.add(tmp2);
            }
            if (categoriaBandera.equals(TODO)){
                placesMkr.add(tmp2);
            }
        }
        recyclerViewMarkers.invalidate();
        recyclerViewCategory.invalidate();
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
        refreshRecycler();
        }
    }

    private void generateSQLiteData(){
        Cursor c = mapasModel.mostrarTodo();
        placesSqlite.clear();
        c.moveToFirst();
        while (!c.isAfterLast()){
            idSQ = c.getInt(c.getColumnIndex("id"));
            nombreSQ = c.getString(c.getColumnIndex("nombre"));
            descripcionSQ = c.getString(c.getColumnIndex("descripcion"));
            latitudSQ = c.getDouble(c.getColumnIndex("latitud"));
            longitudSQ = c.getDouble(c.getColumnIndex("longitud"));
            imagenSQ = c.getString(c.getColumnIndex("imagen"));
            thumbnailSQ = c.getString(c.getColumnIndex("thumbnail"));
            categoriaSQ = c.getString(c.getColumnIndex("categoria"));

            placesSqlite.add(new Place(idSQ,nombreSQ,descripcionSQ,latitudSQ,longitudSQ,imagenSQ,thumbnailSQ,categoriaSQ));
            c.moveToNext();
        }
        placesGral.addAll(placesSqlite);
    }
}
