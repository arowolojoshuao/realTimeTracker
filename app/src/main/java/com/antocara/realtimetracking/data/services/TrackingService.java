package com.antocara.realtimetracking.data.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.antocara.realtimetracking.commons.LocationManager;
import com.google.android.gms.common.api.Status;

public class TrackingService extends Service implements LocationManager.OnLocationManagerListener {

  private static final String TAG = TrackingService.class.getSimpleName();
  public static final String ACTION_SUCCESS_LOCATION = "com.antocara.success.location";
  public static final String ACTION_FAIL_LOCATION = "com.antocara.fail.location";
  public static final String ACTION_USER_REQUIRED = "com.antocara.action.user.required";
  public static final String ACTION_REQUEST_PERMISSIONS = "com.antocara.action.request.permissions";
  public static final String KEY_INTENT_SERVICE_LOCATION = "key_intent.service.location";

  private LocationManager locationManager;

  @Override
  public void onCreate() {
    super.onCreate();

    this.locationManager = new LocationManager(this, this);
    this.locationManager.enableLocationUpdates();
    Log.d(TAG, "onCreate TrackingService");
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return Service.START_STICKY;
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onDestroy() {
    this.locationManager.disableLocationUpdates();
    this.locationManager.disConnect();
    super.onDestroy();
    Log.d(TAG, "onDestroy TrackingService");
  }

  @Override
  public void lasLocationReceived(Location lastLocation) {
    Intent localBroadcastIntent = new Intent(ACTION_SUCCESS_LOCATION);
    localBroadcastIntent.putExtra(KEY_INTENT_SERVICE_LOCATION, lastLocation);
    sendBroadcastWithResult(localBroadcastIntent);
  }

  @Override
  public void onFailGetLocation() {
    Intent localBroadcastIntent = new Intent(ACTION_FAIL_LOCATION);
    sendBroadcastWithResult(localBroadcastIntent);
  }

  @Override
  public void userActionRequired(Status status) {
    Intent localBroadcastIntent = new Intent(ACTION_USER_REQUIRED);
    localBroadcastIntent.putExtra(KEY_INTENT_SERVICE_LOCATION, status);
    sendBroadcastWithResult(localBroadcastIntent);
  }

  private void sendBroadcastWithResult(Intent broadcastIntent) {
    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent);
  }

  @Override
  public void requestPermissionsNecessary() {
    Intent localBroadcastIntent = new Intent(ACTION_REQUEST_PERMISSIONS);
    sendBroadcastWithResult(localBroadcastIntent);
  }
}
