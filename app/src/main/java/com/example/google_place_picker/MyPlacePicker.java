package com.example.google_place_picker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import www.sanju.motiontoast.MotionToast;

import static android.provider.SettingsSlicesContract.KEY_LOCATION;

public class MyPlacePicker extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
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

    EditText from , to ;
    Button proceed , closeKeyboard;

    String from_lat , from_lng , to_lat , to_lng = "";
    LatLng fromMain , toMain ;
    ImageView my_location ;
    Context context ;

    boolean flag_from =true ;
    boolean flag_to = false ;

    boolean empty_from_flag = true;
    boolean empty_to_flag = false ;

    boolean current_flag =true;

    boolean map_moved_from_flag = true;
    boolean map_moved_to_flag  = false ;

    LottieAnimationView l;
    LinearLayout linearLayout ;
    public String loc ="";
    boolean gps_enabled = false;
    boolean network_enabled = false;
    LocationManager lm ;

    GoogleApiClient mGoogleApiClient ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_place_picker);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);



        onStart();

        mMarkerParentView = findViewById(R.id.marker_view_incl);
        marker = findViewById(R.id.marker_icon_view);
        textView = findViewById(R.id.location);
        from = findViewById(R.id.from);
        to = findViewById(R.id.to);
        proceed = findViewById(R.id.makeRoute);
        my_location = findViewById(R.id.myLocation);
        closeKeyboard =  findViewById(R.id.set_on_map);
        l = findViewById(R.id.animationView);
        linearLayout = findViewById(R.id.noInternet);

        if(!internetOn()){


            linearLayout.setVisibility(View.VISIBLE);
            //mMapFragment.getView().setVisibility(View.INVISIBLE);

            l.setVisibility(View.VISIBLE);
            l.playAnimation();
            l.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {

                }
            });

            l.setProgress(0.5f);
        }else{


            if(!isLocationEnabled(MyPlacePicker.this)){
                displayPromptForEnablingGPS(MyPlacePicker.this);
                // restartSctivity();
            }

            l.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
            mapFragment.getMapAsync(this);


            Places.initialize(getApplicationContext(), "AIzaSyAsMc4rdygUZ3ye9iOfuio_3Ek41KZaaEE");


            MotionToast.Companion.createToast(this,"Map generated!",
                    MotionToast.Companion.getTOAST_SUCCESS(),
                    MotionToast.Companion.getGRAVITY_BOTTOM(),
                    MotionToast.Companion.getLONG_DURATION(),
                    ResourcesCompat.getFont(this,R.font.helvetica_regular));


            Toast.makeText(MyPlacePicker.this , "Press the Red Button to get your location" ,Toast.LENGTH_LONG).show();

            if (savedInstanceState != null)
                lastknownlocation = savedInstanceState.getParcelable(KEY_LOCATION);




            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MyPlacePicker.this);


            if (ok()) {
                if(!isLocationEnabled(MyPlacePicker.this)){
                    displayPromptForEnablingGPS(MyPlacePicker.this);
                }

                init();

            }



            from.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                        closeKeyboard.setVisibility(View.VISIBLE);

                        flag_from = true ;
                        flag_to = false ;

                        empty_from_flag = true ;
                        empty_to_flag = false ;


                        Log.d("debug", "--------------Edit text in FORM typing -------------");

                }
            });


            to.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    closeKeyboard.setVisibility(View.VISIBLE);

                    flag_to = true ;
                    flag_from = false ;


                    empty_to_flag = true ;
                    empty_from_flag = false ;

//                        if(center == null){
//                            to.setText("");
//                        }else{
//                            to.setText(setLocation(center));
//                        }


                    Log.d("debug", "--------------Edit text in TO typing -------------");
                }
            });


            from.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onFocusChange(View view, boolean hasFocus) {



                    if (hasFocus) {
                        closeKeyboard.setVisibility(View.VISIBLE);

                        flag_from = true ;
                        flag_to = false ;

//                        empty_from_flag = true ;
//                        empty_to_flag = false ;
////                        if(center == null){
////                            from.setText("");
////                        }else{
////                            from.setText(setLocation(center));
////                        }


                        Log.d("debug", "--------------Edit text in FORM typing -------------");

                    } else {
                        //focus has stopped perform your desired action
                    }
                }
            });
