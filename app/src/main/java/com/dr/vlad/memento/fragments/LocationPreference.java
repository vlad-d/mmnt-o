package com.dr.vlad.memento.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.dr.vlad.memento.PlaceAutocompleteAdapter;
import com.dr.vlad.memento.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.regex.Pattern;

/**
 * Created by drinc on 3/3/2017.
 */

public class LocationPreference extends DialogPreference {
    public static final String TAG = "Preferences";
    public static final LatLngBounds BOUNDS_ENTIRE_WORLD = new LatLngBounds(new LatLng(-90, -180), new LatLng(90, 180));
    private Context context;
    private GoogleApiClient mGoogleApiClient;
    private AutoCompleteTextView mAutocompleteView;
    private PlaceAutocompleteAdapter mAdapter;
    private Place mPlace;
    private ResultCallback<PlaceBuffer> mUpdateDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            mPlace = places.get(0);
            mAutocompleteView.setText(mPlace.getName());
            mAutocompleteView.clearFocus();
        }
    };
    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdateDetailsCallback);

            showKeyboard(false);

        }
    };

    public LocationPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setDialogLayoutResource(R.layout.card_autocomplete);
        setPositiveButtonText(R.string.reminder_positive_button);
        setNegativeButtonText(R.string.reminder_negative_button);
        setDialogTitle("Choose place");
    }

    @Override
    protected View onCreateDialogView() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Places.GEO_DATA_API)
                .build();
        mGoogleApiClient.connect();
        return super.onCreateDialogView();
    }

    @Override
    protected void onBindDialogView(View rootView) {
        super.onBindDialogView(rootView);
        mAutocompleteView = (AutoCompleteTextView) rootView.findViewById(R.id.autocomplete_text_view);
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
        mAdapter = new PlaceAutocompleteAdapter(getContext(), mGoogleApiClient, BOUNDS_ENTIRE_WORLD, null);
        mAutocompleteView.setAdapter(mAdapter);

        mAutocompleteView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    showKeyboard(true);
                }
            }
        });
    }


    @Override
    protected void onDialogClosed(boolean positiveResult) {
        mGoogleApiClient.disconnect();
        if (positiveResult) {
            if (mPlace != null) {
                persistString(mPlace.getLatLng().latitude + "|" + mPlace.getLatLng().longitude + "|" + mPlace.getName());
                setSummary(mPlace.getName());
            } else {
                Toast.makeText(context, "Please select a place", Toast.LENGTH_SHORT).show();
                setSummary("not set");
            }

        }
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            String[] location = this.getPersistedString("not set|not set|not set").split(Pattern.quote("|"));
            setSummary(location[2]);
        } else {
            setSummary("not set");

        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);

    }


    private void showKeyboard(boolean show) {
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (show) {
            getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//            inputMethodManager.showSoftInput(mAutocompleteView, InputMethodManager.SHOW_IMPLICIT);
        } else {
            inputMethodManager.hideSoftInputFromWindow(mAutocompleteView.getWindowToken(), 0);
        }
    }

}
