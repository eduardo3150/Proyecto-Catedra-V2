package com.chavez.eduardo.udbtour;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

/**
 * Created by Eduardo_Chavez on 9/5/2017.
 */

public class FollowPosition implements LocationListener {
    private LocationManager locationManager;
    private Context context;

    private static int DISTANCE = 1;
    private static int TIME = 3000;

    private String provider;
    private List<String> listProvider;
    private int contadorProvider = 0;
    private Marker point;

    private GoogleMap mMap;
    private LatLng latLng;
    public FollowPosition(GoogleMap map, Context context) {
        this.setmMap(map);
        this.context = context;

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        listProvider = locationManager.getAllProviders();
        setLocationProvider();

    }

    private void setLocationProvider() {
        if (provider == null) {
            provider = LocationManager.GPS_PROVIDER;

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Habilitar GPS");
                builder.setMessage("¿Desea ir a la configuracion de red?");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent);
                    }
                });
                builder.create().show();
            }
        } else {
            if (contadorProvider < listProvider.size() - 1) {
                contadorProvider++;
            } else {
                contadorProvider = 0;
            }
            provider = listProvider.get(contadorProvider);

            Toast.makeText(context, "Proveedor seleccionado: " + provider, Toast.LENGTH_LONG).show();
        }
    }

    public void register(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                ActivityCompat.requestPermissions((Activity) context,new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.INTERNET},
                        10);
            }
            return;
        }
        this.locationManager.requestLocationUpdates(provider,TIME,10,this);
    }

    public void unRegister(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                ActivityCompat.requestPermissions((Activity) context,new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.INTERNET},
                        10);
            }
            return;
        }
        this.locationManager.removeUpdates(this);
    }


    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions currentPos = new MarkerOptions();

        currentPos.position(latLng);
        currentPos.title("Ubicación actual");
        currentPos.icon(BitmapDescriptorFactory.fromResource(R.drawable.ubicacionactual));

        if (point != null) {
            point.remove();
        }

        point = getmMap().addMarker(currentPos);
        setLatLng(latLng);
    }

    public GoogleMap getmMap() {
        return mMap;
    }

    public void setmMap(GoogleMap mMap) {
        this.mMap = mMap;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }


    public LatLng getLatLng() {
        return latLng;
    }
}
