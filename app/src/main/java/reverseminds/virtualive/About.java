package reverseminds.virtualive;

import android.app.Activity;
import android.os.Bundle;
import android.widget.VideoView;

/**
 * Created by Varun on 28-04-2018.
 */

public class About extends Activity {

    VideoView v;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
    }
}
