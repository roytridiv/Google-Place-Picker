package com.example.google_place_picker;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import www.sanju.motiontoast.MotionToast;

public class MakeRoute extends FragmentActivity implements OnMapReadyCallback, RoutingListener {

    private GoogleMap mMap;
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
    String from_lat, from_lng, to_lat, to_lng;
    LatLng start, end , fromMain , toMain;
    Button setLocation;

    List<LatLng> l ;

    LinearLayout linearLayout2 ;
    LottieAnimationView lottieAnimationView ;
    String dis = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_route);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Places.initialize(getApplicationContext(), "{PLACE-YOUR-API-KEY}");

        Intent intent = getIntent();
        //fromMain = intent.getData();
        from_lat = intent.getStringExtra("current_lat");
        from_lng = intent.getStringExtra("current_lng");
        to_lat = intent.getStringExtra("destination_lat");
        to_lng = intent.getStringExtra("destination_lng");

        Log.d("debug", "------------- Make route e ki paitesi--------------" + from_lat+" , "+from_lng+" , "+to_lat+" , "+to_lng);

        setLocation = findViewById(R.id.set_location);
        lottieAnimationView = findViewById(R.id.animationView2);
        linearLayout2 = findViewById(R.id.noRouteFound);

        setLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MakeRoute.this ,MyPlacePicker.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        MotionToast.Companion.createColorToast(this,"Loading Route!",
                MotionToast.Companion.getTOAST_INFO(),
                MotionToast.Companion.getGRAVITY_BOTTOM(),
                MotionToast.Companion.getLONG_DURATION(),
                ResourcesCompat.getFont(this,R.font.helvetica_regular));


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
//        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
//                .clickable(true)
//                .add(
//                        new LatLng(-35.016, 143.321),
//                        new LatLng(-32.491, 147.309)));
//        getRoute();


        // Getting URL to the Google Directions API
        // String url = getDirectionsUrl(new LatLng(23.747949, 90.396824), new LatLng(23.866874, 90.404575));
        String url = getDirectionsUrl(new LatLng(Double.parseDouble(from_lat), Double.parseDouble(from_lng)), new LatLng(Double.parseDouble(to_lat), Double.parseDouble(to_lng)));
        //String url = getDirectionsUrl(new LatLng(23.747546005998082, 23.747546005998082), new LatLng(23.866874, 90.404575));

        // DownloadTask downloadTask = new DownloadTask();
        TaskRequestDirection downloadTask = new TaskRequestDirection();


        // Start downloading json data from Google Directions API
        downloadTask.execute(url);


        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        // Set listeners for click events.


        // List<LatLng> path = new ArrayList();


        //Execute Directions API request
//        GeoApiContext context = new GeoApiContext.Builder()
//                .apiKey("YOUR_API_KEY")
//                .build();
//        DirectionsApiRequest req = DirectionsApi.getDirections(context, "41.385064,2.173403", "40.416775,-3.70379");

        // polyline1.setTag("A");
        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {

            }
        });
        // mMap.setOnPolylineClickListener();
        // googleMap.setOnPolygonClickListener(this);


    }

    @Override
    public void onRoutingFailure(RouteException e) {
        if (e != null) {
            // Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("debug", "Error: " + e.getMessage());
        } else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(), "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
        }
//        // Start marker
//        MarkerOptions options = new MarkerOptions();
//        options.position(start);
//        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
//        mMap.addMarker(options);
//
//        // End marker
//        options = new MarkerOptions();
//        options.position(end);
//        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green));
//        map.addMarker(options);
    }

    @Override
    public void onRoutingCancelled() {

    }

//    public void getRoute() {
//        Routing routing = new Routing.Builder()
//                .travelMode(Routing.TravelMode.WALKING)
//                .withListener(this)
//                .waypoints(new LatLng(23.747949, 90.396824), new LatLng(23.866874, 90.404575))
//                .build();
//        routing.execute();
//    }

