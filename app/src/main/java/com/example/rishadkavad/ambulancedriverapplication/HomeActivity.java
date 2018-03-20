package com.example.rishadkavad.ambulancedriverapplication;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;


public class HomeActivity extends AppCompatActivity {
    String myJSON;
    private static final String TAG_RESULTS = "result";
    private static final String TAG_ACCIDENT_PLACE = "a_place";
    private static final String TAG_ACCIDENT_INFORMER = "a_informer";
    ArrayList<HashMap<String, String>> accidentArrayList;
    ListView listView;
    JSONArray accidentsJSonArray = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        accidentArrayList = new ArrayList<HashMap<String, String>>();
        listView = (ListView)findViewById(R.id.acc_list);
        new ViewAccidentClass(getApplicationContext()).execute("KKM","Rishad","123456");
        // Alert Section
//        final Context context = this;
//        final Dialog openDialog = new Dialog(context);
//        openDialog.setContentView(R.layout.em_message);
//        openDialog.setTitle("Do you want to attend?");
//        TextView dialogTextContent = (TextView)openDialog.findViewById(R.id.dialog_text);
//        //ImageView dialogImage = (ImageView)openDialog.findViewById(R.id.dialog_image);
//        Button dialogCloseButton = (Button)openDialog.findViewById(R.id.dialog_button);
//        dialogCloseButton.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                openDialog.dismiss();
//            }
//        });
//        openDialog.show();
        //Alert Section ends
// Accident List From Server

        //Ends(Accident List From server)


//        mBuilder = new NotificationCompat.Builder(this);
//        mBuilder.setSmallIcon(R.drawable.notfi_ico);
//        mBuilder.setContentTitle("Notification Alert, Click Me!");
//        mBuilder.setContentText("Hi, This is Android Notification Detail!");
//        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        Intent notificationIntent = new Intent(this, HomeActivity.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        mBuilder.setContentIntent(contentIntent);
//        long[] v = {500,1000};
//        mBuilder.setVibrate(v);
//        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        mBuilder.setSound(uri);
//
//// notificationID allows you to update the notification later on.
//        mNotificationManager.notify(100, mBuilder.build());




    }

    class ViewAccidentClass extends AsyncTask<String,Void,String> {
        Context context;

        public ViewAccidentClass(Context context) {
            this.context = context;

        }

        @Override
        protected String doInBackground(String... params) {
            String userEmail = (String)params[0];
            String userPassword = (String)params[1];
            String appId = (String)params[2];

            String link = "http://192.168.43.245/APISystem/notifications/ViewAccidents.php";

            try{

                URL url = new URL(link);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

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
               // Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                myJSON = s;
               showList();
            }
            else {
                Toast.makeText(getApplicationContext(), "Not registered", Toast.LENGTH_LONG).show();
            }

        }
        protected void showList() {
            Toast.makeText(getApplicationContext(), "JSON :"+myJSON, Toast.LENGTH_LONG).show();
           try {
                JSONObject jsonObject = new JSONObject(myJSON);
                accidentsJSonArray = jsonObject.getJSONArray(TAG_RESULTS);
                for (int i = 0; i < accidentsJSonArray.length(); i++) {
                    JSONObject c = accidentsJSonArray.getJSONObject(i);
                    String accidentPlace = c.getString(TAG_ACCIDENT_PLACE);
                    String accidentInformer = c.getString(TAG_ACCIDENT_INFORMER);

                    HashMap<String, String> accidentArrayList2 = new HashMap<String, String>();
                    accidentArrayList2.put(TAG_ACCIDENT_PLACE, accidentPlace);
                    accidentArrayList2.put(TAG_ACCIDENT_INFORMER, accidentInformer);
                    accidentArrayList.add(accidentArrayList2);
                    ListAdapter listAdapter = new SimpleAdapter(HomeActivity.this, accidentArrayList, R.layout.list_accedents, new String[]
                            {TAG_ACCIDENT_PLACE, TAG_ACCIDENT_INFORMER}, new int[]{R.id.ac_place_tv, R.id.ac_info_tv});
                    listView.setAdapter(listAdapter);
//
               }
            } catch (JSONException e) {
               e.printStackTrace();
           }
        }

    }


}