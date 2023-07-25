package com.example.incidentreportingapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.Nullable;


public class MapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private TextView txtCordinates;
    private FusedLocationProviderClient fusedLocationClient;
    private Marker currentLocationMarker;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        txtCordinates = rootView.findViewById(R.id.txtCordinates);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.myMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
            setLocationPermissionGranted();
        }

        return rootView;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
//        mMap = googleMap;
//
//        // Check if location permission is granted, and request it if not
//        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
//            return;
//        }
//
//        // Enable the My Location layer and zoom to the current location
////        mMap.setMyLocationEnabled(true);
//        fusedLocationClient.getLastLocation()
//                .addOnSuccessListener(getActivity(), location -> {
//                    if (location != null) {
//                        LatLng currentLatLng = new LatLng(1.373333, 32.290275);
//                        currentLocationMarker = mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
//                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
//                        txtCordinates.setText("Current Location: " + currentLatLng.latitude + ", " + currentLatLng.longitude);
//                    }
//                });

    }
    public void setLocationPermissionGranted() {
        // Check if location permission is granted, and proceed with map initialization
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            onMapReady(mMap);
        }
    }
}