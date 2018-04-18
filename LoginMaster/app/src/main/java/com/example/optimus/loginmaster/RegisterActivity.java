package com.example.optimus.loginmaster;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity{

    private EditText inputEmail, inputPassword,inputFullName,inputDOB;
    private Button btnLogin,btnRegister;
    private ProgressBar progressBar;
    Calendar myCalendar;


    private DatabaseReference mDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseAuth fAuth;
    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnLogin = (Button) findViewById(R.id.btn_login);
        btnRegister = (Button) findViewById(R.id.btn_register);
        inputFullName = (EditText) findViewById(R.id.txt_fullname);
        inputDOB = (EditText) findViewById(R.id.txt_dob);
        inputEmail = (EditText) findViewById(R.id.txt_email);
        inputPassword = (EditText) findViewById(R.id.txt_password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        myCalendar = Calendar.getInstance();

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mDatabase = mFirebaseInstance.getReference();

        fAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateTime();
            }
        };

        inputDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(RegisterActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();
                final String fullname = inputFullName.getText().toString().trim().toLowerCase();
                final String DOB = inputDOB.getText().toString().trim();

                if(TextUtils.isEmpty(inputFullName.getText().toString())) {
                    Toast.makeText(getApplicationContext(),"Full name is required",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(inputDOB.getText().toString())) {
                    Toast.makeText(getApplicationContext(),"Date of birth is required",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Email ID is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Password is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.matches("^(?=.*[A-Z].*[A-Z])(?=.*[!@#$&*])(?=.*[0-9].*[0-9])(?=.*[a-z].*[a-z].*[a-z]).{8}$")) {
                    Toast.makeText(getApplicationContext(), "Password is weak", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.INVISIBLE);
                                if (!task.isSuccessful()) {
                                    try {
                                        throw task.getException();
                                    }
                                    catch (FirebaseAuthUserCollisionException existEmail) {
                                        Toast.makeText(RegisterActivity.this, "Email ID already exists",Toast.LENGTH_SHORT).show();
                                    }
                                    catch (FirebaseAuthWeakPasswordException weakPassword) {
                                        Toast.makeText(RegisterActivity.this, "Password is weak",Toast.LENGTH_SHORT).show();
                                    }
                                    catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                                        Toast.makeText(RegisterActivity.this, "Invalid Email ID" ,Toast.LENGTH_SHORT).show();
                                    }
                                    catch (Exception e) {
                                        Toast.makeText(RegisterActivity.this, "Registration failed",Toast.LENGTH_SHORT).show();
                                    }
                                } else {

                                    FirebaseUser userProfile = FirebaseAuth.getInstance().getCurrentUser();
                                    if (userProfile != null) {
                                        String uid = userProfile.getUid();
                                        //User user = new User(inputFullName.getText().toString(),inputDOB.getText().toString());
                                        //DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference("users");
                                        //userDatabase.child(uid).setValue(user);
                                        FirebaseAuth.getInstance().getCurrentUser()
                                                .sendEmailVerification()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(RegisterActivity.this, "Email sent to "+ FirebaseAuth.getInstance().getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                                                    FirebaseAuth.getInstance().getCurrentUser()
                                                            .reload();
                                                    Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                                                    startActivity(i);

                                                    //mDatabase.child("users").setValue(email, password, fullname, DOB);
                                                    User user = new User(fullname, DOB, email);
                                                    mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);
                                                }
                                                else{
                                                    Toast.makeText(RegisterActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    }
                                }
                            }
                        });

            }
        });
    }


    private void updateDateTime() {
        String myFormat = "dd-MM-yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        inputDOB.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.INVISIBLE);
    }
}

