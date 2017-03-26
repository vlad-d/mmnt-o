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
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.gcm.PendingCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by drinc on 3/25/2017.
 */

public class GeofenceBuilder implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback {

    public static final String TAG = GeofenceBuilder.class.getSimpleName();
    /**
     * Geofence radius in meters
     */
    public static final float GEOFENCE_RADIUS = 50f;
    /**
     * Geofence dwell time in milliseconds
     */
    public static final int GEOFENCE_DWELL_TIME = 500;
    Context context;
    GoogleApiClient mGoogleApiClient;
    Reminder reminder;

    public GeofenceBuilder(Context context, Reminder reminder) {
        this.context = context;
        this.reminder = reminder;
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mGoogleApiClient.connect();
    }

    @SuppressWarnings("MissingPermission")
    private void addGeofence(Reminder reminder) {
        Geofence geofence = new Geofence.Builder()
                .setRequestId(String.valueOf(reminder.getId()))
                .setCircularRegion(reminder.getLatitude(), reminder.getLatitude(), GEOFENCE_RADIUS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(GEOFENCE_DWELL_TIME)
                .build();

        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build();
        Intent intent = new Intent(context, GeofenceTransitionsIntentService.class);
        intent.putExtra(context.getResources().getString(R.string.key_intent_geofence_reminder), reminder.getId());
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, geofencingRequest, pendingIntent).setResultCallback(this);

    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        addGeofence(reminder);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull Result result) {
        Log.i(TAG, "Geofence added");
    }
}
