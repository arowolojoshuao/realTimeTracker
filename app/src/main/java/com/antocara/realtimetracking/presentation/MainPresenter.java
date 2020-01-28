package com.antocara.realtimetracking.presentation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import com.antocara.realtimetracking.data.DataRepository;
import com.antocara.realtimetracking.data.services.TrackingService;
import com.google.android.gms.common.api.Status;

public class MainPresenter {

  private RouteBroadCastReceiver routeServiceReceiver;
  private MainView view;

  public MainPresenter(MainView view) {
    this.view = view;
  }

  public void startTracking(Context context) {
    DataRepository.getInstance().setTracking(true);
    Intent intent = new Intent(context, TrackingService.class);
    context.startService(intent);
  }

  public void stopTracking(Context context) {
    DataRepository.getInstance().setTracking(false);
    DataRepository.getInstance().cleanDataLocation();
    context.stopService(new Intent(context, TrackingService.class));
  }

  private class RouteBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      String actionType = intent.getAction();
      if (actionType != null) {
        parseResult(actionType, intent);
      }
    }
  }

  private void parseResult(String actionType, Intent intent) {
    if (TrackingService.ACTION_SUCCESS_LOCATION.equals(actionType)) {
      Location location =
          (Location) intent.getExtras().getParcelable(TrackingService.KEY_INTENT_SERVICE_LOCATION);
      saveNewLocation(location);
      view.updateMapWithNewLocation();
    } else if (TrackingService.ACTION_FAIL_LOCATION.equals(actionType)) {
      view.displayAlertErrorLocation();
    } else if (TrackingService.ACTION_USER_REQUIRED.equals(actionType)) {
      Status status =
          (Status) intent.getExtras().getParcelable(TrackingService.KEY_INTENT_SERVICE_LOCATION);
      view.displayAlertActionUserRequired(status);
    } else if (TrackingService.ACTION_REQUEST_PERMISSIONS.equals(actionType)){
      view.requestPermissions();
    }
  }

  private void saveNewLocation(Location location) {
    double latitude = location.getLatitude();
    double longitude = location.getLongitude();

    DataRepository.getInstance()
        .addNewLocationObject(System.currentTimeMillis(), latitude, longitude);

  }

  public void registerBroadcast(Context context) {
    if (routeServiceReceiver == null) {
      routeServiceReceiver = new RouteBroadCastReceiver();
    }
    IntentFilter filter = new IntentFilter();
    filter.addAction(TrackingService.ACTION_SUCCESS_LOCATION);
    filter.addAction(TrackingService.ACTION_FAIL_LOCATION);
    filter.addAction(TrackingService.ACTION_USER_REQUIRED);
    LocalBroadcastManager.getInstance(context).registerReceiver(routeServiceReceiver, filter);
  }

  public void unregisterBroadcast(Context context) {
    LocalBroadcastManager.getInstance(context).unregisterReceiver(routeServiceReceiver);
  }
}
