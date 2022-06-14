package com.example.a2011500192_visto_uas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.example.a2011500192_visto_uas.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private BottomSheetBehavior bottomSheetBehavior;

    private TextView textViewID, textViewName,textViewAvailability, textViewMapPhone, textViewDetails;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RelativeLayout relativeLayout = findViewById(R.id.update);
        relativeLayout.setOnClickListener(this);

        LinearLayout linearLayout = findViewById(R.id.design_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        id = getIntent().getStringExtra(DBConfiguration.MAP_ID);



        textViewName = (TextView) findViewById(R.id.mapname);
        textViewAvailability = (TextView) findViewById(R.id.mapavailability);
        textViewMapPhone = (TextView) findViewById(R.id.mapphone);
        textViewDetails = (TextView) findViewById(R.id.mapdetail);
        getMap();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-1011, 100.1211);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    private void getMap() {
        class GetMap extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MapsActivity.this, "Fetching...", "Wait...", false, false);
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
            Log.d("mapdebug", name);
            Log.d("mapdebug", ava);
            Log.d("mapdebug", pho);
            Log.d("mapdebug", det);
//            Log.d("mapdebug", lon);
            textViewName.setText(name);
            textViewAvailability.setText(ava);
            textViewMapPhone.setText(pho);
            textViewDetails.setText(det);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        startActivity(new Intent(MapsActivity.this, UpdateActivity.class));
    }
}