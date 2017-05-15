package com.chavez.eduardo.udbtour;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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

import java.util.ArrayList;

import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;

    EditText userInput;
    EditText userInput2;

    GoogleApiClient mGoogleApiClient;
    Location lastLocation;
    Marker mCurrentLocationMarker;
    LocationRequest mLocationRequest;


    ArrayList<Place> places = new ArrayList<>();
    SeekBar seekBarZoom;
    LatLng defaultLatLng, currentLatLng;

    Button currentPos, changeMap;
    Bitmap custom;
    String placeHolderCust, imgCust;
    String CATEGORIA = "Sitios que recomiendo";
    int zoomSaved = 10;
    int zoomSelect = 10;

    int touch=0;
    MapasModel mapasModel;
    Double newLat,newLon;

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
        defaultLatLng = new LatLng(places.get(0).getLatitud(), places.get(0).getLongitud());
        mapasModel = new MapasModel(this);
        placeHolderCust = "http://i.imgur.com/XVJ4SSM.png";
        imgCust = "https://i.imgur.com/yr95Qr7.png";


        seekBarZoom = (SeekBar) findViewById(R.id.seekZoom);
        currentPos = (Button) findViewById(R.id.ubicacionActual);
        changeMap = (Button) findViewById(R.id.cambiarMapa);
        seekBarZoom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (zoomSelect == 10) {
                    chooseMoveCamera(mMap, defaultLatLng, progress);
                }

                if (zoomSelect == 20) {
                    chooseMoveCamera(mMap, currentLatLng, progress);
                }
                zoomSaved = progress;

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
                chooseMoveCamera(mMap, currentLatLng, zoomSaved);
                zoomSelect = 20;
            }
        });

        currentPos.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                newCustomMarker(currentLatLng.latitude, currentLatLng.longitude);
                zoomSelect = 20;
                return true;
            }
        });
        changeMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (touch){
                    case 0:
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        touch = 1;
                        break;
                    case 1:
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        touch = 2;
                        break;
                    case 2:
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        touch = 3;
                        break;
                    case 3:
                        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        touch = 0;
                        break;
                    default:
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        touch = 0;

                        break;

                }
            }
        });
        currentPos.setEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        custom = getBitmapFromURL(imgCust);
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(defaultLatLng));
        chooseMoveCamera(mMap, defaultLatLng, zoomSaved);

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
                mMap.clear();
                // Animating to the touched position
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                // Placing a marker on the touched position
                //mMap.addMarker(markerOptions);
                newCustomMarker(latLng.latitude, latLng.longitude);
            }
        });


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                defaultLatLng = marker.getPosition();
                zoomSelect = 10;
                return true;
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


    public void newCustomMarker(final Double latitudObt, final Double longitdObt) {
        newLat = latitudObt;
        newLon = longitdObt;
        LayoutInflater li = LayoutInflater.from(this);
        View dialogView = li.inflate(R.layout.dialog, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setView(dialogView);
        userInput = (EditText) dialogView.findViewById(R.id.editTextDialogUserInputName);
        userInput2 = (EditText) dialogView.findViewById(R.id.editTextDialogUserInputDescription);
        mMap.clear();
        alertDialog.setTitle("Marcador nuevo")
                .setCancelable(false)
                .setMessage("")
                .setPositiveButton("Agregar marcador", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("No agregar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        loadMarkers();
                    }
                });
        AlertDialog alertDialogN = alertDialog.create();
        alertDialogN.show();

        Button validator = alertDialogN.getButton(DialogInterface.BUTTON_POSITIVE);
        validator.setOnClickListener(new CustomListener(alertDialogN));

    }


    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        if (mCurrentLocationMarker != null) {
            mCurrentLocationMarker.remove();
        }
        if (location != null){
            currentPos.setEnabled(true);
            currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        }
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Posicion Actual");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrentLocationMarker = mMap.addMarker(markerOptions);


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Se necesita el permiso de Ubicacion")
                        .setMessage("Esta Aplicacion necesita los permisos de ubicación")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    class CustomListener implements View.OnClickListener{
        private final Dialog dialog;
        public CustomListener(Dialog dialog){
            this.dialog = dialog;
        }

        @Override
        public void onClick(View view) {
            validateNameField(userInput);
        }

        private boolean validateNameField(EditText editText){
            String regexString = "^[A-Za-z\\S]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$";
            Pattern r = Pattern.compile(regexString);

            Matcher m =r.matcher(editText.getText());
            if (m.matches()){
                String nombre = userInput.getText().toString();
                String descripcion = userInput2.getText().toString();
                mapasModel.insertar(nombre, descripcion, newLat, newLon, placeHolderCust, imgCust, CATEGORIA);
                places.add(new Place(1, nombre, descripcion, newLat, newLon, placeHolderCust, imgCust, CATEGORIA));
                Toast.makeText(getApplicationContext(), "Marcador agregado", Toast.LENGTH_LONG).show();
                loadMarkers();
                dialog.dismiss();
            } else {
                errorField(editText);
                editText.setError("Ingrese caracteres validos");
                return false;
            }

            return true;
        }

        void errorField(EditText editText){
            editText.setBackgroundColor(Color.rgb(255,235,238));
        }


    }

}


