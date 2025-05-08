package com.cardoso.shareyourtime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button btnAceptar = findViewById(R.id.btnAceptar);
        btnAceptar.setOnClickListener(v -> {
            if (marker != null) {
                LatLng posicion = marker.getPosition();
                enviarUbicacionDeVuelta(posicion.latitude, posicion.longitude);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Centrar el mapa en una posición inicial
        LatLng centro = new LatLng(40.4168, -3.7038); // Madrid como centro inicial
        marker = mMap.addMarker(new MarkerOptions()
            .position(centro)
            .draggable(true)
            .title(getString(R.string.drag_marker)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centro, 5));

        // Permitir hacer clic en el mapa para mover el marcador
        mMap.setOnMapClickListener(latLng -> {
            marker.setPosition(latLng);
        });
    }

    private void enviarUbicacionDeVuelta(double lat, double lon) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("lat", lat);
        resultIntent.putExtra("lon", lon);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
} 