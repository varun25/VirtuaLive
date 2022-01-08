package reverseminds.virtualive;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.pikkart.ar.geo.GeoElement;
import com.pikkart.ar.geo.MarkerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * Created by Varun on 31-10-2017.
 */

public class ArViewActivity extends com.pikkart.ar.geo.GeoActivity implements OnMapReadyCallback{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    int toggle=0;

    private void init() {
        MyMarkerViewAdapter arMyMarkerViewAdapter = new MyMarkerViewAdapter(this, 51, 73);

        MyMarkerViewAdapter mapMyMarkerViewAdapter = new MyMarkerViewAdapter(this, 30, 43);

        initGeoFragment(arMyMarkerViewAdapter, mapMyMarkerViewAdapter);

        //initGeoFragment();

        Location loc1 = new Location("loc1");
        loc1.setLatitude(12.966388);
        loc1.setLongitude(77.711705);

        Location loc2 = new Location("loc2");
        loc2.setLatitude(12.967190);
        loc2.setLongitude(77.712692);

        Location loc3 = new Location("loc3");
        loc3.setLatitude(12.966526);
        loc3.setLongitude(77.711029);

        Location loc4 = new Location("loc4");
        loc4.setLatitude(12.966064);
        loc4.setLongitude(77.712172);


        List<GeoElement> geoElementList = new ArrayList<>();

        geoElementList.add(new GeoElement(loc1, "1", "Ground"));
        geoElementList.add(new GeoElement(loc2, "2", "Parking"));
        geoElementList.add(new GeoElement(loc3, "3", "Civil Dept"));
        geoElementList.add(new GeoElement(loc4, "4", "Library"));

        setGeoElements(geoElementList);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
        params.setMargins(20, 1, 20, 20);
        params.height = 200;

        FrameLayout ll = findViewById(R.id.activity_pikkart_geo_layout);
        TextView bb = new TextView(this);
        bb.setLayoutParams(params);
        bb.setTranslationZ(5);
        bb.setBackgroundColor(Color.WHITE);
        bb.setTextColor(Color.MAGENTA);
        bb.setGravity(Gravity.CENTER);
        bb.setHint("Move the Phone Around to have \nthe Perfect Experience!");
        bb.setHintTextColor(Color.BLUE);
        ll.addView(bb);

    }

    public void onMapReady(GoogleMap googleMap) {
//        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//        LatLng sydney = new LatLng(-33.852, 151.211);
//        googleMap.addMarker(new MarkerOptions().position(sydney)
//                .title("Marker in Sydney"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public class MyMarkerViewAdapter extends MarkerViewAdapter
    {
        Context ctx;
        public MyMarkerViewAdapter(Context context, int width, int height) {
            super(context, width, height);
            ctx = context;
        }

        @Override
        public View getView(GeoElement geoElement)
        {
            ImageView imageView = (ImageView)getMarkerView().findViewById(R.id.image);
            imageView.setImageBitmap(textAsBitmap(geoElement.getName(), 50, Color.BLACK));
            imageView.invalidate();
            return imageView;
        }

        @Override
        public View getSelectedView(GeoElement geoElement)
        {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
            params.setMargins(20, 1, 20, 20);
            params.height = 400;

            FrameLayout ll = findViewById(R.id.activity_pikkart_geo_layout);
            TextView bb = new TextView(ctx);
            bb.setLayoutParams(params);
            bb.setTranslationZ(5);
            bb.setBackgroundColor(Color.WHITE);
            bb.setTextColor(Color.MAGENTA);
            bb.setGravity(Gravity.CENTER);
            bb.setPadding(20,20,20,20);
            bb.setHint("A tourist attraction is a place of interest where tourists visit, typically for its inherent or exhibited natural or cultural value, historical significance, natural or built beauty, offering leisure and amusement.");
            bb.setHintTextColor(Color.MAGENTA);
            ll.addView(bb);

            ImageView imageView = (ImageView) getMarkerView().findViewById(R.id.image);
            imageView.setImageResource(R.drawable.map_marker_blue);
            imageView.invalidate();
            return imageView;
        }

        public Bitmap textAsBitmap(String text, float textSize, int textColor) {
            Paint paint = new Paint(ANTI_ALIAS_FLAG);
            paint.setTextSize(textSize);
            paint.setColor(textColor);
            paint.setTextAlign(Paint.Align.LEFT);

            float baseline = -paint.ascent();
            int width = (int) (paint.measureText(text) + 1.5f);
            int height = (int) (baseline + paint.descent() + 1.5f);
            Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(image);
            canvas.drawText(text, 0, baseline, paint);
            //canvas.drawRect(80, 80,80,80 , paint);
            return image;
        }
    }
}
