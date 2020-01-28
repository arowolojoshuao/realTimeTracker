package com.antocara.realtimetracking.data;

import com.antocara.realtimetracking.data.entities.LocationObject;
import java.util.ArrayList;
import java.util.List;

public class DataRepository {
  private List<LocationObject> locationObjectsStored;
  private boolean isTracking;

  private static DataRepository instance;

  public static DataRepository getInstance() {
    if (instance == null){
      instance = new DataRepository();
    }
    return instance;
  }

  private DataRepository() {
    this.locationObjectsStored = new ArrayList<>();
  }


  public List<LocationObject> getAllLocationSaved(){
    return locationObjectsStored;
  }

  public void addNewLocationObject(long currentTimeMillis, double latitudeValue, double longitudeValue){
    LocationObject locationObject = new LocationObject();
    locationObject.setCurrentTimeMiliseconds(currentTimeMillis);
    locationObject.setLatitude(latitudeValue);
    locationObject.setLongitude(longitudeValue);

    this.locationObjectsStored.add(locationObject);
  }

  public void cleanDataLocation(){
    this.locationObjectsStored.clear();
  }

  public boolean isTracking() {
    return isTracking;
  }

  public void setTracking(boolean tracking) {
    isTracking = tracking;
  }
}
