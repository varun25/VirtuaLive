package reverseminds.virtualive;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.TextView;

/**
 * Created by Rajat on 31-03-2018.
 */

public class InfoPage extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.info_page);
        TextView t = findViewById(R.id.descc);
        t.setText(i.getStringExtra("info"));
    }
}