//    private  void erase(){
//        for (Polyline line : polylines){
//            line.remove();
//        }
//        polylines.clear();
//    }
//
//    private String getrequestURL(LatLng a , LatLng b){
//        String org = "origin="+ a.latitude+" , "+a.longitude;
//        String dest = "destination="+ b.latitude+" , "+b.longitude;
//        String sensor = "sensor=false";
//        String mode = "mode=driving";
//        String param = org+"&"+dest+"&"+sensor+"&"+mode;
//
//        String out = "json";
//
//        String url = "https://maps.googleapis.com/maps/directios/"+out+"?"+param;
//        return url;
//    }
//
//    private  String requestDirection(String s) throws IOException {
//        String resposndString = "";
//        InputStream inputStream = null;
//        HttpURLConnection httpURLConnection = null;
//
//        try {
//            URL url = new URL(s);
//            httpURLConnection = (HttpURLConnection) url.openConnection();
//            httpURLConnection.connect();
//
//            inputStream = httpURLConnection.getInputStream();
//            InputStreamReader inputStreamReader = new  InputStreamReader(inputStream);
//            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//            StringBuffer stringBuffer = new StringBuffer();
//            String line = "";
//            while ((line = bufferedReader.readLine()) != null) {
//                stringBuffer.append(line);
//            }
//
//            resposndString = stringBuffer.toString();
//            bufferedReader.close();
//            inputStreamReader.close();
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (inputStream != null){
//                inputStream.close();
//        }
//            return  resposndString;
//
//        }
//    }


    public class TaskRequestDirection extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("debug", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ParserTask parserTask = new ParserTask();


            parserTask.execute(s);
        }
    }


    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&key=API_KEY";

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }


    public boolean flag1  = false ;

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {

                    HashMap point = path.get(j);

                    if (point.get("lat") != null && point.get("lng") != null) {

                        double lat = Double.parseDouble(point.get("lat").toString());
                        double lng = Double.parseDouble(point.get("lng").toString());

                        MarkerOptions options ;

                        if (start == null && flag1 == false) {
                            start = new LatLng(lat, lng);

                            // Start marker
                            options = new MarkerOptions();
                            options.position(start);
                            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurent));
                            mMap.addMarker(options);
                            flag1 = true;
                        }

                        if (end == null && j == path.size() - 1) {
                            end = new LatLng(lat, lng);

                            // End marker
                            options = new MarkerOptions();
                            options.position(end);
                            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.destination));
                            mMap.addMarker(options);
                        }

                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }


                }

                if(lineOptions != null) {
                    linearLayout2.setVisibility(View.GONE);
                    lottieAnimationView.setVisibility(View.GONE);

                    lineOptions.addAll(points);
                    lineOptions.width(8);
                    lineOptions.color(Color.RED);
                    lineOptions.geodesic(true);
                }else{
                    linearLayout2.setVisibility(View.VISIBLE);
                    lottieAnimationView.setVisibility(View.VISIBLE);
                }



            }

            if(lineOptions != null){
                linearLayout2.setVisibility(View.GONE);
                lottieAnimationView.setVisibility(View.GONE);
                // Drawing polyline in the Google Map for the i-th route
                mMap.addPolyline(lineOptions);

                if(start != null && end != null){
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(start));
//                float zoomLevel = 12.0f;
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start, zoomLevel));
//                mMap.getMaxZoomLevel();

                    // Start marker


                    MarkerOptions options = new MarkerOptions();
                    options.position(start);
                    options.icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurent));
                    mMap.addMarker(options);

                    // End marker

                    options = new MarkerOptions();
                    options.position(end);
                    options.icon(BitmapDescriptorFactory.fromResource(R.drawable.destination));
                    mMap.addMarker(options);

                    zoomRoute(mMap , l);
                }else{

                }
            }else{
                linearLayout2.setVisibility(View.VISIBLE);
                lottieAnimationView.setVisibility(View.VISIBLE);
            }




        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    public class DirectionsJSONParser {
        /**
         * Receives a JSONObject and returns a list of lists containing latitude and
         * longitude
         */


        public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

            List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
            JSONArray jRoutes = null;
            JSONArray jLegs = null;
            JSONArray jSteps = null;
            JSONObject jDistance = null;
            JSONObject jDuration = null;

            try {

                jRoutes = jObject.getJSONArray("routes");

                /** Traversing all routes */
                for (int i = 0; i < jRoutes.length(); i++) {
                    jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");

                    List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

                    /** Traversing all legs */
                    for (int j = 0; j < jLegs.length(); j++) {

                        /** Getting distance from the json data */
                        jDistance = ((JSONObject) jLegs.get(j))
                                .getJSONObject("distance");
                        HashMap<String, String> hmDistance = new HashMap<String, String>();
                        dis = jDistance.getString("value");
                        hmDistance.put("distance", jDistance.getString("text"));

                        /** Getting duration from the json data */
                        jDuration = ((JSONObject) jLegs.get(j))
                                .getJSONObject("duration");
                        HashMap<String, String> hmDuration = new HashMap<String, String>();
                        hmDuration.put("duration", jDuration.getString("text"));

                        /** Adding distance object to the path */
                        path.add(hmDistance);

                        /** Adding duration object to the path */
                        path.add(hmDuration);

                        jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                        /** Traversing all steps */
                        for (int k = 0; k < jSteps.length(); k++) {
                            String polyline = "";
                            polyline = (String) ((JSONObject) ((JSONObject) jSteps
                                    .get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);

                            l=list ;

                            /** Traversing all points */
                            for (int l = 0; l < list.size(); l++) {
                                HashMap<String, String> hm = new HashMap<String, String>();
                                hm.put("lat",
                                        Double.toString(((LatLng) list.get(l)).latitude));
                                hm.put("lng",
                                        Double.toString(((LatLng) list.get(l)).longitude));
                                path.add(hm);
                            }
                        }
                    }
                    routes.add(path);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
            }

            return routes;
        }

        /**
         * Method to decode polyline points Courtesy :
         * jeffreysambells.com/2010/05/27
         * /decoding-polylines-from-google-maps-direction-api-with-java
         */
        private List<LatLng> decodePoly(String encoded) {

            List<LatLng> poly = new ArrayList<LatLng>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }

            return poly;
        }

    }
