package com.gui.mdt.thongsieknavclient.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gui.mdt.thongsieknavclient.NavClientApp;
import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.ApiCustomerLocations;
import com.gui.mdt.thongsieknavclient.interfaces.NavBrokerService;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MapActivity extends AppCompatActivity implements
        OnMapReadyCallback, PermissionsListener, LocationEngineListener {

    // MapBox
    private Toolbar mToolBar;
    private TextView tvToolbarTitle;
    private Drawable mBackArrow;
    private MapView mapView;
    private Button btnStartNavigation;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Location userLocation;
    private LatLng latLng;
    private String searchLocation;
    public static final String SEARCH_TEXT = "search_text";
    public static final String ADDRESS = "address";
    private NavigationMapRoute navigationMapRoute;
    private static final String TAG = "MapActivity";
    private Point userPoint;
    private Point destinationPoint;
    private boolean isStartNavigation = false;

    private NavClientApp mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(MapActivity.this, getResources().getString(R.string.mapbox_access_code));
        setContentView(R.layout.activity_map);

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mBackArrow = new IconicsDrawable(this).icon(FontAwesome.Icon.faw_angle_left)
                .color(Color.WHITE).sizeDp(30);
        getSupportActionBar().setHomeAsUpIndicator(mBackArrow);

        mapView = (MapView) findViewById(R.id.mapView);
        btnStartNavigation = (Button) findViewById(R.id.btnStartNavigation);
        mapView.onCreate(savedInstanceState); //This is essential for mapbox to work

        mApp = (NavClientApp) getApplication();

//        LocationManager locationManager =
//                (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        searchLocation = getResources().getString(R.string.Country);
        if (getIntent() != null) {
            searchLocation = getIntent().getStringExtra(SEARCH_TEXT);
            tvToolbarTitle.setText(getIntent().getStringExtra(ADDRESS));
        }
//        searchLocation = "11250, Sri Lanka";

//        getLocation();
        getCustomerLocation("https://api.mapbox.com/geocoding/v5/mapbox.places/" + searchLocation
                + " .json?access_token="+getResources().getString(R.string.mapbox_access_code)+"",this);

//        mapView.getMapAsync(this);
        btnStartNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStartNavigation = true;
                if (userLocation != null) {
                    userPoint = Point.fromLngLat(userLocation.getLongitude(), userLocation.getLatitude());
                    setCameraPosition(userLocation);
                    getRoute(userPoint, destinationPoint);
                    btnStartNavigation.setText("Refresh");
                } else {
                    Toast.makeText(getApplicationContext(), "Please Try Again", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void getLocation() {
        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(searchLocation
                    , 5);

            for (int i = 0; i < addresses.size(); i++) { // MULTIPLE MATCHES

                Address addr = addresses.get(i);

                double latitude = addr.getLatitude();
                double longitude = addr.getLongitude();
                latLng = new LatLng(latitude, longitude);
                destinationPoint = Point.fromLngLat(longitude, latitude);
//                Toast.makeText(this, longitude + " --" + latitude, Toast.LENGTH_LONG).show();
                break;
            }

        } catch (Exception e) {
            Log.d(TAG, "search customer location" + e.getMessage());
        }

    }

    private void getCustomerLocation(String type, Context context) {
        NavBrokerService service = mApp.getNavBrokerService();
        Call mCategoryCall = service.GetCustomerCoordinate(type);

        mCategoryCall.enqueue(new Callback<ApiCustomerLocations>() {
            @Override
            public void onResponse(Call<ApiCustomerLocations> call
                    , Response<ApiCustomerLocations> response) {
                Log.d("MapActivity", "recived");

                if (response != null) {
                    if (response.body() != null) {
                        if (response.body().features != null) {
                            if (response.body().features.size() > 0) {
                                try {
                                    ArrayList<Double> cordinates = response.body().features.get(0).center;
                                    double latitude = cordinates.get(1);
                                    double longitude = cordinates.get(0);
                                    latLng = new LatLng(latitude, longitude);
                                    destinationPoint = Point.fromLngLat(longitude, latitude);
                                    mapView.getMapAsync((OnMapReadyCallback) context);
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), "Location not found", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Location not found", Toast.LENGTH_LONG).show();
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "Location not found", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Location not found", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Location not found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiCustomerLocations> call, Throwable t) {
                Log.d("MapActivity", t.getMessage());
                Toast.makeText(getApplicationContext(), "Location not found", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        MapActivity.this.mapboxMap = mapboxMap;
//        initLocationLayer();
        MarkerOptions options = new MarkerOptions();
        options.title("Customer Location");
        options.position(latLng);

        mapboxMap.addMarker(options);
        initLocationEngine();
        initLocationLayer();
//        getRoute(userPoint, destinationPoint);

    }

    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder()
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if (response.body() == null) {
                            Log.e(TAG, "check access token");
                            return;
                        } else if (response.body().routes().size() == 0) {
                            Log.e(TAG, "No routes found");
                            return;
                        }
                        DirectionsRoute directionsRoute = response.body().routes().get(0);
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap);
                        }
                        navigationMapRoute.addRoute(directionsRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Log.e(TAG, "No routes found");
                    }
                });
    }

    @Override
    public void onConnected() {
        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            userLocation = location;
            if (isStartNavigation) {
                setCameraPosition(userLocation);
                getRoute(userPoint, destinationPoint);
            }
        }
    }

    private void initLocationEngine() {
        locationEngine = new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.setInterval(5000);
        locationEngine.activate();

        Location location = locationEngine.getLastLocation();
        if (location != null) {
            userLocation = location;

            setCameraPosition(userLocation);
        } else {
            locationEngine.addLocationEngineListener(this);
        }
    }

    private void initLocationLayer() {
        locationLayerPlugin = new LocationLayerPlugin(mapView, mapboxMap, locationEngine);
        locationLayerPlugin.setLocationLayerEnabled(true);
        locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
        locationLayerPlugin.setRenderMode(RenderMode.NORMAL);
    }

    private void setCameraPosition(Location location) {
//        userPoint = Point.fromLngLat(103.7041605, 1.3327682);
        userPoint = Point.fromLngLat(location.getLongitude(), location.getLatitude());
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom
                (new LatLng(location.getLatitude(), location.getLongitude()), 13.0));
//        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom
//                (new LatLng(1.3327682, 103.7041605), 13.0));

    }

    private void enableLocation() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            initLocationEngine();
//            initLocationLayer();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocation();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationEngine != null) {
            locationEngine.deactivate();
        }
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (locationEngine != null) {
            locationEngine.requestLocationUpdates();
        }
        if (locationLayerPlugin != null) {
            locationLayerPlugin.onStart();
        }
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (locationEngine != null) {
            locationEngine.requestLocationUpdates();
        }
        if (locationLayerPlugin != null) {
            locationLayerPlugin.onStop();
        }
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public boolean onSupportNavigateUp() {
//        onBackPressed();
        finish();
        return true;
    }


}
