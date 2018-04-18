package com.thepraats.driverdroid;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * Created by prasanna on 30-03-2018.
 */

public class CallReceive extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

//        Toast.makeText(context, "Intent Detected.", Toast.LENGTH_LONG).show();

        final TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        final AudioManager audioManager=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        telephony.listen(new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
//                LocationService ls=new LocationService();
//                Toast.makeText(context,"incoming : "+incomingNumber,Toast.LENGTH_LONG).show();

                if(LocationService.speed>20.0 && state==1){

                    audioManager.setRingerMode(1);
                    if(!incomingNumber.equals("")){
                        //telephonyService.endCall();
                    SmsManager smsManager=SmsManager.getDefault();
                    smsManager.sendTextMessage(incomingNumber, null, "Driver is busy", null, null);}

                }
//                Toast.makeText(context,"incomingNumber : "+incomingNumber,Toast.LENGTH_LONG).show();

            }

        },PhoneStateListener.LISTEN_CALL_STATE);

    }
}
