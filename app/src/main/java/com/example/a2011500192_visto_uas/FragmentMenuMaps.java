package com.example.a2011500192_visto_uas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class FragmentMenuMaps extends Fragment implements AdapterView.OnItemClickListener {

    private ListView listView;
    private String JSON_STRING;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu_maps, container, false);
        listView = (ListView) view.findViewById(R.id.listView);
        TextView textView = view.findViewById(R.id.mapavailability);
        listView.setOnItemClickListener(this);
        getJSON();
        return view;
    }

    private void showMap(){
        JSONObject jsonObject = null;
        ArrayList<HashMap<String,String>> list = new ArrayList<>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(DBConfiguration.TAG_JSON_ARRAY);
            for(int i = 0; i<result.length(); i++){
                JSONObject jo = result.getJSONObject(i);
                String id = jo.getString(DBConfiguration.TAG_ID);
                String name = jo.getString(DBConfiguration.TAG_MAP_NAME);
                String address = jo.getString(DBConfiguration.TAG_MAP_ADDRESS);
                String phone = jo.getString(DBConfiguration.TAG_MAP_PHONE);
                String availability = jo.getString(DBConfiguration.TAG_MAP_AVAILABILITY);

                HashMap<String,String> mhs = new HashMap<>();
                mhs.put(DBConfiguration.TAG_ID,id);
                mhs.put(DBConfiguration.TAG_MAP_NAME,name);
                mhs.put(DBConfiguration.TAG_MAP_ADDRESS,address);
                mhs.put(DBConfiguration.TAG_MAP_PHONE,phone);
                mhs.put(DBConfiguration.TAG_MAP_AVAILABILITY,availability);
                list.add(mhs);
            }
        }
        catch (JSONException e) { e.printStackTrace();
        }
        ListAdapter adapter = new SimpleAdapter(
                getActivity(), list, R.layout.list_menu_map,
                new String[]{DBConfiguration.TAG_MAP_NAME, DBConfiguration.TAG_MAP_ADDRESS, DBConfiguration.TAG_MAP_AVAILABILITY},
                new int[]{R.id.mapnama, R.id.mapaddress, R.id.mapavailability});
        listView.setAdapter(adapter);
    }
    private void getJSON(){
        class GetJSON extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(getActivity(),"Fetching Data","Please Wait...",false,false);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                showMap();
            }
            @Override protected String doInBackground(Void... params) { DBRequestHandler rh = new DBRequestHandler();
                Log.d("testing","test");
                String s = rh.sendGetRequest(DBConfiguration.URL_GET_ALL);
                Log.d("response",s);

                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), MapsActivity.class);
        HashMap<String,String> map =(HashMap)parent.getItemAtPosition(position);
        String mapId = map.get(DBConfiguration.TAG_ID).toString();
        String latId = map.get(DBConfiguration.TAG_MAP_LAT);
        String longId = map.get(DBConfiguration.TAG_MAP_LONG);
        intent.putExtra(DBConfiguration.MAP_ID,mapId);
        intent.putExtra(DBConfiguration.MAP_LAT,latId);
        intent.putExtra(DBConfiguration.MAP_LONG,longId);
        startActivity(intent);
    }
}