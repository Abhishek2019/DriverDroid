package com.example.optimus.loginmaster;

/**
 * Created by Roshan on 31-03-2018.
 */
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public String fullname;
    public String DOB;
    public String email;
    User(){

    }
    User(String fullname, String DOB, String email){
        this.fullname = fullname;
        this.DOB = DOB;
        this.email = email;
    }
}