//
//    public void zoomRoute(GoogleMap googleMap, List<LatLng> lstLatLngRoute) {
//
//        if (googleMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;
//
//        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
//        for (LatLng latLngPoint : lstLatLngRoute)
//            boundsBuilder.include(latLngPoint);
//
//        int routePadding = 0;
//
//        LatLngBounds latLngBounds = boundsBuilder.build();
//        googleMap.setPadding(10, 200, 10, 100);
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
//    }


    public void zoomRoute(GoogleMap googleMap, List<LatLng> lstLatLngRoute) {
        boolean hasPoints = false;
        Double maxLat = null, minLat = null, minLon = null, maxLon = null;

        List<LatLng> pts = lstLatLngRoute;
        for (LatLng coordinate : pts) {

            maxLat = maxLat != null ? Math.max(coordinate.latitude, maxLat) : coordinate.latitude;
            minLat = minLat != null ? Math.min(coordinate.latitude, minLat) : coordinate.latitude;

            maxLon = maxLon != null ? Math.max(coordinate.longitude, maxLon) : coordinate.longitude;
            minLon = minLon != null ? Math.min(coordinate.longitude, minLon) : coordinate.longitude;

            hasPoints = true;
        }

        Log.d("debug" , " Max --->"+ maxLat + " , "+maxLon + " , MIN----->"+ minLat+" , "+minLon);

        if (hasPoints) {
            LatLngBounds latLngBounds = new LatLngBounds(  new LatLng(minLat, minLon), new LatLng(maxLat, maxLon));

            float zoomLevel = 0.0f;

            float f = Float.parseFloat(dis);

            if (f < 1128) { zoomLevel = 15.5f; }
            else if ((f > 1128) && (f < 2256)) { zoomLevel = 14.5f; }
            else if ((f > 2256) && (f < 4513)) { zoomLevel = 13.5f; }
            else if ((f > 4513) && (f < 9027)) { zoomLevel = 12.5f; }
            else if ((f > 9027) && (f < 18055)) { zoomLevel = 11.5f; }
            else if ((f > 18055) && (f < 36111)) { zoomLevel = 10.5f; }
            else if ((f > 36111) && (f < 722223)){ zoomLevel = 9.5f ; }



            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngBounds.getCenter(), zoomLevel));
            googleMap.getMinZoomLevel();
        }

    }
}