//
            to.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        closeKeyboard.setVisibility(View.VISIBLE);

                        flag_to = true ;
                        flag_from = false ;


//                        empty_to_flag = true ;
//                        empty_from_flag = false ;

//                        if(center == null){
//                            to.setText("");
//                        }else{
//                            to.setText(setLocation(center));
//                        }


                        Log.d("debug", "--------------Edit text in TO typing -------------");


                    } else {
                        //focus has stopped perform your desired action
                    }
                }
            });

            closeKeyboard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideSoftKeyboard(MyPlacePicker.this);
                    closeKeyboard.setVisibility(View.GONE);
                }
            });


        }

        my_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // locationCheck(MyPlacePicker.this);
                if(!isLocationEnabled(MyPlacePicker.this)){
                    displayPromptForEnablingGPS(MyPlacePicker.this);
                   // restartSctivity();
                }
                init();
            }
        });

        init();

//        while(current_flag){
//            init();
//            if(!current_flag){
//                break;
//            }
//
//        }

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyPlacePicker.this, MakeRoute.class);

                if(from_lat == "" || from_lng == "" || to_lat == "" || to_lng == "" || center == null || from_lat == null || from_lng == null || to_lat == null || to_lng == null){
                   // Toast.makeText(MyPlacePicker.this , " Please set location again" , Toast.LENGTH_LONG);
                    MotionToast.Companion.createToast(MyPlacePicker.this,"No internet / Location not found!",
                            MotionToast.Companion.getTOAST_ERROR(),
                            MotionToast.Companion.getGRAVITY_BOTTOM(),
                            MotionToast.Companion.getLONG_DURATION(),
                            ResourcesCompat.getFont(MyPlacePicker.this,R.font.helvetica_regular));

                }else{
                    intent.putExtra("current_lat", from_lat );
                    intent.putExtra("current_lng", from_lng);
                    intent.putExtra("destination_lat", to_lat);
                    intent.putExtra("destination_lng", to_lng);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }

                Log.d("debug" ,"--------ki pailm----------" +from_lat+" , "+from_lng+" , "+to_lat+" , "+to_lng);


            }
        });



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

                latLng = place.getLatLng();
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

                Log.d("debug" , "--------------- Map is moving ---------------");

                closeKeyboard.setVisibility(View.GONE);
                 hideSoftKeyboard(MyPlacePicker.this);

                 empty_from_flag = false;
                 empty_to_flag = false ;

                map_moved_from_flag = true ;
            }
        });




        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Log.d("debug" , "--------------- Map is IDLE ---------------");

                center = mMap.getCameraPosition().target;
                // Log.d("debug", "Eita certer er lat lang: " + center.toString());
                Log.d("debug" , "---------- center er lat lang-------- "+ center.toString());
               // mMap.moveCamera(CameraUpdateFactory.newLatLng(center));

//
//               // fromMain = center ;
//
                if(flag_from){
                    //from.setText(center.latitude + " , "+center.longitude);

//                    if(empty_from_flag){
//
//                    }else{
                      //  from.setText(setLocation(center));
                        Log.d("debug" ,"--------------- flags from ---------------"+ flag_from );
                        if(center != null){
                            from_lat = ""+center.latitude ;
                            from_lng = ""+center.longitude;

                            //  from.setText(setLocation(center));
                            // from.setText(from_lat + " , "+ from_lng);
                            if(map_moved_from_flag){
                                from.setText(setLocation(center));
                                map_moved_from_flag = false ;
                            }

                        }
                   // }


                }

                if(flag_to){
                   // to.setText(center.latitude + " , "+center.longitude);

//                    if(empty_to_flag){
//
//                    }else{
                       // to.setText(setLocation(center));
                        Log.d("debug" ,"--------------- flags to ---------------"+ flag_to );
                        if(center != null){
                            to_lat = ""+center.latitude ;
                            to_lng = ""+center.longitude;
                            if(map_moved_from_flag){
                                to.setText(setLocation(center));
                                map_moved_from_flag =false ;
                            }

                        }
                   // }


                }




