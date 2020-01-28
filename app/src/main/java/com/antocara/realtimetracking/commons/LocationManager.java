package com.antocara.realtimetracking.commons;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public class LocationManager
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    LocationListener {

  private static final String TAG = LocationManager.class.getSimpleName();


  private final long FASTEST_INTERVAL_UPDATE = 10 * 1000; //10secs
  private final long MIN_INTERVAL_UPDATE = 10 * 1000; //10secs
  private final int PRIORITY_POWER_ACCURACY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
  private final int PRIORITY_HIGH_ACCURACY = LocationRequest.PRIORITY_HIGH_ACCURACY;
  private final int PRIORITY_LOW_POWER = LocationRequest.PRIORITY_LOW_POWER;
  private final int PRIORITY_NO_POWER = LocationRequest.PRIORITY_NO_POWER;

  private GoogleApiClient mGoogleApiClient;
  private OnLocationManagerListener listener;
  private Context context;

  public LocationManager(Context context, OnLocationManagerListener listener) {
    this.context = context;
    this.listener = listener;
    initGoogleApiConnection(context);
  }

  private void initGoogleApiConnection(Context context) {
    if (mGoogleApiClient == null) {
      mGoogleApiClient = new GoogleApiClient.Builder(context).addConnectionCallbacks(this)
          .addApi(LocationServices.API)
          .build();
    }
    connect();
  }

  public void connect() {
    if (mGoogleApiClient != null) {
      mGoogleApiClient.connect();
    }
  }

  public void disConnect() {
    if (mGoogleApiClient != null) {
      mGoogleApiClient.disconnect();
    }
  }

  public void disableLocationUpdates() {
    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
  }

  public void enableLocationUpdates() {
    if (HelperPermissions.isPermissionGranted(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
      PendingResult<LocationSettingsResult> result =
          LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
              createLocationSettingsRequest());

      result.setResultCallback(callbaclUpdateLocation);
    }
  }

  private LocationSettingsRequest createLocationSettingsRequest() {
    return new LocationSettingsRequest.Builder().addLocationRequest(createLocationRequest())
        .build();
  }

  private LocationRequest createLocationRequest() {
    LocationRequest mLocationRequest = new LocationRequest();
    mLocationRequest.setInterval(MIN_INTERVAL_UPDATE);
    mLocationRequest.setFastestInterval(FASTEST_INTERVAL_UPDATE);
    mLocationRequest.setPriority(PRIORITY_HIGH_ACCURACY);
    return mLocationRequest;
  }

  private ResultCallback<LocationSettingsResult> callbaclUpdateLocation =
      new ResultCallback<LocationSettingsResult>() {
        @Override
        public void onResult(
            @NonNull
                LocationSettingsResult locationSettingsResult) {

          final Status status = locationSettingsResult.getStatus();
          switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
              startLocationUpdates();
              break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
              if (listener != null) {
                listener.userActionRequired(status);
              }
              break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
              if (listener != null) {
                listener.onFailGetLocation();
              }
              break;
          }
        }
      };

  @SuppressWarnings({ "MissingPermission" })
  private void startLocationUpdates() {
    if (!HelperPermissions.isPermissionGranted(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
      requestUserPermission();
    } else {
      LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
          createLocationRequest(), this);
    }
  }

  /**
   * Callbacks @{@link GoogleApiClient.ConnectionCallbacks}
   */
  @Override
  public void onConnected(
      @Nullable
          Bundle bundle) {
    if (!HelperPermissions.isPermissionGranted(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
      requestUserPermission();
    } else {
      getLastLocation();
    }
  }

  @Override
  public void onConnectionSuspended(int i) {
    Log.d(TAG, "onConnectionSuspended" + i);
  }

  @Override
  public void onConnectionFailed(
      @NonNull
          ConnectionResult connectionResult) {
    Log.d(TAG, "onConnectionFailed");
    if (listener != null) {
      listener.onFailGetLocation();
    }
  }

  /**
   * Callback @{@link LocationListener}
   *
   * @param location new location received
   */
  @Override
  public void onLocationChanged(Location location) {
    if (location != null) {
      Log.d(TAG, "new location update - latitude" + location.getLatitude());
      Log.d(TAG, "new location update - longitude" + location.getLongitude());
    }
    if (listener != null) {
      listener.lasLocationReceived(location);
    }
  }

  @SuppressWarnings({ "MissingPermission" })
  public void getLastLocation() {
    if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
      Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
      if (listener != null) {
        listener.lasLocationReceived(lastLocation);
      }
    }
  }

  private void requestUserPermission() {
    if (listener != null) {
      listener.requestPermissionsNecessary();
    }
  }

  /**
   * Interface to notify news locations update
   */
  public interface OnLocationManagerListener {
    void lasLocationReceived(Location lastLocation);

    void onFailGetLocation();

    void userActionRequired(Status status);

    void requestPermissionsNecessary();
  }
}
