package com.geek.lesson37;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.geek.lesson37.databinding.ActivityMainBinding;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private ActivityMainBinding binding;
    private LocationManager locationManager;
    private final int PERMISSION_REQUEST_CODE = 1;
    private MapboxMap mMap;
    private Prefs prefs;
    private LatLng coords, coords2;
    private Polyline polyline = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefs = new Prefs(this);
        setupMap();
        setupLocationRequest();
        binding.btnDrawLine.setOnClickListener(v -> drawLine());

        binding.btnClearAll.setOnClickListener(v -> clearAll());
    }

    private void clearAll() {
        prefs.deleteAll(this);
        mMap.removePolyline(polyline);
        mMap.removeMarker(mMap.getMarkers().get(0));
        mMap.removeMarker(mMap.getMarkers().get(0));
    }

    private void drawLine() {
        if (polyline != null) mMap.removePolyline(polyline);
        PolylineOptions polylineOptions = new PolylineOptions()
                .add(new LatLng(prefs.getMarker().getCord1(), prefs.getMarker().getCord2()), new LatLng(prefs.getMarker2().getCord1(), prefs.getMarker2().getCord2()))
                .width(5)
                .color(Color.RED);
        polyline = mMap.addPolyline(polylineOptions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0F, this);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setupLocationRequest() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        checkMyLocationPermission();
    }

    private void checkMyLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        else locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0F, this);
    }

    private void setupMap() {
        binding.mapView.getMapAsync(mapboxMap -> {
            mMap = mapboxMap;
            MarkerOptions options2 = new MarkerOptions().position(new LatLng(prefs.getMarker().getCord1(), prefs.getMarker().getCord2()));
            MarkerOptions options3 = new MarkerOptions().position(new LatLng(prefs.getMarker2().getCord1(), prefs.getMarker2().getCord2()));
            mMap.addMarker(options2);
            mMap.addMarker(options3);
            mapboxMap.setStyle(Style.SATELLITE_STREETS, style -> {
            });

            mapboxMap.addOnMapClickListener(point -> {
                MarkerOptions options = new MarkerOptions().position(point);
                if (mapboxMap.getMarkers().size() <= 1) {
                    mapboxMap.addMarker(options);
                } else {
                    mapboxMap.removeMarker(mapboxMap.getMarkers().get(0));
                    mapboxMap.addMarker(options);
                    coords2 = mapboxMap.getMarkers().get(1).getPosition();
                    coords = mapboxMap.getMarkers().get(0).getPosition();
                }
                //Toast.makeText(MainActivity.this.getBaseContext(), String.valueOf(point.getLatitude()) + point.getLongitude(), Toast.LENGTH_SHORT).show();
                prefs.setMarker(coords.getLatitude(), coords.getLongitude());
                prefs.setMarker2(coords2.getLatitude(), coords2.getLongitude());
                return true;
            });

            mapboxMap.setOnMarkerClickListener(marker -> {
                CameraPosition position = new CameraPosition.Builder().zoom(15.0)
                        .target(marker.getPosition())
                        .bearing(100.0)
                        .tilt(30.0)
                        .build();
                CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
                mapboxMap.animateCamera(update, 4000);
                //mapboxMap.removeMarker(marker);
                return true;
            });
        });
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            CameraPosition position = new CameraPosition.Builder()
                    .zoom(18.0)
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .bearing(100.0)
                    .tilt(30.0)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 5000);
        } else Log.e("TAG", "ERROR ");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
    }
}