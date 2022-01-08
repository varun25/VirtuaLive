package reverseminds.virtualive;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

/**
 * Created by Varun on 27-04-2018.
 */

public class Splash extends Activity {

    VideoView v;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        v = findViewById(R.id.videoView);
        v.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash));
        v.start();

        Thread timer = new Thread(){
        public void run(){
            try {
                sleep(5000);
            } catch (InterruptedException e) {
            }
            Splash.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {

                    } finally {
                        Intent i = new Intent(Splash.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            });
        }
        };
        timer.start();
    }
}
