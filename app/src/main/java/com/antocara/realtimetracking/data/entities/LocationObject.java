package com.antocara.realtimetracking.data.entities;

public class LocationObject {

  private double Latitude;
  private double Longitude;
  private long currentTimeMiliseconds;

  public double getLatitude() {
    return Latitude;
  }

  public void setLatitude(double latitude) {
    Latitude = latitude;
  }

  public double getLongitude() {
    return Longitude;
  }

  public void setLongitude(double longitude) {
    Longitude = longitude;
  }

  public long getCurrentTimeMiliseconds() {
    return currentTimeMiliseconds;
  }

  public void setCurrentTimeMiliseconds(long currentTimeMiliseconds) {
    this.currentTimeMiliseconds = currentTimeMiliseconds;
  }
}
