package com.thepraats.driverdroid;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.Manifest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    Button button;

    FirebaseDatabase database ;
    DatabaseReference ref;

    static final int REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


        button = findViewById(R.id.updateCoords);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCoordsMaps();
            }
        });

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

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    public void getCoordsMaps(){


//        Toast.makeText(MapsActivity.this,"clicked",Toast.LENGTH_SHORT).show();

        ref.child("danger_coordinates").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    double longi1 = 0.0;
                    double latti1 = 0.0;
                    for(DataSnapshot ds : dataSnapshot.getChildren()){



    //                    Toast.makeText(MapsActivity.this,ds.getKey()+"",Toast.LENGTH_SHORT).show();
                        DangerDataStore dangerDataStore = ds.getValue(DangerDataStore.class);

//                       Toast.makeText(MapsActivity.this, dangerDataStore.longitude+"",Toast.LENGTH_SHORT).show();

                        if(ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                        }
                        else {
                            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null){
                                latti1 = location.getLatitude();
                                longi1 = location.getLongitude();
//                                Toast.makeText(MapsActivity.this, String.valueOf("Lattitude: "+latti1+"  Longitude: "+longi1),Toast.LENGTH_SHORT).show();
                    /*((EditText)findViewById(R.id.etLocationLat)).setText("Latitude: " + latti);
                    ((EditText)findViewById(R.id.etlocationLong)).setText("Longitude: " + longi);*/
                            } else {
//                                Toast.makeText(MapsActivity.this,"Unable to get location coordinates",Toast.LENGTH_SHORT).show();
                            }
                        }

    //                    double distance = SphericalUtil.computeDistanceBetween();

                        mMap.addMarker(new MarkerOptions().position(new LatLng(latti1,longi1)).title("Current"));

                        Location loc1 = new Location("");
                        loc1.setLatitude(latti1);
                        loc1.setLongitude(longi1);

                        Location loc2 = new Location("");
                        if (dangerDataStore != null) {
                            loc2.setLatitude(Double.parseDouble(dangerDataStore.lattitude));
                        }
                        if (dangerDataStore != null) {
                            loc2.setLongitude(Double.parseDouble(dangerDataStore.longitude));
                        }

                        float distanceInMeters = loc1.distanceTo(loc2);

//                        Toast.makeText(MapsActivity.this,"distance : "+distanceInMeters,Toast.LENGTH_SHORT).show();

    //                    if(distanceInMeters < 5) {

                        mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(dangerDataStore.lattitude), Double.parseDouble(dangerDataStore.longitude))).title("Danger"));
    //                    Log.d("tag", "Value: " + dangerdatastore.latitude);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(dangerDataStore.lattitude), Double.parseDouble(dangerDataStore.longitude))));
    //                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }



}
