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
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MarkersList extends AppCompatActivity implements CategoryItemAdapter.Callback {
    ArrayList<Place> placesMkr = new ArrayList<>();
    ArrayList<String> categories = new ArrayList<>();
    ArrayList<Place> placesGral = new ArrayList<>();
    ArrayList<Place> placesReceived = new ArrayList<>();
    ArrayList<Place> placesSqlite = new ArrayList<>();

    RecyclerView recyclerViewMarkers;
    RecyclerView recyclerViewCategory;
    LinearLayoutManager verticalRecycler;
    LinearLayoutManager horizontalRecycler;
    CategoryItemAdapter categoryItemAdapter;
    MarkerItemAdapter markerItemAdapter;
    String categoriaBandera = "";
    String TODO = "Todo";
    String CUSTOM = "Custom";


    int id;
    String nombre;
    String descripcion;
    double latitud;
    double longitud;
    String imagen;
    String thumbnail;
    String categoria;


    int idSQ;
    String nombreSQ, descripcionSQ, imagenSQ, thumbnailSQ, categoriaSQ;
    double latitudSQ, longitudSQ;

    MapasModel mapasModel;
    CacheMapasModel cacheMapasModel;

    public boolean checker = true;
    public int firstSQLite = 10;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Boolean iniciarSincronia = intent.getBooleanExtra("Status", false);

            initialData();

            if (iniciarSincronia) {
                prepareData();
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_markers_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mapasModel = new MapasModel(this);
        cacheMapasModel = new CacheMapasModel(this);
        startService(new Intent(MarkersList.this, WebService.class));
        LocalBroadcastManager.getInstance(MarkersList.this).registerReceiver(
                mMessageReceiver, new IntentFilter("WebService"));



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MarkersList.this, MapsActivity.class);
                intent.putExtra("Markers", placesMkr);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerViewMarkers = (RecyclerView) findViewById(R.id.recyclerViewMarkers);
        recyclerViewCategory = (RecyclerView) findViewById(R.id.recyclerViewCategories);
        verticalRecycler = new LinearLayoutManager(MarkersList.this, LinearLayoutManager.VERTICAL, false);
        horizontalRecycler = new LinearLayoutManager(MarkersList.this, LinearLayoutManager.HORIZONTAL, false);
        setCategoryList();
        setMarkersList();
        initialData();
        prepareData();
        refreshRecycler();

    }


    private void initialData() {
        placesReceived.clear();
        Cursor c = cacheMapasModel.mostrarTodo();
        placesSqlite.clear();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            id = c.getInt(c.getColumnIndex("id"));
            nombre = c.getString(c.getColumnIndex("nombre"));
            descripcion = c.getString(c.getColumnIndex("descripcion"));
            latitud = c.getDouble(c.getColumnIndex("latitud"));
            longitud = c.getDouble(c.getColumnIndex("longitud"));
            imagen = c.getString(c.getColumnIndex("imagen"));
            thumbnail = c.getString(c.getColumnIndex("thumbnail"));
            categoria = c.getString(c.getColumnIndex("categoria"));

            placesReceived.add(new Place(id, nombre, descripcion, latitud, longitud, imagen, thumbnail, categoria));
            c.moveToNext();
        }
    }


    private void prepareData() {
        placesGral.clear();
        placesGral.addAll(placesReceived);
        generateSQLiteData();


        for (Place tmp : placesGral) {
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
        for (Place tmp : placesGral) {
            categories.add(tmp.getCategoria());
        }
        Set<String> unique = new HashSet<>();
        unique.clear();
        unique.addAll(categories);
        categories.clear();
        categories.add(TODO);
        categories.addAll(unique);
        unique.clear();

        for (Place tmp2 : placesGral) {
            if (tmp2.getCategoria().equals(categoriaBandera)) {
                placesMkr.add(tmp2);
            }
            if (categoriaBandera.equals(TODO)) {
                placesMkr.add(tmp2);
            }
        }
        recyclerViewMarkers.invalidate();
        recyclerViewCategory.invalidate();
    }


    private void setCategoryList() {
        recyclerViewCategory.setLayoutManager(horizontalRecycler);
        categoryItemAdapter = new CategoryItemAdapter(placesMkr, this, categories);
        recyclerViewCategory.setAdapter(categoryItemAdapter);
        categoryItemAdapter.setCallback(this);

    }


    private void setMarkersList() {
        recyclerViewMarkers.setLayoutManager(verticalRecycler);
        markerItemAdapter = new MarkerItemAdapter(placesMkr, this);
        recyclerViewMarkers.setAdapter(markerItemAdapter);

    }

    @Override
    public void onButtonClicked(String categoria) {
        if (!categoriaBandera.equals(categoria)) {
            categoriaBandera = categoria;
            refreshRecycler();
        }
    }


    private void generateSQLiteData() {
        Cursor c = mapasModel.mostrarTodo();
        placesSqlite.clear();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            idSQ = c.getInt(c.getColumnIndex("id"));
            nombreSQ = c.getString(c.getColumnIndex("nombre"));
            descripcionSQ = c.getString(c.getColumnIndex("descripcion"));
            latitudSQ = c.getDouble(c.getColumnIndex("latitud"));
            longitudSQ = c.getDouble(c.getColumnIndex("longitud"));
            imagenSQ = c.getString(c.getColumnIndex("imagen"));
            thumbnailSQ = c.getString(c.getColumnIndex("thumbnail"));
            categoriaSQ = c.getString(c.getColumnIndex("categoria"));

            placesSqlite.add(new Place(idSQ, nombreSQ, descripcionSQ, latitudSQ, longitudSQ, imagenSQ, thumbnailSQ, categoriaSQ));
            c.moveToNext();
        }

        placesGral.addAll(placesSqlite);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void search(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                markerItemAdapter.getFilter().filter(newText);
                return true;
            }


        });
    }

}
