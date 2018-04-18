package com.thepraats.driverdroid;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;

public class Report extends AppCompatActivity {
    static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    private static final int CAMERA_REQUEST = 1888;
    private DatabaseReference mDatabase;
    String encoded="no image";
    double latti=0.0, longi=0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mDatabase = FirebaseDatabase.getInstance().getReference();
         }
    //location
    public void getLocation(View v) {

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null){
                latti = location.getLatitude();
                longi = location.getLongitude();
              Toast.makeText(Report.this, String.valueOf("Lattitude: "+latti+"  Longitude: "+longi),Toast.LENGTH_SHORT).show();
                /*((EditText)findViewById(R.id.etLocationLat)).setText("Latitude: " + latti);
                ((EditText)findViewById(R.id.etlocationLong)).setText("Longitude: " + longi);*/
            } else {
             Toast.makeText(Report.this,"Unable to get location coordinates",Toast.LENGTH_SHORT).show();
            }
        }

    }
    //image
    public void onClickImage(View v) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

           // mDatabase.child("img").push().setValue(encoded);

        }
    }
    public void reportIssue(View v){
        UploadIssue u=new UploadIssue(encoded, String.valueOf(latti), String.valueOf(longi));
        mDatabase.child("danger_coordinates").push().setValue(u);
//        Toast.makeText(Report.this,"Issue Reported",Toast.LENGTH_SHORT).show();
    }

}
