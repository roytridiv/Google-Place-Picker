package com.example.google_place_picker;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.provider.SettingsSlicesContract.KEY_LOCATION;

public class MyPlacePicker extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    final int place_picker_req_code = 1;
    LatLng latLng, current;
    LatLng center;
    String name;
    ImageView marker;
    View mMarkerParentView;

    TextView textView;
    List<Address> addresses = null;

    private static final String KEY_LOCATION = "location";

    private FusedLocationProviderClient fusedLocationProviderClient;



    private Location lastknownlocation;
    private String s = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_place_picker);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Places.initialize(getApplicationContext(), "AIzaSyAsMc4rdygUZ3ye9iOfuio_3Ek41KZaaEE");


        mMarkerParentView = findViewById(R.id.marker_view_incl);
        marker = findViewById(R.id.marker_icon_view);
        textView = findViewById(R.id.location);


        if (savedInstanceState != null)
            lastknownlocation = savedInstanceState.getParcelable(KEY_LOCATION);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MyPlacePicker.this);


        if (ok()) {
            init();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case place_picker_req_code:
                Place place2;
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.d("debug", "Eita on activity er moddhe er lat lang: " + place.getName());
                name = place.getName();
                // String p = place2.getP
                latLng = place.getLatLng();
                // lat = place.get
                mMap.addMarker(new MarkerOptions().position(latLng).title(name));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                break;
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


        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                center = mMap.getCameraPosition().target;
               // Log.d("debug", "Eita certer er lat lang: " + center.toString());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(center));
                sendData(""+center.latitude , ""+center.longitude);
                textView.setText("Loading....");
               // textView.setText(center.toString());
                //setLocation(center);

            }
        });

//        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
//            @Override
//            public void onCameraIdle() {
//                if(center != null){
//                    Log.d("debug", "Eita certer er lat lang: " + center.toString());
//                    textView.setText(center.toString());
//                    setLocation(center);
//                }
//            }
//        });

    }



    public void setLocation(LatLng l) {

        Geocoder geocoder = new Geocoder(MyPlacePicker.this);
        try {

            addresses = geocoder.getFromLocation(l.latitude, l.longitude, 1);

            if (addresses.size() != 0) {
                Address obj = addresses.get(0);
                String add = obj.getAddressLine(0);
                add = add + " , " + obj.getCountryName() + " , " + obj.getCountryCode() + " , " + obj.getAdminArea();
                textView.setText(add);


            } else {

                Log.d("debug", "kono location i paynai :/ ");
            }
            //String add = obj.getAddressLine(0);
            // add = add + " , " + obj.getCountryName()+ " , " +obj.getCountryCode()+ " , " +obj.getAdminArea();
            //textView.setText(add);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("debug", "The size is " +
                addresses.size());
    }


    private void init() {


        if (ActivityCompat.checkSelfPermission(MyPlacePicker.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyPlacePicker.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("debug", "permission");
            requestPermission();
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(MyPlacePicker.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Log.d("debug", "location found");

                    current = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
                    float zoomLevel = 16.0f;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, zoomLevel));
                    mMap.getMaxZoomLevel();
                    setLocation(current);
                }
            }
        });
    }

    public boolean ok() {

        boolean res = false;
        Log.d("debug", "okok");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MyPlacePicker.this);

        if (available == ConnectionResult.SUCCESS) {
            Log.d("debug", "its working");
            res = true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            System.out.println("n");
            Log.d("debug", "error ");
            // Dialog d = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available , Error);
            res = false;

        } else {
            Toast.makeText(MyPlacePicker.this, "Can not make a map", Toast.LENGTH_SHORT).show();
        }
        return res;
    }


    public void requestPermission() {
        ActivityCompat.requestPermissions(MyPlacePicker.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    public void sendData(String lati , String longi){

        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .findLocation(lati, longi);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String response_from_the_server = null;

                if(response.body() != null){
                    response_from_the_server = response.body().toString();

                }else{
                    Log.d("debug" , "response painai");
                }

                Log.d("debug" , "response paisi : " + response_from_the_server);

                JSONObject jsonObject = null;
                try {
                    if(response_from_the_server != null) {
                        jsonObject = new JSONObject(response_from_the_server);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    if(jsonObject != null){
                        JSONObject jsonObject1 = jsonObject.getJSONObject("geolocation");
                        Log.d("debug", jsonObject1.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }




}
