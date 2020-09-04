package com.sp.gold.Fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPointStyle;
import com.sp.gold.R;
import com.sp.gold.Utils.GPSTracker;
import com.sp.gold.Utils.MapViewModel;

import org.json.JSONException;

import java.io.IOException;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;

public class MapFragment extends Fragment {
    private static final String TAG = "MapFragment";

    private GeoJsonLayer ccLayer, sacLayer;
    private GPSTracker gpsTracker;
    private GoogleMap mMap;

    protected LocationManager locationManager;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;

    private int myZoom = 14;
    private double myLat = 1.3521;
    private double myLon = 103.8198;
    private boolean isCC = false, isSAC = false;
    private LatLng myLatLng, newLatLng;
    private Marker myMarker, newMarker;

    private com.github.clans.fab.FloatingActionButton ccFab, sacFab, resetFab;
    private FloatingActionMenu menuFab;
    private FloatingActionButton fab;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            mMap.getUiSettings().setMapToolbarEnabled(false);

            checkModel();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gpsTracker = new GPSTracker(getContext());

        Log.d(TAG, "onViewCreated: tested");

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        fab = view.findViewById(R.id.loc_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gpsTracker.getLocation();
                findMyLocation();
            }
        });

        resetFab = view.findViewById(R.id.reset_fab);
        ccFab = view.findViewById(R.id.cc_fab);
        sacFab = view.findViewById(R.id.sac_fab);
        menuFab = view.findViewById(R.id.menu_fab);

        resetFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                menuFab.close(true);

                if (sacLayer != null) {
                    sacLayer.removeLayerFromMap();
                    isSAC = false;
                }

                if (ccLayer != null) {
                    ccLayer.removeLayerFromMap();

                    isCC = false;
                }

                if (newMarker != null) {
                    newMarker.remove();
                }

                sacLayer = null;
                ccLayer = null;
                newMarker = null;

                mMap.setMapStyle(null);
                findMyLocation();
            }
        });

        ccFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                menuFab.close(true);

                if (!isCC) {
                    mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.clean_map));

                    if (sacLayer != null) {
                        sacLayer.removeLayerFromMap();
                        isSAC = false;
                    }

                    try {
                        ccLayer = new GeoJsonLayer(mMap, R.raw.community_clubs_geojson, getContext());
                        final GeoJsonPointStyle style = ccLayer.getDefaultPointStyle();
                        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.round_place_white_48);
                        Bitmap res = changeBitmapColor(bitmap, Color.parseColor("#009CDF"));
                        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(res);
                        style.setIcon(descriptor);

                        ccLayer.addLayerToMap();
                        isCC = true;

                        ccLayer.setOnFeatureClickListener(new GeoJsonLayer.OnFeatureClickListener() {
                            @Override
                            public void onFeatureClick(Feature feature) {
                                Toast.makeText(getContext(), feature.getProperty("Name"), Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ccLayer.removeLayerFromMap();
                    mMap.setMapStyle(null);
                    isCC = false;
                }
            }
        });

        sacFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuFab.close(true);

                if (!isSAC) {
                    mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.clean_map));

                    if (ccLayer != null) {
                        ccLayer.removeLayerFromMap();
                        isCC = false;
                    }

                    try {
                        sacLayer = new GeoJsonLayer(mMap, R.raw.senior_clubs_geojson, getContext());
                        GeoJsonPointStyle style = sacLayer.getDefaultPointStyle();
                        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.round_place_white_48);
                        Bitmap res = changeBitmapColor(bitmap, Color.parseColor("#5EBD3E"));
                        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(res);
                        style.setIcon(descriptor);

                        sacLayer.addLayerToMap();
                        isSAC = true;

                        sacLayer.setOnFeatureClickListener(new GeoJsonLayer.OnFeatureClickListener() {
                            @Override
                            public void onFeatureClick(Feature feature) {
                                Toast.makeText(getContext(), feature.getProperty("Name"), Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    sacLayer.removeLayerFromMap();
                    mMap.setMapStyle(null);
                    isSAC = false;
                }
            }
        });
    }

//                for (GeoJsonFeature feature : ccLayer.getFeatures()) {
//                    if (feature.hasProperty("Name")) {
//                        String name = feature.getProperty("Name");
//                        style.setTitle(name);
//                    }
//                }