//                if(center != null){
//                    Log.d("debug", "Eita certer er lat lang: " + center.toString());
////                    textView.setText(center.toString());
////                    setLocation(center);
//
//                    sendData(""+center.latitude , ""+center.longitude);
//
//                }
            }
        });

    }




    public String setLocation(LatLng l) {

        Geocoder geocoder = new Geocoder(MyPlacePicker.this);
        try {
            final String k ;

            addresses = geocoder.getFromLocation(l.latitude, l.longitude, 1);

            if (addresses.size() != 0) {
                Address obj = addresses.get(0);
                String add = obj.getAddressLine(0);
               // add = add + " , " + obj.getCountryName() + " , " + obj.getCountryCode() + " , " + obj.getAdminArea();
               // from.setText(add);

                loc = add+"";

            } else {

                Log.d("debug", "-----------kono location i paynai :/ ----------");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
       // Log.d("debug", "----------------The size is ---------------------" + addresses.size());

        return  loc;
    }


    private void init() {

        Log.d("debug", "---------------------- init call korsi ----------------------");

        if (ActivityCompat.checkSelfPermission(MyPlacePicker.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyPlacePicker.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("debug", "----------------------permission er request kortese --------------------");
            requestPermission();
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(MyPlacePicker.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Log.d("debug", "--------------location found------------------");

                    current = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
                    float zoomLevel = 16.0f;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, zoomLevel));
                    mMap.getMaxZoomLevel();
                    setLocation(current);

                    from_lat = current.latitude+"" ;
                    from_lng = current.longitude+"";
                    current_flag = false ;

                }else{
                    Log.d("debug", "--------------location not found again calling init ------------------");
                    current_flag = true ;
                    init();
                }
            }


        });

//        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(
//                new OnCompleteListener<Location>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Location> task) {
//                        Location location = task.getResult();
//                if (location != null) {
//                    Log.d("debug", "--------------location found------------------");
//
//                    current = new LatLng(location.getLatitude(), location.getLongitude());
//                    mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
//                    float zoomLevel = 16.0f;
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, zoomLevel));
//                    mMap.getMaxZoomLevel();
//                    setLocation(current);
//
//                    from_lat = current.latitude+"" ;
//                    from_lng = current.longitude+"";
//                    current_flag = false ;
//
//                }else{
//
//
//                    Log.d("debug", "--------------location not found again calling init ------------------");
//                    current_flag = true ;
//                }
//                    }
//                }
//        );

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
            res = false;

        } else {
            Toast.makeText(MyPlacePicker.this, "Can not make a map", Toast.LENGTH_SHORT).show();
        }
        return res;
    }


    public void requestPermission() {
        ActivityCompat.requestPermissions(MyPlacePicker.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        Log.d("debug ", "------------------ getting permission  --------------------");
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

    //-------------  HIDING THE KEYBOARD ------------------ //

    public static void hideSoftKeyboard(Activity activity) {

        Log.d("debug ", "-------------------- keyboard off-----------------");
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);

        View focusedView = activity.getCurrentFocus();

        if (focusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    //-------------------- CHECK ON INTERNET ---------------- //

    private boolean internetOn(){
        boolean have_wifi = false ;
        boolean have_data = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();

        for(NetworkInfo info:networkInfos){
            if (info.getTypeName().equalsIgnoreCase("WIFI")){
                if(info.isConnected()){
                    have_wifi=true;
                }

            }
            if (info.getTypeName().equalsIgnoreCase("MOBILE")){

                if (info.isConnected()){
                    have_data = true;
                }


            }
        }
        return have_wifi || have_data ;
    }


    public static void displayPromptForEnablingGPS(
            final Activity activity)
    {
        final AlertDialog.Builder builder =
                new AlertDialog.Builder(activity);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "Enable either GPS or any other location"
                + " service to find current location.  Click OK to go to"
                + " location services settings to let you do so.";

        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                activity.startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.isConnected();
        }
    }

    protected void onResume() {

        super.onResume();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.isConnected();
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//       // setUpMapIfNeeded();
//        mGoogleApiClient.isConnected();
//    }




}
