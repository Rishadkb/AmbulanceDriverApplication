package com.example.rishadkavad.ambulancedriverapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;

/**
 * Created by Rishad Kavad on 2/5/2018.
 */
@SuppressWarnings("MissingPermission")
public class GpsService extends Service {
    private LocationListener listener;
    private LocationManager manager;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
       listener = new LocationListener() {
           @Override
           public void onLocationChanged(Location location) {
               Intent intent = new Intent("location_update");
               //intent.putExtra("coordinates","Location : Latitude-"+location.getLongitude()+"Longitude-"+location.getLongitude()+" Time-"+location.getTime());
               intent.putExtra("lat",location.getLatitude());
               intent.putExtra("lon",location.getLongitude());
               sendBroadcast(intent);

           }

           @Override
           public void onStatusChanged(String provider, int status, Bundle extras) {

           }

           @Override
           public void onProviderEnabled(String provider) {

           }

           @Override
           public void onProviderDisabled(String provider) {
               Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               startActivity(intent);

           }
       };
        manager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        //noinspection MissingPermission
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000,0,listener);

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(manager != null){
            manager.removeUpdates(listener);
        }
    }
}