//                try {
//                    KmlLayer layer = new KmlLayer(mMap, R.raw.community_clubs_kml, getContext());
//                    layer.addLayerToMap();
//                } catch (XmlPullParserException | IOException e) {
//                    e.printStackTrace();
//                }

    @Override
    public void onDestroy() {
        super.onDestroy();
        gpsTracker.stopUsingGPS();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMap != null) {
            checkModel();
        }
    }

    private void checkModel() {
        MapViewModel model = new ViewModelProvider(requireActivity()).get(MapViewModel.class);
        if (model.isCC()) {
            String name = model.getName();
            if (name != null) {
                findCC(name);
            }
        } else {
            findMyLocation();
        }
    }

    private void addMarker(double lat, double lon) {
        if (myMarker != null)
            myMarker.remove();

        LatLng latLng = new LatLng(lat, lon);
        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.round_person_pin_circle_white_48);
        Bitmap res = changeBitmapColor(bitmap, Color.parseColor("#E23838"));
        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(res);
        myMarker = mMap.addMarker(new MarkerOptions().position(latLng).icon(descriptor));
    }

    private void addNewMarker(double lat, double lon) {
        if (newMarker != null)
            newMarker.remove();

        newLatLng = new LatLng(lat, lon);
        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.round_place_white_48);
        Bitmap res = changeBitmapColor(bitmap, Color.parseColor("#973999"));
        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(res);
        newMarker = mMap.addMarker(new MarkerOptions().position(newLatLng).icon(descriptor));
    }

    private void moveToLocation(double lat, double lon, int zoom) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(lat, lon))
                .zoom(zoom)
                .bearing(0)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void moveToBounds(LatLng latLng1, LatLng latLng2) {
        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include(latLng1)
                .include(latLng2)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 120));
    }

    private void findMyLocation() {
        checkGPSandNetwork();
        if (isGPSEnabled && isNetworkEnabled) {
            gpsTracker.getLocation();

            if (gpsTracker.canGetLocation()) {
                myLat = gpsTracker.getLatitude();
                myLon = gpsTracker.getLongitude();
                myZoom = 17;

                addMarker(myLat, myLon);
            }
        }

        if (newMarker != null && newMarker.isVisible()) {
            myLatLng = new LatLng(myLat, myLon);
            moveToBounds(myLatLng, newLatLng);
        } else {
            moveToLocation(myLat, myLon, myZoom);

        }
    }

    private void checkGPSandNetwork() {
        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private Bitmap changeBitmapColor(Bitmap sourceBitmap, int color) {
        Bitmap resultBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0,
                sourceBitmap.getWidth() - 1, sourceBitmap.getHeight() - 1);
        Paint p = new Paint();
        ColorFilter filter = new LightingColorFilter(color, 1);
        p.setColorFilter(filter);

        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, p);

        return resultBitmap;
    }

    public void findCC(String name) {
        try {
            ccLayer = new GeoJsonLayer(mMap, R.raw.community_clubs_geojson, getContext());
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.clean_map));

            for (GeoJsonFeature feature : ccLayer.getFeatures()) {
                if (feature.hasProperty("Name")) {
                    String nameStr = feature.getProperty("Name");
                    if (nameStr.equals(name)) {
                        String coordinates = feature.getGeometry().getGeometryObject().toString();
                        int coorLen = coordinates.length();
                        String coorSubStr = coordinates.substring(10, coorLen-1);
                        String[] coors = coorSubStr.split(",");
                        double coorLat = Double.parseDouble(coors[0]);
                        double coorLon = Double.parseDouble(coors[1]);

                        moveToLocation(coorLat, coorLon, 17);
                        addNewMarker(coorLat, coorLon);

                        break;
                    }
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public void findSAC(String name) {
        try {
            sacLayer = new GeoJsonLayer(mMap, R.raw.senior_clubs_geojson, getContext());
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.clean_map));

            for (GeoJsonFeature feature : sacLayer.getFeatures()) {
                if (feature.hasProperty("Name")) {
                    String nameStr = feature.getProperty("Name");
                    if (nameStr.equals(name)) {
                        String coordinates = feature.getGeometry().getGeometryObject().toString();
                        int coorLen = coordinates.length();
                        String coorSubStr = coordinates.substring(10, coorLen-1);
                        String[] coors = coorSubStr.split(",");
                        double coorLat = Double.parseDouble(coors[0]);
                        double coorLon = Double.parseDouble(coors[1]);

                        moveToLocation(coorLat, coorLon, 17);
                        addNewMarker(coorLat, coorLon);

                        break;
                    }
                }
            }

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: fab");
                    getActivity().getFragmentManager().popBackStack();
                }
            });
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}