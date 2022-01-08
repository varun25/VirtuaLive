package reverseminds.virtualive;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

/**
 * Created by Varun on 28-04-2018.
 */

public class Feedback extends Activity {

    VideoView v;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);
    }

    public void sendFeed(View v)
    {
        Toast.makeText(getBaseContext(), "Feedback submitted Successfully", Toast.LENGTH_LONG).show();
        finish();
    }
}
