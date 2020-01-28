package com.antocara.realtimetracking.commons;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import com.antocara.realtimetracking.R;
import com.antocara.realtimetracking.data.entities.LocationObject;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.ArrayList;
import java.util.List;

public class HelperMap {

  public static void drawRouteOnMap(GoogleMap map, List<LatLng> positions, Context context) {
    int polylineColor = ContextCompat.getColor(context, R.color.colorPrimaryDark);
    PolylineOptions options = new PolylineOptions().width(10).color(polylineColor).geodesic(true);
    options.addAll(positions);
    map.addPolyline(options);
    CameraPosition cameraPosition = createCameraPosition(positions);
    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
  }

  private static CameraPosition createCameraPosition(List<LatLng> positions) {
    LatLng lastPosition = new LatLng(positions.get(0).latitude, positions.get(0).longitude);
    return new CameraPosition.Builder().target(lastPosition).zoom(17).bearing(90).tilt(40).build();
  }

  public static List<LatLng> getPoints(List<LocationObject> mLocations) {
    List<LatLng> points = new ArrayList<LatLng>();
    for (LocationObject mLocation : mLocations) {
      points.add(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
    }
    return points;
  }
}
