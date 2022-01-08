package reverseminds.virtualive;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import reverseminds.virtualive.login.Constants;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, PlaceSelectionListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;
    public com.sothree.slidinguppanel.SlidingUpPanelLayout l;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        pref = this.getSharedPreferences("VIRTUALIVE", Context.MODE_PRIVATE);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mmap);
        mapFragment.getMapAsync(this);

        l = findViewById(R.id.sliding_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        View header=navigationView.getHeaderView(0);

        TextView name = (TextView)header.findViewById(R.id.nav_uname);
        TextView email = (TextView)header.findViewById(R.id.nav_email);
        name.setText(pref.getString(Constants.NAME,"Username"));
        email.setText(pref.getString(Constants.EMAIL,"Email"));

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if ((l.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || l.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            l.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_feedback) {
            item.setChecked(true);
            Intent i = new Intent(MainActivity.this, Feedback.class);
            startActivity(i);

        } else if (id == R.id.nav_share) {
            Intent share = new Intent();
            share.setAction(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_TEXT, "Hey!" +
                    " Download VirtuaLive and Dream with your eyes open Wide! \nAvailable on google play store: " +
                    "\n\nhttps://play.google.com/store/apps/details?id=reverseminds.cmrit.virtualive");
            share.setType("text/plain");
            startActivity(share);
            return true;

        } else if (id == R.id.nav_about) {
            item.setChecked(true);
            Intent i = new Intent(MainActivity.this, About.class);
            startActivity(i);

        } else if (id == R.id.nav_logout) {
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.putBoolean(Constants.IS_LOGGED_IN,false);
            editor.putString(Constants.EMAIL,"");
            editor.putString(Constants.NAME,"");
            editor.putString(Constants.UNIQUE_ID,"");
            editor.apply();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            Log.d("VIRTUALIVE","MAIN ACTIVITY LOGOUT");
            Log.d("VIRTUALIVE", "MAIN ACTIVITY"+ pref.getBoolean(Constants.IS_LOGGED_IN,false)+"");
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


        @Override
    public void onError(Status status) {

    }

    Marker clickedMarker;
    int markerIndex;

    public boolean onMarkerClick(final Marker marker) {
//        Toast.makeText(this,
//                marker.getTitle() +
//                        " has been clicked " ,
//                Toast.LENGTH_SHORT).show();
        TextView t = findViewById(R.id.place_name);
        t.setText(marker.getTitle());
        clickedMarker = marker;
        l.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        return false;
    }

    GoogleMap mymap;

    List<MarkerModel> markerList;

    public List getList() throws Exception{
        markerList = new ArrayList<>();

        InputStream is = getResources().openRawResource(R.raw.markers);
        Writer writer = new StringWriter();
        char[] buffer = new char[4096];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            is.close();
        }

        String jsonStr = writer.toString();
        Log.d("Json", jsonStr);

        if (jsonStr != null) {
            try {
                JSONArray jsonObj = new JSONArray(jsonStr);

                for (int i = 0; i < 22; i++) {
                    JSONObject c = jsonObj.getJSONObject(i);
                    MarkerModel m = new MarkerModel();
                    m.setId(c.getString("id"));
                    m.setName(c.getString("name"));
                    m.setLongi(c.getString("long"));
                    m.setLat(c.getString("lat"));
                    m.setDesc(c.getString("desc"));
                    markerList.add(m);
                    Log.d("Markers:", m.getName());
                }

            } catch (final JSONException e) {

            }
        }
        return markerList;
    }

    public void onMapReady(GoogleMap googleMap) {
        mymap = googleMap;
        boolean success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.map_style));
        //mymap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //Json json = new Json();
        //List<MarkerModel> markerList = new ArrayList();
        //markerList = getList();
        try {
            getList();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for(int i=0; i<markerList.size(); i++)
        {
            LatLng obj1 = new LatLng(Float.parseFloat(markerList.get(i).getLat()), Float.parseFloat(markerList.get(i).getLongi()));
            mymap.addMarker(new MarkerOptions().position(obj1).title(markerList.get(i).getName()));
        }
        mymap.setOnMarkerClickListener(this);
        mymap.setOnMapClickListener(this);

        LatLng coordinate = new LatLng(12.998715, 77.592027);
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 11);
        Toast.makeText(getBaseContext(), "Zooming to your Current City", Toast.LENGTH_SHORT).show();
        mymap.animateCamera(yourLocation);

//        try {
//            googleMap.setMyLocationEnabled(true);
//            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//            Criteria criteria = new Criteria();
//            String provider = locationManager.getBestProvider(criteria, true);
//            Location location = locationManager.getLastKnownLocation(provider);
//
//            if (location != null) {
//                double latitude = location.getLatitude();
//                double longitude = location.getLongitude();
//                LatLng latLng = new LatLng(latitude, longitude);
//
//                LatLng myPosition = new LatLng(latitude, longitude);
//
//                LatLng coordinate = new LatLng(12.998715, 77.592027);
//                CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 11);
//                Toast.makeText(getBaseContext(), "Zooming to your Current City", Toast.LENGTH_SHORT).show();
//                mymap.animateCamera(yourLocation);
//            }
//        }
//        catch (SecurityException e){}
    }

    @Override
    public void onPlaceSelected(Place place) {
        Toast.makeText(getBaseContext(), "Place selected : " + place.getName() + " " +
                place.getLatLng().latitude + " " + place.getLatLng().longitude, Toast.LENGTH_LONG).show();

        LatLng newplace = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
//        mymap.addMarker(new MarkerOptions().position(newplace).title(place.getName().toString())).setTag(1);
        mymap.moveCamera(CameraUpdateFactory.newLatLng(newplace));

        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(newplace, 16);
        mymap.animateCamera(yourLocation);
    }

    public void arview(View v)
    {
        Intent i = new Intent(MainActivity.this, ArViewActivity.class);
        startActivity(i);
    }

    public void vrview(View v)
    {
        Intent i = new Intent(MainActivity.this, VrVideoActivity.class);

        try {
            if (clickedMarker.getTitle().contains("Taj Mahal"))
                i.putExtra("video", "taj");
            else {
                i.putExtra("video", "bangalore");
                if (clickedMarker.getTitle().contains("Vidhana"))
                    i.putExtra("jump", 0);
                else if (clickedMarker.getTitle().contains("Central"))
                    i.putExtra("jump", 80000);
                else if (clickedMarker.getTitle().contains("Chinnaswamy"))
                    i.putExtra("jump", 120000);
                else if (clickedMarker.getTitle().contains("Lalbagh"))
                    i.putExtra("jump", 164000);
                else if (clickedMarker.getTitle().contains("Lalbagh Lake"))
                    i.putExtra("jump", 200000);
                else if (clickedMarker.getTitle().contains("ITPL"))
                    i.putExtra("jump", 220000);
                else
                    i.putExtra("jump", 0);
            }
            startActivity(i);
        }
        catch (Exception e)
        {
            Toast.makeText(getBaseContext(), "Please Click a Marker", Toast.LENGTH_SHORT).show();
        }
    }

    public void infoview(View v)
    {
        try {
            Intent i = new Intent(MainActivity.this, InfoPage.class);
            int index;
            if(clickedMarker.getId().toString().length()==2)
                index = Integer.parseInt(String.valueOf(clickedMarker.getId().toString().charAt(1)));
            else
                index = Integer.parseInt(String.valueOf(clickedMarker.getId().toString().substring(1,3)));
            i.putExtra("info", markerList.get(index).getDesc());
            startActivity(i);
        }
        catch (Exception e)
        {
            Toast.makeText(getBaseContext(), "Please Click a Marker", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if ((l.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || l.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            l.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
    }
}