package com.yahoo.eslam_m_abdelaziz.uber;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.yahoo.eslam_m_abdelaziz.uber.model.User;

import java.net.InetAddress;
import java.net.UnknownHostException;

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    // permission
    private static final String TAG = "ZIZO Main Activity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Boolean mLocationPermissionsGranted = false;
    // view variables
    Button btnSignIn, btnRegister;
    RelativeLayout rootLayout;
    // firebase variables
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;

    //press ctrl+o to show override methods
    //set font
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //before setContentView
        //set the font type
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Arkhip_font.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_main);
        //------------------for test--------------//
        //Intent intent = new Intent(MainActivity.this,Home.class);
        //finish();
        //startActivity(intent);
        //----------------------------------------//

        // init app
        if(isServicesOK()){
            initApp();
        }
    }

    private void initApp() {
        //init firebase
        //FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Riders");
        //init view
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            // Do something for lollipop and above versions
            btnSignIn.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.btn_sign_in_background_v21));
            btnRegister.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.btn_register_background_v21));
        } else{
            // do something for phones running an SDK before lollipop
            btnSignIn.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.btn_sign_in_background));
            btnRegister.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.btn_register_background));
        }
        //init Events
        btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showRegisterDialog();
                }
            });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSignInDialog();
                }
            });

    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    //register dialog
    private void showRegisterDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Register");
        dialog.setMessage("Please use email to register");

        LayoutInflater inflater = LayoutInflater.from(this);
        View register_layout = inflater.inflate(R.layout.layout_register, null);

        final MaterialEditText edtEmail = register_layout.findViewById(R.id.edtEmail);
        final MaterialEditText edtPassword = register_layout.findViewById(R.id.edtPassword);
        final MaterialEditText edtName = register_layout.findViewById(R.id.edtName);
        final MaterialEditText edtPhone = register_layout.findViewById(R.id.edtPhone);

        dialog.setView(register_layout);

        dialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                //email
                if(TextUtils.isEmpty(edtEmail.getText().toString())){
                    Snackbar.make(rootLayout,"Please enter email address",Snackbar.LENGTH_LONG).show();
                    Log.d(TAG,"showRegisterDialog : Please enter email address");
                    return;
                }else {
                    String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
                    java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
                    java.util.regex.Matcher m = p.matcher(edtEmail.getText().toString());
                    if(!m.matches()){
                        Snackbar.make(rootLayout,"Please enter valid email address",Snackbar.LENGTH_LONG).show();
                        Log.d(TAG,"showRegisterDialog : Please enter valid email address");
                        return;
                    }
                }
                //password
                if(TextUtils.isEmpty(edtPassword.getText().toString())){
                    Snackbar.make(rootLayout,"Please enter password",Snackbar.LENGTH_LONG).show();
                    Log.d(TAG,"showRegisterDialog : Please enter password");
                    return;
                }else {
                    if (edtPassword.getText().length() < 6) {
                        Snackbar.make(rootLayout, "Password too short !!!", Snackbar.LENGTH_LONG).show();
                        Log.d(TAG,"showRegisterDialog : Password too short !!!");
                        return;
                    }
                }
                //name
                if(TextUtils.isEmpty(edtName.getText().toString())){
                    Snackbar.make(rootLayout,"Please enter Name",Snackbar.LENGTH_LONG).show();
                    Log.d(TAG,"showRegisterDialog : Please enter Name");
                    return;
                }
                //phone
                if(TextUtils.isEmpty(edtPhone.getText().toString())){
                    Snackbar.make(rootLayout,"Please enter phone number",Snackbar.LENGTH_LONG).show();
                    Log.d(TAG,"showRegisterDialog : Please enter phone number");
                    return;
                }
                //register new user
                auth.createUserWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                //save user to db
                                User user = new User();
                                user.setEmail(edtEmail.getText().toString());
                                user.setPassword(edtPassword.getText().toString());
                                user.setName(edtName.getText().toString());
                                user.setPhone(edtPhone.getText().toString());

                                //user user id to key
                                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Snackbar.make(rootLayout,"Register Successfully",Snackbar.LENGTH_LONG).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Snackbar.make(rootLayout,"failed "+e.getMessage(),Snackbar.LENGTH_LONG).show();
                                                Log.d(TAG,"createUserWithEmailAndPassword : "+e.getMessage());
                                            }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(rootLayout,"failed "+e.getMessage(),Snackbar.LENGTH_LONG).show();
                                Log.d(TAG,"createUserWithEmailAndPassword : "+e.getMessage());
                            }
                        });
            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    //signIn dialog
    private void showSignInDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Sign In");
        dialog.setMessage("Please use email and password to sign in");

        LayoutInflater inflater = LayoutInflater.from(this);
        View sign_in_layout = inflater.inflate(R.layout.layout_sign_in, null);

        final MaterialEditText edtEmail = sign_in_layout.findViewById(R.id.edtEmail);
        final MaterialEditText edtPassword = sign_in_layout.findViewById(R.id.edtPassword);

        dialog.setView(sign_in_layout);

        dialog.setPositiveButton("SIGN IN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                //btnSignIn.setEnabled(false);
                //email
                if(TextUtils.isEmpty(edtEmail.getText().toString())){
                    Snackbar.make(rootLayout,"Please enter email address",Snackbar.LENGTH_LONG).show();
                    Log.d(TAG,"showSignInDialog : Please enter email address");
                    return;
                }else {
                    String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
                    java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
                    java.util.regex.Matcher m = p.matcher(edtEmail.getText().toString());
                    if(!m.matches()){
                        Snackbar.make(rootLayout,"Please enter valid email address",Snackbar.LENGTH_LONG).show();
                        Log.d(TAG,"showSignInDialog : Please enter valid email address");
                        return;
                    }
                }
                //password
                if(TextUtils.isEmpty(edtPassword.getText().toString())){
                    Snackbar.make(rootLayout,"Please enter password",Snackbar.LENGTH_LONG).show();
                    Log.d(TAG,"showSignInDialog : Please enter password");
                    return;
                }else {
                    if (edtPassword.getText().length() < 6) {
                        Snackbar.make(rootLayout, "Password too short !!!", Snackbar.LENGTH_LONG).show();
                        Log.d(TAG,"showSignInDialog : Password too short !!!");
                        return;
                    }
                }

                //The library requires minimum API level 15.
                final AlertDialog waitingDialog = new SpotsDialog.Builder()
                        .setContext(MainActivity.this)
                        .build();
                waitingDialog.show();

                //sign in
                auth.signInWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                waitingDialog.dismiss();
                                startActivity(new Intent(MainActivity.this, Home.class));
                                finish(); //to finish the MainActivity
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                waitingDialog.dismiss();
                                Snackbar.make(rootLayout,"failed"+e.getMessage(),Snackbar.LENGTH_LONG).show();
                                Log.d(TAG,"signInWithEmailAndPassword : "+e.getMessage());
                                //btnSignIn.setEnabled(true);
                            }
                        });

            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //btnSignIn.setEnabled(true);

            }
        });

        dialog.show();
    }
}
