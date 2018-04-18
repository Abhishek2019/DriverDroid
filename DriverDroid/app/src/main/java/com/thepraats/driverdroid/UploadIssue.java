package com.thepraats.driverdroid;

/**
 * Created by jaybhay-PC on 30-Mar-18.
 */

public class UploadIssue {

    String lattitude="";
    String longitude="";
    String image="";
    UploadIssue(){

    }
    UploadIssue(String encoded, String latti, String longi){
        image=encoded;
        lattitude=latti;
        longitude=longi;
    }

}
