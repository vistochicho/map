package com.example.a2011500192_visto_uas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;

public class CreateActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editNama;
    private EditText editAva;
    private EditText editPho;
    private EditText editDet;
    private TextView getCurentLocation, textLat, textLong, directButton;
    private ProgressBar progressBar;

    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        getCurentLocation = findViewById(R.id.getCurrentLocation);
        textLat = findViewById(R.id.textLat);
        textLong = findViewById(R.id.textLong);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
                CreateActivity.this
        );

        getCurentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(CreateActivity.this
                , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(CreateActivity.this
                , Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                } else {
                    ActivityCompat.requestPermissions(CreateActivity.this
                    , new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                    ,Manifest.permission.ACCESS_COARSE_LOCATION}
                    ,100);
                }
            }
        });

        editNama = findViewById(R.id.editname);
        editAva = findViewById(R.id.editava);
        editPho = findViewById(R.id.editpho);
        editDet = findViewById(R.id.editdet);
        directButton = findViewById(R.id.directbutton);

        directButton.setOnClickListener(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)){
            getCurrentLocation();
        } else {
            Toast.makeText(getApplicationContext(), "Denied!", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE
        );

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @SuppressLint("MissingPermission")
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();

                    if(location != null) {
                        textLat.setText(String.valueOf(location.getLatitude()));
                        textLong.setText(String.valueOf(location.getLongitude()));
                    } else {
                        LocationRequest locationRequest = new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);

                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                                Location location1 = locationResult.getLastLocation();
                                textLat.setText(String.valueOf(location.getLatitude()));
                                textLong.setText(String.valueOf(location.getLongitude()));

                            }
                        };
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest
                        , locationCallback, Looper.myLooper());
                    }
                }
            });
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    private void addMap(){
        final String name = editNama.getText().toString().trim();
        final String ava = editAva.getText().toString().trim();
        final String pho = editPho.getText().toString().trim();
        final String det = editDet.getText().toString().trim();
        final String lat = textLat.getText().toString().trim();
        final String lon = textLong.getText().toString().trim();


        class addMap extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(CreateActivity.this,"Input...","Please Wait...",false,false);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(CreateActivity.this,s,Toast.LENGTH_LONG).show();
            }
            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put(DBConfiguration.KEY_MHS_MAP_NAME,name);
                params.put(DBConfiguration.KEY_MHS_MAP_AVAILABILITY,ava);
                params.put(DBConfiguration.KEY_MHS_MAP_PHONE,pho);
                params.put(DBConfiguration.KEY_MHS_MAP_ADDRESS,det);
                params.put(DBConfiguration.KEY_MHS_MAP_LAT,lat);
                params.put(DBConfiguration.KEY_MHS_MAP_LONG,lon);


                DBRequestHandler rh = new DBRequestHandler();
                String res = rh.sendPostRequest(DBConfiguration.URL_ADD, params);
                Log.d("res",res);
                return res;
            }
        }
        addMap ae = new addMap();
        ae.execute();
    }

    @Override
    public void onClick(View view) {
        addMap();
        startActivity(new Intent(CreateActivity.this, MainActivity.class));
    }
}