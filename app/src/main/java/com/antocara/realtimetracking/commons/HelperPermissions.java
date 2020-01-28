package com.antocara.realtimetracking.commons;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

public class HelperPermissions {

  public static boolean isPermissionGranted(Context context, String permissionName) {
    boolean isGranted = false;
    if (ActivityCompat.checkSelfPermission(context, permissionName)
        == PackageManager.PERMISSION_GRANTED) {
      isGranted = true;
    }
    return isGranted;
  }
}
