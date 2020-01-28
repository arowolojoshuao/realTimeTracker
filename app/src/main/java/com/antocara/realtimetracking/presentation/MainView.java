package com.antocara.realtimetracking.presentation;

import com.google.android.gms.common.api.Status;

public interface MainView {

  void updateMapWithNewLocation();

  void displayAlertErrorLocation();

  void displayAlertActionUserRequired(Status status);

  void requestPermissions();
}
