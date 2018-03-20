package com.example.rishadkavad.ambulancedriverapplication;

import android.*;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase sqLiteDatabase;
  Button startButton,stopButton,viewButton;
  TextView textViewUser;
  BroadcastReceiver receiver;
    public static String userEmail;
  Double lat = 0.0;
  Double lon = 0.0;
  Geocoder geocoder;
    protected void onResume() {
        super.onResume();
        if(receiver == null){
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    // Toast.makeText(getApplicationContext(),"Lat :"+intent.getExtras().get("lat")+"Lon :"+intent.getExtras().get("lon"),Toast.LENGTH_SHORT).show();
                    lat = Double.parseDouble(String.valueOf(intent.getExtras().get("lat")));
                    lon = Double.parseDouble(String.valueOf(intent.getExtras().get("lon")));
                    String uname = String.valueOf(LoginActivity.USER_NAME);
                    String pword = String.valueOf(LoginActivity.USER_PASSWORD);
                    //Toast.makeText(getApplicationContext(),"Latitude :"+lat+" Longitude :"+lon,Toast.LENGTH_SHORT).show();
                       new LocationChangeClass(getApplicationContext()).execute(uname,pword,String.valueOf(lat),String.valueOf(lon));
                }
            };
        }
        registerReceiver(receiver,new IntentFilter("location_update"));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewUser = (TextView)findViewById(R.id.user_tv);
        sqLiteDatabase = openOrCreateDatabase("api_db",MODE_PRIVATE,null);
        Cursor cursor = sqLiteDatabase.rawQuery("select * from driver_login_tb", null);
        if (cursor.moveToNext()){
            textViewUser.setText("User Email :"+String.valueOf(cursor.getString(0)));
            userEmail = String.valueOf(cursor.getString(0));
        }
        sqLiteDatabase.close();
        Toast.makeText(getApplicationContext(),"User Email :"+LoginActivity.USER_NAME+" User Password :"+LoginActivity.USER_PASSWORD,Toast.LENGTH_SHORT).show();
        startButton  = (Button)findViewById(R.id.start_service_btn);
        stopButton = (Button)findViewById(R.id.stop_service_btn);
        viewButton = (Button)findViewById(R.id.view_all_btn);
        viewButton.setVisibility(View.INVISIBLE);
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });
        if(!runTimePermissions())
            enableButtons();
    }

    private void enableButtons() {
       startButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              new AlertDialog.Builder(MainActivity.this)
                      .setTitle("Emergency")
                      .setMessage("Do you really want to start service?")
                      .setIcon(android.R.drawable.ic_dialog_alert)
                      .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                          public void onClick(DialogInterface dialog, int whichButton) {
                              Toast.makeText(MainActivity.this, "Yes", Toast.LENGTH_SHORT).show();

                              Intent intent = new Intent(getApplicationContext(), GpsService.class);
                              startService(intent);
                              viewButton.setVisibility(View.VISIBLE);

                          }})
                      .setNegativeButton(android.R.string.no, null).show();
            }
       });
       stopButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Toast.makeText(MainActivity.this, "Service stopping...", Toast.LENGTH_SHORT).show();
               Intent i = new Intent(getApplicationContext(),GpsService.class);
               stopService(i);
               viewButton.setVisibility(View.INVISIBLE);
           }
       });
    }

    private boolean runTimePermissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},100);
            return true;
        }
        return false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                enableButtons();
            }
            else {
                runTimePermissions();
            }
        }
    }
    class LocationChangeClass extends AsyncTask<String,Void,String> {
        Context context;

        public LocationChangeClass(Context context) {
            this.context = context;

        }

        @Override
        protected String doInBackground(String... params) {
            String userEmail = (String)params[0];
            String userPassword = (String)params[1];
            String lat = (String)params[2];
            String lon = (String)params[3];

            //String link = "http://192.168.43.245/APISystem/Transfer_Locations/Update_driver_location.php";
            String link = "https://rishadkavad.000webhostapp.com/PAS/Transfer_Locations/Update_driver_location.php";

            try{
                String data = URLEncoder.encode("user_email", "UTF-8") + "=" +
                        URLEncoder.encode(userEmail, "UTF-8");
                data += "&" + URLEncoder.encode("user_password", "UTF-8") + "=" +
                        URLEncoder.encode(userPassword, "UTF-8");
                data += "&" + URLEncoder.encode("lat", "UTF-8") + "=" +
                        URLEncoder.encode(lat, "UTF-8");
                data += "&" + URLEncoder.encode("lon", "UTF-8") + "=" +
                        URLEncoder.encode(lon, "UTF-8");

                URL url = new URL(link);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write( data );
                wr.flush();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line = null;
                while ((line = reader.readLine())!=null){
                    stringBuilder.append(line);
                }

                return stringBuilder.toString();
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            if(s!=null) {
                Toast.makeText(getApplicationContext(),"After Change :"+ s, Toast.LENGTH_LONG).show();


            }
            else {
                Toast.makeText(getApplicationContext(), "Not Changed", Toast.LENGTH_LONG).show();
            }

        }
    }
}
