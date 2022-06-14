package com.example.a2011500192_visto_uas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class UpdateActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editNama;
    private EditText editAva;
    private EditText editPho;
    private EditText editDet;
    private TextView textLat, textLong, updateButton;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        id = getIntent().getStringExtra(DBConfiguration.MAP_ID);
        editNama = findViewById(R.id.upeditname);
        editAva = findViewById(R.id.upeditava);
        editPho = findViewById(R.id.upeditpho);
        editDet = findViewById(R.id.upeditdet);
        textLat = findViewById(R.id.uptextLat);
        textLong = findViewById(R.id.uptextLong);
        updateButton = findViewById(R.id.updateButton);
        updateButton.setOnClickListener(this);
        getMap();

    }

    private void getMap() {
        class GetMap extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(UpdateActivity.this, "Fetching...", "Wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                showMap(s);
            }

            @Override
            protected String doInBackground(Void... voids) {
                DBRequestHandler rh = new DBRequestHandler();
                String s = rh.sendGetRequestParam(DBConfiguration.URL_GET_MHS, id);
                Log.d("testing","test");
                Log.d("response",s);
                return s;
            }
        }
        GetMap ge = new GetMap();
        ge.execute();
    }

    private void showMap(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray(DBConfiguration.TAG_JSON_ARRAY);
            JSONObject c = result.getJSONObject(0);
            String name = c.getString(DBConfiguration.TAG_MAP_NAME);
            String ava = c.getString(DBConfiguration.TAG_MAP_AVAILABILITY);
            String pho = c.getString(DBConfiguration.TAG_MAP_PHONE);
            String det = c.getString(DBConfiguration.TAG_MAP_ADDRESS);
            String lat = c.getString(DBConfiguration.TAG_MAP_LAT);
            String lon = c.getString(DBConfiguration.TAG_MAP_LONG);


            Log.d("mapdebug", name);
            Log.d("mapdebug", ava);
            Log.d("mapdebug", pho);
            Log.d("mapdebug", det);
            Log.d("mapdebug", lat);
            Log.d("mapdebug", lon);
            editNama.setText(name);
            editAva.setText(ava);
            editPho.setText(pho);
            editDet.setText(det);
            editDet.setText(det);
            textLat.setText(lat);
            textLong.setText(lon);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateMap() {
        final String name = editNama.getText().toString().trim();
        final String ava = editAva.getText().toString().trim();
        final String pho = editPho.getText().toString().trim();
        final String det = editDet.getText().toString().trim();
        final String lat = textLat.getText().toString().trim();
        final String lon = textLong.getText().toString().trim();

        class UpdateMahasiswa extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(UpdateActivity.this, "Updating...", "Wait...", false,
                        false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(UpdateActivity.this, s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(DBConfiguration.KEY_MHS_ID, id);
                hashMap.put(DBConfiguration.KEY_MHS_MAP_NAME, name);
                hashMap.put(DBConfiguration.KEY_MHS_MAP_AVAILABILITY, ava);
                hashMap.put(DBConfiguration.KEY_MHS_MAP_PHONE, pho);
                hashMap.put(DBConfiguration.KEY_MHS_MAP_ADDRESS, det);
                hashMap.put(DBConfiguration.KEY_MHS_MAP_LAT, lat);
                hashMap.put(DBConfiguration.KEY_MHS_MAP_LONG, lon);

                DBRequestHandler rh = new DBRequestHandler();
                String s = rh.sendPostRequest(DBConfiguration.URL_UPDATE_MHS, hashMap);
                Log.d("Update", s);
                return s;
            }
        }
        UpdateMahasiswa ue = new UpdateMahasiswa();
        ue.execute();
    }


    @Override
    public void onClick(View v) {
        updateMap();
//        startActivity(new Intent(UpdateActivity.this, MainActivity.class));
    }
}