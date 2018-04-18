package com.thepraats.driverdroid;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    LocationService myService;
    static boolean status;
    LocationManager locationManager;
    static TextView dist, time, speedt;
    Button start, pause, stop, go;
    static long startTime, endTime;
    ImageView image;
    static ProgressDialog locate;
    static int p = 0;
    ToggleButton dmode;

    BroadcastReceiver receiver;
    IntentFilter filter;
    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            myService = binder.getService();
            status = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            status = false;
        }
    };

    void bindService() {
        if (status == true)
            return;
        Intent i = new Intent(getApplicationContext(), LocationService.class);
        bindService(i, sc, BIND_AUTO_CREATE);
        status = true;
        startTime = System.currentTimeMillis();
    }

    void unbindService() {
        if (status == false)
            return;
        Intent i = new Intent(getApplicationContext(), LocationService.class);
        unbindService(sc);
        status = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (status == true)
            unbindService();
    }

    @Override
    public void onBackPressed() {
        if (status == false)
            super.onBackPressed();
        else
            moveTaskToBack(true);
    }
    AudioManager am;
    Button sout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);

        }
        speedt = (TextView) findViewById(R.id.speedtext);
        dmode=(ToggleButton)findViewById(R.id.toggleButton);
        am =  (AudioManager)getSystemService(getApplicationContext().AUDIO_SERVICE);
        //sout=(Button)findViewById(R.id.signout);
        go=(Button)findViewById(R.id.button2);

    }
    public void signoutMethod(View v){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this,LoginActivity.class));

    }
    public void goToMaps(View view){


        Intent intent = new Intent(this,MapsActivity.class);
        startActivity(intent);



    }
    void checkGps() {

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

//            Toast.makeText(this,"Network Provider",Toast.LENGTH_SHORT).show();

//            showGPSDisabledAlertToUser();
        }
    }

    //This method configures the Alert Dialog box.
    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Enable GPS to use application")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    public void OnToggleClicked(View view)
    {
        long time;

        if (((ToggleButton) view).isChecked())
        {
            //am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            /*if(LocationService.speed>20.0){
                am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            }
            else{
                int mod = am.getRingerMode();
                am.setRingerMode(mod);
            }*/
            receiver=new CallReceive();

            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.PHONE_STATE");
            this.registerReceiver(receiver, filter);



            checkGps();
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

//                return;
//                Toast.makeText(this,"Net",Toast.LENGTH_SHORT).show();
            }


            if (status == false)
                //Here, the Location Service gets bound and the GPS Speedometer gets Active.
                bindService();
            locate = new ProgressDialog(MainActivity.this);
            locate.setIndeterminate(true);
            locate.setCancelable(false);
            locate.setMessage("Getting Location...");
            locate.show();
            //start.setVisibility(View.GONE);

        }
        else
        {

            if (status == true)
                unbindService();

            p = 0;
            speedt.setText("---");
        }
    }
    public void rep(View view){
        Intent i=new Intent(MainActivity.this, Report.class);

        startActivity(i);

    }
//    public void actionSilent (int i){
//        if(i==1){
//            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//        }
////        else{
////            int mod = am.getRingerMode();
////            am.setRingerMode(mod);
////        }
//    }
}
