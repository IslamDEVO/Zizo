package com.yahoo.eslam_m_abdelaziz.uber.helper;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class Permission {

    //create an object of SingleObject
    private Permission instance = new Permission();

    // permission
    private static final String TAG = "ZIZO Main Activity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Boolean mLocationPermissionsGranted = false;

    //make the constructor private so that this class cannot be
    //instantiated
    private Permission() {}

    //Get the only object available
    public Permission getInstance(){
        return instance;
    }

    public boolean isServicesOK(Activity activity){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity.getBaseContext());

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(activity, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(activity, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

}
