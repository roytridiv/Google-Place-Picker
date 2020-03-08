package com.example.google_place_picker;

import androidx.fragment.app.FragmentActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
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
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MakeRoute extends FragmentActivity implements OnMapReadyCallback, RoutingListener {

    private GoogleMap mMap;
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_route);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Places.initialize(getApplicationContext(), "AIzaSyDq5gYTlfiBuszmn2IrwQ7T0vxgIBn3Qac");


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
        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(
                        new LatLng(-35.016, 143.321),
                        new LatLng(-34.747, 145.592),
                        new LatLng(-34.364, 147.891),
                        new LatLng(-33.501, 150.217),
                        new LatLng(-32.306, 149.248),
                        new LatLng(-32.491, 147.309)));
        getRoute();

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        // Set listeners for click events.



        List<LatLng> path = new ArrayList();


        //Execute Directions API request
//        GeoApiContext context = new GeoApiContext.Builder()
//                .apiKey("YOUR_API_KEY")
//                .build();
//        DirectionsApiRequest req = DirectionsApi.getDirections(context, "41.385064,2.173403", "40.416775,-3.70379");

        polyline1.setTag("A");
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
        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("debug" , "Error: " + e.getMessage());
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
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

    public  void getRoute(){
        Routing routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.WALKING)
                .withListener(this)
                .waypoints(new LatLng(23.777176, 90.399452), new LatLng(25.74664 , 89.25166))
                .build();
        routing.execute();
    }

    private  void erase(){
        for (Polyline line : polylines){
            line.remove();
        }
        polylines.clear();
    }

    private String getrequestURL(LatLng a , LatLng b){
        String org = "origin="+ a.latitude+" , "+a.longitude;
        String dest = "destination="+ b.latitude+" , "+b.longitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String param = org+"&"+dest+"&"+sensor+"&"+mode;

        String out = "json";

        String url = "https://maps.googleapis.com/maps/directios/"+out+"?"+param;
        return url;
    }

    private  String requestDirection(String s) throws IOException {
        String resposndString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        try {
            URL url = new URL(s);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new  InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            resposndString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null){
                inputStream.close();
        }
            return  resposndString;

        }
    }


    public  class TaskRequestDirection extends AsyncTask<String, Void ,String>{

        @Override
        protected String doInBackground(String... strings) {

            String reposeString = "";

            try {
                reposeString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }


            return reposeString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

//    public List<List<HashMap<String,String>>> parse(JSONObject jObject){
//
//        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;
//        JSONArray jRoutes = null;
//        JSONArray jLegs = null;
//        JSONArray jSteps = null;
//
//        try {
//
//            jRoutes = jObject.getJSONArray("routes");
//
//            /** Traversing all routes */
//            for(int i=0;i<jRoutes.length();i++){
//                jLegs = ( (JSONArray)jRoutes.get(i)).getJSONArray("legs");
//                List path = new ArrayList<HashMap<String, String>>();
//
//                /** Traversing all legs */
//                for(int j=0;j<jLegs.length();j++){
//                    jSteps = ( (JSONArray)jLegs.get(j)).getJSONArray("steps");
//
//                    /** Traversing all steps */
//                    for(int k=0;k<jSteps.length();k++){
//
//                        String html_instructions = jSteps.get(k).getString("html_instructions");
//                        String travel_mode = jSteps.get(k).getString("travel_mode");
//                        String maneuver = jSteps.get(k).getString("maneuver");
//
//                        String distance_text = jSteps.get(k).getJSONObject("distance").getString("text");
//                        String distance_value = jSteps.get(k).getJSONObject("distance").getString("value");
//
//                        String duration_text = jSteps.get(k).getJSONObject("duration").getString("text");
//                        String duration_value = jSteps.get(k).getJSONObject("duration").getString("value");
//
//                        String start_lat = jSteps.get(k).getJSONObject("start_location").getString("lat");
//                        String start_lon = jSteps.get(k).getJSONObject("start_location").getString("lng");
//
//                        String end_lat = jSteps.get(k).getJSONObject("end_location").getString("lat");
//                        String end_lon = jSteps.get(k).getJSONObject("end_location").getString("lng");
//
//                        String polyline = "";
//                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
//                        List<LatLng> list = decodePoly(polyline);
//
//
//                        /** Traversing all points */
//                        for(int l=0;l<list.size();l++){
//                            HashMap<String, String> hm = new HashMap<String, String>();
//                            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
//                            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
//                            path.add(hm);
//                        }
//                    }
//                    routes.add(path);
//                }
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }catch (Exception e){
//        }
//
//
//        return routes;
//    }
//
//
//
//
//
//    public static Route parseRoute(java.lang.String response)
//            throws org.json.JSONException {
//        JSONObject route = new JSONObject(response);
//        Route routeReturn = new Route();
//
//        JSONArray legs = route.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
//        for (int j = 0; j < legs.length(); j++) {
//            JSONObject leg = legs.getJSONObject(j);
//            Leg leg1 = new Leg();
//
//            int distance = leg.getJSONObject("distance").getInt("value");
//            leg1.setDistance(distance);
//            routeReturn.addLeg(leg1);
//
//            JSONArray steps = route.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(j).getJSONArray("steps");
//            for (int k = 0; k < steps.length(); k++) {
//                JSONObject step = steps.getJSONObject(k);
//                String polyline = step.getJSONObject("polyline").getString("points");
//
//                List<LatLng> latLngs = PolylineDecoder.decodePoly(polyline);
//
//                leg1.addAllPoints(latLngs);
//            }
//
//
//        }
//        return routeReturn;
//    }

/*
    public class DirectionsJSONParser {
        String tag = "DirectionsJSONParser";


//          Receives a JSONObject and returns a GDirection
//
//          @param jObject
//                     The Json to parse
//         *@return The GDirection defined by the JSon Object

        public List<GDirection> parse(JSONObject jObject) {
            // The returned direction
            List<GDirection> directionsList = null;
            // The current GDirection
            GDirection currentGDirection = null;
            // The legs
            List<GDLegs> legs = null;
            // The current leg
            GDLegs currentLeg = null;
            // The paths
            List<GDPath> paths = null;
            // The current path
            GDPath currentPath = null;
            // JSON Array representing Routes
            JSONArray jRoutes = null;
            JSONObject jRoute;
            JSONObject jBound;
            // JSON Array representing Legs
            JSONArray jLegs = null;
            JSONObject jLeg;
            // JSON Array representing Step
            JSONArray jSteps = null;
            JSONObject jStep;
            String polyline = "";
            try {
                jRoutes = jObject.getJSONArray("routes");
                Log.v(tag, "routes found : " + jRoutes.length());
                directionsList = new ArrayList<GDirection>();
                /** Traversing all routes */
//                for (int i = 0; i < jRoutes.length(); i++) {
//                    jRoute=(JSONObject) jRoutes.get(i);
//                    jLegs = jRoute.getJSONArray("legs");
//                    Log.v(tag, "routes[" + i + "]contains jLegs found : " + jLegs.length());
//                    /** Traversing all legs */
//                    legs = new ArrayList<GDLegs>();
//                    for (int j = 0; j < jLegs.length(); j++) {
//                        jLeg=(JSONObject) jLegs.get(j);
//                        jSteps = jLeg.getJSONArray("steps");
//                        Log.v(tag, "routes[" + i + "]:legs[" + j + "] contains jSteps found : " + jSteps.length());
//                        /** Traversing all steps */
//                        paths = new ArrayList<GDPath>();
//                        for (int k = 0; k < jSteps.length(); k++) {
//                            jStep = (JSONObject) jSteps.get(k);
//                            polyline = (String) ((JSONObject) (jStep).get("polyline")).get("points");
//                            // Build the List of GDPoint that define the path
//                            List<GDPoint> list = decodePoly(polyline);
//                            // Create the GDPath
//                            currentPath = new GDPath(list);
//                            currentPath.setDistance(((JSONObject)jStep.get("distance")).getInt("value"));
//                            currentPath.setDuration(((JSONObject)jStep.get("duration")).getInt("value"));
//                            currentPath.setHtmlText(jStep.getString("html_instructions"));
//                            currentPath.setTravelMode(jStep.getString("travel_mode"));
//                            Log.v(tag,
//                                    "routes[" + i + "]:legs[" + j + "]:Step[" + k + "] contains Points found : "
//                                            + list.size());
//                            // Add it to the list of Path of the Direction
//                            paths.add(currentPath);
//                        }
//                        //
//                        currentLeg = new GDLegs(paths);
//                        currentLeg.setmDistance(((JSONObject)jLeg.get("distance")).getInt("value"));
//                        currentLeg.setmDuration(((JSONObject)jLeg.get("duration")).getInt("value"));
//                        currentLeg.setmEndAddress(jLeg.getString("end_address"));
//                        currentLeg.setmStartAddress(jLeg.getString("start_address"));
//                        legs.add(currentLeg);
//
//                        Log.v(tag, "Added a new Path and paths size is : " + paths.size());
//                    }
//                    // Build the GDirection using the paths found
//                    currentGDirection = new GDirection(legs);
//                    jBound=(JSONObject)jRoute.get("bounds");
//                    currentGDirection.setmNorthEastBound(new LatLng(
//                            ((JSONObject)jBound.get("northeast")).getDouble("lat"),
//                            ((JSONObject)jBound.get("northeast")).getDouble("lng")));
//                    currentGDirection.setmSouthWestBound(new LatLng(
//                            ((JSONObject)jBound.get("southwest")).getDouble("lat"),
//                            ((JSONObject)jBound.get("southwest")).getDouble("lng")));
//                    currentGDirection.setCopyrights(jRoute.getString("copyrights"));
//                    directionsList.add(currentGDirection);
//                }
//
//            } catch (JSONException e) {
//                Log.e(tag, "Parsing JSon from GoogleDirection Api failed, see stack trace below:", e);
//            } catch (Exception e) {
//                Log.e(tag, "Parsing JSon from GoogleDirection Api failed, see stack trace below:", e);
//            }
//            return directionsList;
//        }
//
//        /**
//         * Method to decode polyline points
//         * Courtesy :
//         * http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction
//         * -api-with-java
//         */
//        private List<GDPoint> decodePoly(String encoded) {
//
//            List<GDPoint> poly = new ArrayList<GDPoint>();
//            int index = 0, len = encoded.length();
//            int lat = 0, lng = 0;
//
//            while (index < len) {
//                int b, shift = 0, result = 0;
//                do {
//                    b = encoded.charAt(index++) - 63;
//                    result |= (b & 0x1f) << shift;
//                    shift += 5;
//                } while (b >= 0x20);
//                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
//                lat += dlat;
//
//                shift = 0;
//                result = 0;
//                do {
//                    b = encoded.charAt(index++) - 63;
//                    result |= (b & 0x1f) << shift;
//                    shift += 5;
//                } while (b >= 0x20);
//                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
//                lng += dlng;
//                poly.add(new GDPoint((double) lat / 1E5, (double) lng / 1E5));
//            }
//
//            return poly;
//        }
//
//
//
//
}
