package com.dr.vlad.memento;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dr.vlad.memento.model.Reminder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by drinc on 3/25/2017.
 */

public class GeofenceBuilder {

    public static final String TAG = GeofenceBuilder.class.getSimpleName();
    /**
     * Geofence radius in meters
     */
    public static final float GEOFENCE_RADIUS = 100;
    /**
     * Geofence dwell time in milliseconds
     */
    public static final int GEOFENCE_DWELL_TIME = 2 * 60 * 1000;

    PendingIntent mGeofencePendingIntent;
    private Context context;
    private GoogleApiClient mGoogleApiClient;
    private Reminder reminder;
    private List<Geofence> mGeofenceList;
    private GoogleApiClient.ConnectionCallbacks connectionAddListener = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Log.i(TAG, "onConnected");

            try {
                LocationServices.GeofencingApi.addGeofences(
                        mGoogleApiClient,
                        getGeofencingRequest(),
                        getGeofencePendingIntent()
                ).setResultCallback(new ResultCallback<Status>() {

                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Saving Geofence");

                        } else {
                            Log.e(TAG, "Registering geofence failed: " + status.getStatusMessage() +
                                    " : " + status.getStatusCode());
                        }
                    }
                });

            } catch (SecurityException securityException) {
                // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
                Log.e(TAG, "Error");
            }
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.i(TAG, "onConnectionSuspended");
        }
    };
    private GoogleApiClient.OnConnectionFailedListener connectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.e(TAG, "onConnectionFailed");
        }
    };

    public GeofenceBuilder(Context context, Reminder reminder) {
        this.context = context;
        this.reminder = reminder;
        mGeofenceList = new ArrayList<Geofence>();

        createGeofences();
        initGoogleApiClient();
    }

    private void initGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(connectionAddListener)
                .addOnConnectionFailedListener(connectionFailedListener)
                .build();
        mGoogleApiClient.connect();
    }

    public void createGeofences() {
        Geofence geofence = new Geofence.Builder()
                .setRequestId(reminder.getId().toString())
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL)
                .setCircularRegion(reminder.getLatitude(), reminder.getLongitude(), GEOFENCE_RADIUS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setLoiteringDelay(GEOFENCE_DWELL_TIME)
                .build();

        mGeofenceList.add(geofence);
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(context, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
