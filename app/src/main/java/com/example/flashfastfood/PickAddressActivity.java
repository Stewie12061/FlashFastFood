package com.example.flashfastfood;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.flashfastfood.Guest.GuestCheckOutActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PickAddressActivity extends AppCompatActivity {

    private GoogleMap mMap;
    Geocoder geocoder;
    CardView btnSubmitAddress, btnConfirmAddress;
    EditText edtAddress;
    String ItemAddress = null, orderItemQuantity, orderPrice, guestFlag;

    Boolean fag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_address);

        btnSubmitAddress = findViewById(R.id.submitAddress);
        edtAddress = findViewById(R.id.edtAddress);
        btnConfirmAddress = findViewById(R.id.btnConfirmAddress);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                mMap = googleMap;
                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(PickAddressActivity.this,R.raw.map_style));
                googleMap.getUiSettings().setMapToolbarEnabled(false);

                geocoder = new Geocoder(PickAddressActivity.this, Locale.getDefault());

            }
        });

        orderItemQuantity = getIntent().getStringExtra("orderItemQuantity");
        orderPrice = getIntent().getStringExtra("orderPrice");
        guestFlag = getIntent().getStringExtra("guestFlag");
        if (guestFlag==null){
            guestFlag="user";
        }

        btnSubmitAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Address> addressList = null;

                if (edtAddress.getText().toString().equals("")){
                    edtAddress.setError("Fill in your address!");
                    edtAddress.requestFocus();
                }else {
                    ItemAddress = edtAddress.getText().toString();

                    try {
                        addressList = geocoder.getFromLocationName(ItemAddress,1);


                        if (addressList != null && addressList.size() > 0){
                            Address address = addressList.get(0);

                            Log.d(TAG,address.getLatitude()+" "+ address.getLongitude());

                            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(latLng).title(ItemAddress).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                            mMap.getUiSettings().setZoomControlsEnabled(true);
                            fag=true;
                        }
                        else {
                            Toast.makeText(PickAddressActivity.this, "Can't find location",Toast.LENGTH_SHORT).show();
                            fag=false;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }
        });

        btnConfirmAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtAddress.getText().toString().equals("")){
                    edtAddress.setError("Fill in your address!");
                    edtAddress.requestFocus();
                }else {
                    if (fag==true&&guestFlag.equals("user")){
                        Intent intent = new Intent(PickAddressActivity.this,CheckOutActivity.class);
                        intent.putExtra("orderItemQuantity",orderItemQuantity);
                        intent.putExtra("orderPrice",orderPrice);
                        intent.putExtra("orderAddress",edtAddress.getText().toString());
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }else if (fag==true&&guestFlag.equals("guest")){
                        Intent intent = new Intent(PickAddressActivity.this, GuestCheckOutActivity.class);
                        intent.putExtra("orderItemQuantity",orderItemQuantity);
                        intent.putExtra("orderPrice",orderPrice);
                        intent.putExtra("orderAddress",edtAddress.getText().toString());
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        edtAddress.setError("Please choose a different address");
                        edtAddress.requestFocus();
                    }
                }
            }
        });

    }
}