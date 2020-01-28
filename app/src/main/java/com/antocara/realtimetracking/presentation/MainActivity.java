package com.antocara.realtimetracking.presentation;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.antocara.realtimetracking.R;
import com.antocara.realtimetracking.commons.HelperMap;
import com.antocara.realtimetracking.commons.HelperPermissions;
import com.antocara.realtimetracking.data.DataRepository;
import com.antocara.realtimetracking.data.entities.LocationObject;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, MainView {

  private static final String TAG = MainActivity.class.getSimpleName();
  private static final int CODE_ACTION_USER_REQUIRES = 2000;
  public static final int CODE_REQUEST_LOCATION_PERMISSIONS = 1000;

  private FloatingActionButton btnStartTracking;

  private MainPresenter presenter;
  private GoogleMap mMap;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    this.presenter = new MainPresenter(this);

    initializeUI();
  }

  private void initializeUI() {
    btnStartTracking = (FloatingActionButton) findViewById(R.id.start_tracking);
    btnStartTracking.setOnClickListener(listenerBtnStart);

    SupportMapFragment mapFragment =
        (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (presenter != null) {
      this.presenter.registerBroadcast(this);
    }
    updateMapWithNewLocation();
  }

  @Override
  protected void onPause() {
    super.onPause();
    this.presenter.unregisterBroadcast(this);
  }

  private View.OnClickListener listenerBtnStart = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      checkPermissions();
    }
  };

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
  }

  private void checkPermissions() {
    if (!HelperPermissions.isPermissionGranted(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
      ActivityCompat.requestPermissions(this,
          new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
          CODE_REQUEST_LOCATION_PERMISSIONS);
    } else {
      changeTrakingState();
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
      @NonNull
          String[] permissions,
      @NonNull
          int[] grantResults) {
    if (requestCode == CODE_REQUEST_LOCATION_PERMISSIONS) {
      if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        changeTrakingState();
      } else {
        Log.d(TAG, "Permission denegated");
      }
    }
  }

  private void changeTrakingState() {
    boolean isTracking = DataRepository.getInstance().isTracking();
    if (isTracking) {
      this.presenter.stopTracking(this);
      setIconAtButton(R.drawable.ic_action_start);
    } else {
      this.presenter.startTracking(this);
      setIconAtButton(R.drawable.ic_action_stop);
    }

  }
  private void setIconAtButton(int iconResourceDrawable){
    Drawable iconDrawable = ContextCompat.getDrawable(this, iconResourceDrawable);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      btnStartTracking.setImageDrawable(iconDrawable);
    } else {
      btnStartTracking.setImageDrawable(iconDrawable);
    }
  }

  /**
   * Callback @{@link MainView}
   */
  @Override
  public void updateMapWithNewLocation() {
    List<LocationObject> locationObjectsStored =
        DataRepository.getInstance().getAllLocationSaved();
    if (locationObjectsStored.size() > 0) {
      List<LatLng> locationPoints = HelperMap.getPoints(locationObjectsStored);
      refreshMap(mMap);
      markLocationOnMap(mMap, locationPoints.get(0));
      HelperMap.drawRouteOnMap(mMap, locationPoints, this);
    }
  }

  private void markLocationOnMap(GoogleMap map, LatLng location) {
    map.addMarker(new MarkerOptions().position(location).title("Current location"));
    map.moveCamera(CameraUpdateFactory.newLatLng(location));
  }

  private void refreshMap(GoogleMap map) {
    map.clear();
  }

  @Override
  public void displayAlertErrorLocation() {
    Log.d(TAG, "Location Error");
  }

  @Override
  public void displayAlertActionUserRequired(Status status) {
    try {
      status.startResolutionForResult(this, CODE_ACTION_USER_REQUIRES);
    } catch (IntentSender.SendIntentException e) {
      e.printStackTrace();
      Log.d(TAG, "User action required");
    }
  }

  @Override
  public void requestPermissions() {
    checkPermissions();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case CODE_ACTION_USER_REQUIRES:
        switch (resultCode) {
          case Activity.RESULT_OK:
            changeTrakingState();
            break;
          case Activity.RESULT_CANCELED:
            Log.d(TAG, "Operation canceled by user");
            break;
        }
        break;
    }
  }
}
