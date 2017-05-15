package com.chavez.eduardo.udbtour;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.koushikdutta.ion.Ion;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    ArrayList<Place> places = new ArrayList<>();
    SeekBar seekBarZoom;
    LatLng defaultLatLng;
    Button currentPos;
    Bitmap custom;
    Double latitudCust, longitudCust;
    String placeHolderCust, imgCust;
    FollowPosition followPosition;
    String CATEGORIA = "Personalizado";
    int zoomSaved;

    MapasModel mapasModel;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        places = (ArrayList<Place>) getIntent().getSerializableExtra("Markers");
        defaultLatLng = new LatLng(places.get(0).getLatitud(),places.get(0).getLongitud());
        seekBarZoom = (SeekBar) findViewById(R.id.seekZoom);
        currentPos = (Button) findViewById(R.id.ubicacionActual);
        seekBarZoom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (followPosition.getLatLng() != null) {
                    chooseMoveCamera(mMap, followPosition.getLatLng(), progress);
                    zoomSaved = progress;
                } else {
                    chooseMoveCamera(mMap, defaultLatLng, progress);
                    zoomSaved = progress;
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        currentPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (followPosition.getLatLng() != null) {
                    chooseMoveCamera(mMap, followPosition.getLatLng(), zoomSaved);
                }
            }
        });
        mapasModel = new MapasModel(this);
        placeHolderCust = "http://i.imgur.com/XVJ4SSM.png";
        imgCust ="https://i.imgur.com/yr95Qr7.png";

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (followPosition != null) {
            followPosition.register(MapsActivity.this);
        }
    }


        @Override
        protected void onPause () {
            if (followPosition != null) {
                followPosition.unRegister(MapsActivity.this);
            }
            super.onPause();
        }

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera. In this case,
         * we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to install
         * it inside the SupportMapFragment. This method will only be triggered once the user has
         * installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady (GoogleMap googleMap){
            mMap = googleMap;
            custom = getBitmapFromURL(imgCust);
            followPosition = new FollowPosition(this.mMap, MapsActivity.this);

            followPosition.register(MapsActivity.this);

            mMap.moveCamera(CameraUpdateFactory.newLatLng(defaultLatLng));
            chooseMoveCamera(mMap, defaultLatLng, 10);

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    // Creating a marker
                    MarkerOptions markerOptions = new MarkerOptions();
                    // Setting the position for the marker
                    markerOptions.position(latLng);

                    // Setting the title for the marker.
                    // This will be displayed on taping the marker
                    markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(custom));
                    latitudCust = latLng.latitude;
                    longitudCust = latLng.longitude;
                    // Clears the previously touched position
                    mMap.clear();
                    // Animating to the touched position
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    // Placing a marker on the touched position
                    //mMap.addMarker(markerOptions);
                    newCustomMarker();
                }
            });

            loadMarkers();

        }

    private void loadMarkers() {
        if (places != null && places.size() > 0) {
            if (mMap != null) {
                for (Place tmp : places) {
                    LatLng tmpLatLng = new LatLng(tmp.getLatitud(), tmp.getLongitud());
                    mMap.addMarker(new MarkerOptions().position(tmpLatLng).title(tmp.getNombre()).icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromURL(tmp.getThumbnail()))));
                }
            }

        }
    }

    private void chooseMoveCamera(GoogleMap mMap, LatLng latLng, int zoom) {
        CameraPosition cameraPosition = new CameraPosition.Builder().zoom(zoom).target(latLng).build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 10:
                if (followPosition != null) {
                    followPosition.register(MapsActivity.this);
                }
                break;
            default:
                break;
        }
    }


    public Bitmap getBitmapFromURL(String imageUrl) {
        try {
            Bitmap bmImg = Ion.with(this).load(imageUrl).asBitmap().get();

            return bmImg;

        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }

    }


    public void newCustomMarker(){
        LayoutInflater li = LayoutInflater.from(this);
        View dialogView = li.inflate(R.layout.dialog,null);
        AlertDialog.Builder alertDialog= new AlertDialog.Builder(this);
        alertDialog.setView(dialogView);
        final EditText userInput = (EditText) dialogView.findViewById(R.id.editTextDialogUserInputName);
        final EditText userInput2 = (EditText) dialogView.findViewById(R.id.editTextDialogUserInputDescription);
        mMap.clear();
        alertDialog.setTitle("Marcador nuevo")
                .setCancelable(false)
                .setMessage("")
                .setPositiveButton("Agregar marcador", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nombre = userInput.getText().toString();
                        String descripcion = userInput2.getText().toString();
                        mapasModel.insertar(nombre,descripcion,latitudCust,longitudCust,placeHolderCust,imgCust,CATEGORIA);
                        places.add(new Place(1,nombre,descripcion,latitudCust,longitudCust,placeHolderCust,imgCust,CATEGORIA));
                        Toast.makeText(getApplicationContext(),"Marcador agregado",Toast.LENGTH_LONG).show();
                        loadMarkers();
                    }
                })
                .setNegativeButton("No agregar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        loadMarkers();
                    }
                })
                .show();
    }

}
