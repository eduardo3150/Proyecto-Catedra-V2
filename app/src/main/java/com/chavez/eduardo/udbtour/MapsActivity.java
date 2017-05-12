package com.chavez.eduardo.udbtour;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
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
    LatLng defaultLatLng = new LatLng(13.714966, -89.155755);
    Button currentPos;
    Bitmap custom;

    FollowPosition followPosition;

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

        seekBarZoom = (SeekBar) findViewById(R.id.seekZoom);
        currentPos = (Button) findViewById(R.id.ubicacionActual);
        seekBarZoom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (followPosition.getLatLng() != null) {
                    chooseMoveCamera(mMap, followPosition.getLatLng(), progress);
                } else {
                    chooseMoveCamera(mMap, defaultLatLng, progress);
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
                    chooseMoveCamera(mMap, followPosition.getLatLng(), 10);
                }
            }
        });


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
                    // Clears the previously touched position
                    //mMap.clear();

                    // Animating to the touched position
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                    // Placing a marker on the touched position
                    mMap.addMarker(markerOptions);
                }
            });

            if (places != null && places.size() > 0) {
                if (mMap != null) {
                    for (Place tmp : places) {
                        LatLng tmpLatLng = new LatLng(tmp.getLatitud(), tmp.getLongitud());
                        mMap.addMarker(new MarkerOptions().position(tmpLatLng).title(tmp.getNombre()).icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromURL(tmp.getThumbnail()))));
                        if (tmp.getCategoria().equals("Personalizado")){
                            custom = getBitmapFromURL(tmp.getThumbnail());
                        }
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
}
