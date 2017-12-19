package com.example.dell.smartcitytraveller_app;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

// Import Google API Client
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
// Maps API Imports
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionApi;
import com.google.android.gms.location.places.PlaceFilter;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
// Location & Places API imports
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.Places;
// Others imports
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, GoogleMap.OnInfoWindowClickListener {


    /** ********************************************************************************************
     *  Variables declaration
     * *********************************************************************************************
     */

    // Name of the class
    private static final String TAG = MapActivity.class.getSimpleName();
    // Variable to use when a reference to the current activity is needed as a parameter in a nested method (in this case "this" does not refer to the current activity)
    Activity thisActivity = this;

    // The entry points to the Maps & Places API.
    private GoogleMap mMap;                     // Google maps object
    private GoogleApiClient mGoogleApiClient;   // Connexion to google maps API
    private GeoDataClient mGeoDataClient;       // Gives access to places API database
    private PlaceDetectionClient mPlaceDetectionClient; // Allow to find the places around the user
    private FusedLocationProviderClient mFusedLocationProviderClient; // An object to retrieve current position

    // A flag to indicates if the access to location was granted (if no we will ask for permission, if refused we will use a default position)
    private boolean mLocationPermissionGranted;
    // Identifiers for the location rights requests (usen to identify the response corresponding to this request in the onRequestPermissionsResult method
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1; // When the request is made during the first load of the class
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2; // If the request has been refused during initialisation and that an other request is needed

    // The geographical location where the device is currently located. That is, the last-known location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;
    // Variable to store the current position of the camera (= display window)
    private CameraPosition mCameraPosition;
    // Default position for the Camera (Paris)
    private final LatLng mDefaultLocation = new LatLng(48.864716, 	2.349014);
    //Default zoom for the camera
    private static final int DEFAULT_ZOOM = 16;
    // Keys for storing activity state (saving the last known place and the current camera position)
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Identifier for the answer to a place picker request
    int PLACE_PICKER_REQUEST = 3;
    // Place Picker elements, used to launch a google "place picker" activity
    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

    // Variable defining the accessible Kms around the user
    double accessibleKmsAroundUser;
    // User ID
    int userID;
    // Flags to know if the user wants to see monuments or restaurants in its code
    boolean monumentsAllowed;
    boolean restaurantsAllowed;

    // Flag to know if the autocomplete search bar has already been initialized
    boolean isSearchbarInitialized = false;

    // Marker to show the results of a request in the autocomplete search bar
    private Marker researchResponseMarker;

    // Markers for all the 20 possible results of the request of places around the user
    // Creating 20 different values is not very "clean" but dynamically creating variables or using list was not working
    private Marker m1;
    private Marker m2;
    private Marker m3;
    private Marker m4;
    private Marker m5;
    private Marker m6;
    private Marker m7;
    private Marker m8;
    private Marker m9;
    private Marker m10;
    private Marker m11;
    private Marker m12;
    private Marker m13;
    private Marker m14;
    private Marker m15;
    private Marker m16;
    private Marker m17;
    private Marker m18;
    private Marker m19;
    private Marker m20;




    /** ********************************************************************************************
     *  Function called during the launch of the activity
     *  ********************************************************************************************
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Render the layout (containing the map)
        setContentView(R.layout.activity_map);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Retrieve the nb of kilometers the user can walk from the intent that launched the current class
        accessibleKmsAroundUser = getIntent().getIntExtra("accessibleKms", 2);
        // Retrieve user id
        userID = getIntent().getIntExtra("id", 0);
        // Retrieve preferences on monuments
        monumentsAllowed = getIntent().getBooleanExtra("monuments", true);
        // Retrieve preferences on restaurants
        restaurantsAllowed = getIntent().getBooleanExtra("restaurants", true);

        //Construct a GeoDataClient. (to give access to the Places API database)
        mGeoDataClient = Places.getGeoDataClient(this, null);
        // Construct a PlaceDetectionClient. (to allow to find most likely current place)
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        // Construct a FusedLocationProviderClient. (to find the current position of the user)
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        /** **************************************************
         *  Setting up the toolbar
         *  **************************************************
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // To add a return button (arrow) on the left of the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Smart City Traveller");

        /** ***************************************************
         *  Obtain the SupportMapFragment and get notified when the map is ready to be used.
         *  ***************************************************
         */
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /** ***************************************************
         *  Setting up the action to perform when the little round floating button is clicked
         *  (for the moment display a toast, but it should be used to get current place and set the camera at a proper zoom on it)
         *  Obtain the SupportMapFragment and get notified when the map is ready to be used.
         *  ***************************************************
         */
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        /** ***************************************************
         *  Creating an instance of the google API client
         *  ***************************************************
         */
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
    }


    /** ********************************************************************************************
     * When the user comes (back) to this activity, we (re)connects the API to the Maps API.
     * *********************************************************************************************
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }


    /** ********************************************************************************************
     * When the user switches to an other Applcation/Activity, we disconnect the activity from the Maps API.
     * *********************************************************************************************
     */
    @Override
    protected void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }


    /** ********************************************************************************************
     * Saves the state of the map when the activity is paused.
     * *********************************************************************************************
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }


    /** ********************************************************************************************
     * Method to add icons to the toolbar
     * *********************************************************************************************
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_simple, menu);
        return true;
    }


    /** ********************************************************************************************
     * Function that activates the return button on the toolbar to actually perform a return action.
     * *********************************************************************************************
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    /** ********************************************************************************************
     * Defining the actions to perform when the buttons of the toolbar are clicked
     * *********************************************************************************************
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /** ********************************************************************************************
     * Builds the map when the Google Play services client is successfully connected.
     * *********************************************************************************************
     * @param connectionHint
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /** ********************************************************************************************
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera...
     *
     * Here we will :   > set the window info element
     *                  > Get permissions if not already done
     *                  > Update the blue dot on the map identifying the user position
     *                  > Get the used location with the 'FusedLocationProviderClient' object, and set the camera to the current position with a default zoom
     *                          > The first time we call the 'getLocation' function, when its result is triggered,
     *                                  we will calculate an adapted window around the user and setup the autocomplete
     *                                  search bar to provide results only in that window (so the user won't be messed up
     *                                  with information about too far places were he doesn't have time to go to)
     * *********************************************************************************************
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Setting a listener to be able to handle clicks on a
        mMap.setOnInfoWindowClickListener(this);

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents. (the little windows that appears when we click on a marker on the map)
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents, (FrameLayout) findViewById(R.id.map), false);

                TextView title = ((TextView) infoWindow.findViewById(R.id.info_title));
                title.setText(marker.getTitle());

                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

        // Prompt the user for permissions (for accessing location).
        getLocationPermission();

        // Turn on the 'My Location' layer = the blue dot indicating the current user position
        // and the circle around indicating the accuracy of the location.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        setMarkersAroundUser();

    }


    /** ********************************************************************************************
     * Updates the map's UI settings based on whether the user has granted location permission.
     * *********************************************************************************************
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    /** ********************************************************************************************
     * Gets the current location of the device, and positions the map's camera.
     * > The first time this function is called, when its result is triggered,
     *   we will calculate an adapted window around the user and setup the autocomplete
     *   search bar to provide results only around that window (so the user won't be messed up
     *   with information about too far places were he doesn't have time to go to)
     * *********************************************************************************************
     */
    private void getDeviceLocation() {
        /*****************************************
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         * Here we se the FusedLocationProviderClient to get the current position
         * ***************************************
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            // The 'animateCamera' makes the move smoother than the 'moveCamera' function
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                            // Now that the current position is initialised, we can calculated the appropriate window around the user and set the search bar on that window
                            if(!isSearchbarInitialized){
                                initializeSearchBar();
                                isSearchbarInitialized = true;
                            }

                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    /** ********************************************************************************************
     * Prompts the user for permission to use the device location.
     * *********************************************************************************************
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    /** ********************************************************************************************
     * Method called when the result of a permission request is detected.
     * We can then define the action to do when the permission asked was granted, or when it was refused
     * *********************************************************************************************
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the location  task you need to do.
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                }
                return;
            }
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
        updateLocationUI();
    }


    /** ********************************************************************************************
     * Method called when an element is clicked (the "view" element that was clicked is given as a parameter)
     * This methods needs to identify which element was clicked (by comparing the id of the clicked element with known IDs)
     * Depending on the element clicked we can then define the action to perform.
     * *********************************************************************************************
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            //when the round button a the bottom of the screen is clicked
            case R.id.fab:
                //displayToast("Floating action button clicked");
                updateLocationUI();
                getDeviceLocation();
                break;
        }
    }


    /** ********************************************************************************************
     * Method from abstract parent classes onConnectionFailedListener and onMapReadyCallback that must be implemented
     * (But i don't know what to put inside)
     * *********************************************************************************************
     */
    @Override
    public void onConnectionFailed(ConnectionResult result){
        displayToast("Connection failed");
    }
    @Override
    public void onConnectionSuspended(int i) {
        displayToast("Connection  suspended");
    }


    /** ********************************************************************************************
     *  Method is used to display the message in proper manner
     *  ********************************************************************************************"
     * @param message
     */
    private void displayToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();

    }


    /** ********************************************************************************************
     * This method needed to be called but i don't know what to put inside
     * *********************************************************************************************
     * @param hasCapture
     */
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    /** ********************************************************************************************
     *  Defines the bound of a square window around the user (x Kms wide in each direction).
     *  This windows is calculated to contain places accessible in a given amount of Kms to walk (that will be calculated given the amount of time available).
     *  A red square is drawn on the map to represent the window in which the user can search a place.
     *  ********************************************************************************************"
     */
    private LatLngBounds accessibleWindowAroundUser(double accessibleKms){
        System.out.println("Hello, calculating the window ...");

        // Getting the current position from default location (in the case the current location is null)
        double currentLat = mDefaultLocation.latitude;
        double currentLong = mDefaultLocation.longitude;

        if(mLastKnownLocation!=null){
            // use the real location if available
            currentLat = mLastKnownLocation.getLatitude();
            currentLong = mLastKnownLocation.getLongitude();
            System.out.println("Current position was not null when calculating the window around the user !");
        }else{
            System.out.println("Current position was null when calculating the window around the user...");
        }

        // Calculating the Latitude to add to the current position to set se position to 1km north of the current position
        double LatForOneKm = (1/110.574);
        // Calculating the Longitude to add to the current position to set se position to 1km east of the current position
        //  (depends on the latitude we are at, for this calculus we will approximate the Latitude to be approximately the same within our window around the user)
        double LongForOneKm =(1/(111.320*Math.cos(Math.toRadians(currentLat))));

        // Calculating the Bottom Left (South West) and Top Right (North East) bounds of our window around the user, given the amount of kms we think he can walk
        LatLng windowSouthWestBound = new LatLng(currentLat-accessibleKms*LatForOneKm, currentLong-accessibleKms*LongForOneKm);
        LatLng windowNorthEastBound = new LatLng(currentLat+accessibleKms*LatForOneKm, currentLong+accessibleKms*LongForOneKm);

        //mMap.addMarker(new MarkerOptions().position(windowSouthWestBound).title("Window south west bound"));
        //mMap.addMarker(new MarkerOptions().position(windowNorthEastBound).title("Window north east bound"));

        // Instantiates a new Polygon object and adds points to define a square representing the window defined around the user
        // Calculating the two other points
        LatLng windowNorthWestBound = new LatLng(currentLat+accessibleKms*LatForOneKm, currentLong-accessibleKms*LongForOneKm);
        LatLng windowSouthEastBound = new LatLng(currentLat-accessibleKms*LatForOneKm, currentLong+accessibleKms*LongForOneKm);
        PolygonOptions windowOptions = new PolygonOptions().add(windowSouthWestBound, windowNorthWestBound, windowNorthEastBound, windowSouthEastBound, windowSouthWestBound);
        windowOptions.strokeColor(Color.RED).strokeWidth(8);
        // Get back the mutable Polygon
        Polygon polygon = mMap.addPolygon(windowOptions);

        // Finally the objects that defines the bounds of our accessible window around the user
        LatLngBounds windowAroudUser = new LatLngBounds(windowSouthWestBound, windowNorthEastBound);
        return windowAroudUser;
    }


    /** ********************************************************************************************
     *  Method that retrieve from google Places API the 20 closest places from the user (we can't get
     *  more results for a request, the web API could go up to 60 results but not the android API).
     *
     *  The results are then filtered, and for the results that have a type among the ones we want
     * *********************************************************************************************
     */
    private void setMarkersAroundUser(){
        // Here the location needs to be enabled, if is not the case we ask it (but they should already have been asked during the first launch f the class)
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("ACCESS_FINE_LOCATION permission missing");
            ActivityCompat.requestPermissions(thisActivity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }//else{

        // Look for the Places around the user
        List<String> filters=new ArrayList<>();
        filters.add(String.valueOf(Place.TYPE_STREET_ADDRESS));

        PlaceFilter placeFilter = new PlaceFilter(true,filters);


        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient,null );
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                System.out.println("onResult called ");
                boolean cameraMoved = false;
                int i=1;
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    float placeLkyhd = placeLikelihood.getLikelihood();
                    Place place = placeLikelihood.getPlace();
                    LatLng placeLatLng = place.getLatLng();

                    String placeName = (String) place.getName();
                    List<Integer> placeTypes = place.getPlaceTypes();

                    String typeslist = "";
                    for(int type : placeTypes){
                        typeslist=typeslist+";"+type;
                    }
                    System.out.println("Place : "+placeName+" has likelyhood : "+placeLkyhd+" and types : "+typeslist);
                    //Log.i(TAG, String.format("Place '%s' has likelihood: %g",placeLikelihood.getPlace().getName(),placeLikelihood.getLikelihood()));

                    // Checking if the types of the places we just found match with the places we want to display.
                    // If yes we display a marker
                    if(PlaceTypesAreWanted(placeTypes)) {
                        // The following lines are not really "clean" but i didn't found a better way to to that (lists or dynamically created variables were not working)
                        switch (i) {
                            case 1:
                                m1 = mMap.addMarker(new MarkerOptions().position(placeLatLng).title(placeName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).snippet("Click for more details ..."));
                                m1.setTag(new CustomTag(place));
                                break;
                            case 2:
                                m2 = mMap.addMarker(new MarkerOptions().position(placeLatLng).title(placeName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).snippet("Click for more details ..."));
                                m2.setTag(new CustomTag(place));
                                break;
                            case 3:
                                m3 = mMap.addMarker(new MarkerOptions().position(placeLatLng).title(placeName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).snippet("Click for more details ..."));
                                m3.setTag(new CustomTag(place));
                                break;
                            case 4:
                                m4 = mMap.addMarker(new MarkerOptions().position(placeLatLng).title(placeName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).snippet("Click for more details ..."));
                                m4.setTag(new CustomTag(place));
                                break;
                            case 5:
                                m5 = mMap.addMarker(new MarkerOptions().position(placeLatLng).title(placeName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).snippet("Click for more details ..."));
                                m5.setTag(new CustomTag(place));
                                break;
                            case 6:
                                m6 = mMap.addMarker(new MarkerOptions().position(placeLatLng).title(placeName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).snippet("Click for more details ..."));
                                m6.setTag(new CustomTag(place));
                                break;
                            case 7:
                                m7 = mMap.addMarker(new MarkerOptions().position(placeLatLng).title(placeName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).snippet("Click for more details ..."));
                                m7.setTag(new CustomTag(place));
                                break;
                            case 8:
                                m8 = mMap.addMarker(new MarkerOptions().position(placeLatLng).title(placeName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).snippet("Click for more details ..."));
                                m8.setTag(new CustomTag(place));
                                break;
                            case 9:
                                m9 = mMap.addMarker(new MarkerOptions().position(placeLatLng).title(placeName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).snippet("Click for more details ..."));
                                m9.setTag(new CustomTag(place));
                                break;
                            case 10:
                                m10 = mMap.addMarker(new MarkerOptions().position(placeLatLng).title(placeName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).snippet("Click for more details ..."));
                                m10.setTag(new CustomTag(place));
                                break;
                            case 11:
                                m11 = mMap.addMarker(new MarkerOptions().position(placeLatLng).title(placeName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).snippet("Click for more details ..."));
                                m11.setTag(new CustomTag(place));
                                break;
                            case 12:
                                m12 = mMap.addMarker(new MarkerOptions().position(placeLatLng).title(placeName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).snippet("Click for more details ..."));
                                m12.setTag(new CustomTag(place));
                                break;
                            case 13:
                                m13 = mMap.addMarker(new MarkerOptions().position(placeLatLng).title(placeName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).snippet("Click for more details ..."));
                                m13.setTag(new CustomTag(place));
                                break;
                            case 14:
                                m14 = mMap.addMarker(new MarkerOptions().position(placeLatLng).title(placeName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).snippet("Click for more details ..."));
                                m14.setTag(new CustomTag(place));
                                break;
                            case 15:
                                m15 = mMap.addMarker(new MarkerOptions().position(placeLatLng).title(placeName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).snippet("Click for more details ..."));
                                m15.setTag(new CustomTag(place));
                                break;
                            case 16:
                                m16 = mMap.addMarker(new MarkerOptions().position(placeLatLng).title(placeName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).snippet("Click for more details ..."));
                                m16.setTag(new CustomTag(place));
                                break;
                            case 17:
                                m17 = mMap.addMarker(new MarkerOptions().position(placeLatLng).title(placeName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).snippet("Click for more details ..."));
                                m17.setTag(new CustomTag(place));
                                break;
                            case 18:
                                m18 = mMap.addMarker(new MarkerOptions().position(placeLatLng).title(placeName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).snippet("Click for more details ..."));
                                m18.setTag(new CustomTag(place));
                                break;
                            case 19:
                                m19 = mMap.addMarker(new MarkerOptions().position(placeLatLng).title(placeName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).snippet("Click for more details ..."));
                                m19.setTag(new CustomTag(place));
                                break;
                            case 20:
                                m20 = mMap.addMarker(new MarkerOptions().position(placeLatLng).title(placeName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).snippet("Click for more details ..."));
                                m20.setTag(new CustomTag(place));
                                break;
                        }
                    }
                    i++;
                }
                likelyPlaces.release();
            }
        });
        //}

    }


    /** ********************************************************************************************
     * Method that returns "true" is a place has a type is the following list of possible place types :
     *      TYPE_RESTAURANT     : value = 79
     *      TYPE_FOOD           : value = 38
     * Monuments doesn't exist in places database, for 'Monuments' we could use the following elements :
     *      TYPE_AQUARIUM       : value = 4
     *      TYPE_ART_GALLERY    : value = 5
     *      TYPE_CEMETERY       : value = 22
     *      TYPE_CHURCH         : value = 23
     *      TYPE_CITY_HALL      : value = 24
     *      TYPE_EMBASSY        : value = 33
     *      TYPE_HINDU_TEMPLE   : value = 48
     *      TYPE_MOSQUE         : value = 62
     *      TYPE_MUSEUM         : value = 66
     *      TYPE_PARK           : value = 69
     *	    TYPE_SYNAGOGUE      : value = 90
     *      TYPE_TRAVEL_AGENCY  : value = 93
     *  ********************************************************************************************
     * @param placeTypes
     * @return
     */
    private boolean PlaceTypesAreWanted(List<Integer> placeTypes){
        List<Integer> wantedTypes = new ArrayList<>();
        if(monumentsAllowed && restaurantsAllowed) {
            wantedTypes = new ArrayList<>(Arrays.asList(79, 38, 4, 5, 22, 23, 24, 33, 48, 62, 66, 69, 90, 93));
        }
        if(monumentsAllowed && !restaurantsAllowed){
            wantedTypes = new ArrayList<>(Arrays.asList( 4, 5, 22, 23, 24, 33, 48, 62, 66, 69, 90, 93));
        }
        if(!monumentsAllowed && restaurantsAllowed){
            wantedTypes = new ArrayList<>(Arrays.asList( 79, 38));
        }
        boolean areCurrentTypesWanted = false;
        // for each of the times of the given place, we check if it matches with one of the types we want, if yes the function will return tru and the place will be displayed
        for(int type : placeTypes){
            if(wantedTypes.contains(type)){
                areCurrentTypesWanted = true;
            }
        }
        return areCurrentTypesWanted;
    }


    /** ********************************************************************************************
     *  Allowing the use of the results of the "Autocomplete" Places search bar
     *   The search bar is set here to retrieve only results that are of type 'Establishment'
     *      and only establishments that are around the adapted window around the user (set by the 'accessibleWindowAroundUser' method) can be searched
     *
     *   When a Place is selected through the search bar, display a marker on its position on the map. The info window of the marker is automatically activated.
     *   The camera is then centered on this position.
     *  ********************************************************************************************
     */
    private void initializeSearchBar(){
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        // Setting bounds for the results of the requests (unsing our function to find adapted bounds around the user)
        autocompleteFragment.setBoundsBias(accessibleWindowAroundUser(accessibleKmsAroundUser));
        // Setting a filter for the places type
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT).build();
        // The only filter available for android Places API are :
        //TYPE_FILTER_NONE ,TYPE_FILTER_GEOCODE, TYPE_FILTER_ADDRESS, TYPE_FILTER_ESTABLISHMENT, TYPE_FILTER_REGIONS, TYPE_FILTER_CITIES
        // So to get a more precise filter we need filter the results of the query ourselves
        autocompleteFragment.setFilter(typeFilter);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(Place place) {
                //Log.i(TAG, "Place: " + place.getName());

                // When a place is selected in the Serach bar, a marker is added to the map
                researchResponseMarker = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title((String)place.getName()).snippet("Click for more details ..."));
                researchResponseMarker.setTag(new CustomTag(place));
                researchResponseMarker.showInfoWindow();

                // And the camera is moved to this place
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), DEFAULT_ZOOM));


            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.Log.i(TAG, "An error occurred: " + status);
            }
        });
    }


    /** ********************************************************************************************
     *  Setting up a custom tag object that also lets the possibility to store the Place object associated with it
     *  ********************************************************************************************
     */
    private static class CustomTag {
        private final Place place;

        public CustomTag(Place givenPlace) {
            this.place = givenPlace;
        }

        public Place getPlace(){
            return place;
        }

    }


    /** ********************************************************************************************
     *  When the info window associated with a tag is clicked, retrieve the associated place and
     *  launches a new 'place details' activity to show its content.
     * *********************************************************************************************
     * @param marker
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        CustomTag clickedTag = (CustomTag) marker.getTag();
        Place associatedPlace = clickedTag.getPlace();
        Intent placeDetails = new Intent(this, PlaceDetailsActivity.class);
        placeDetails.putExtra("placeID",associatedPlace.getId());
        startActivity(placeDetails);
    }

}
