package reverseminds.virtualive;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Pair;
import android.widget.SeekBar;
import android.widget.Toast;
import com.google.vr.sdk.widgets.video.VrVideoEventListener;
import com.google.vr.sdk.widgets.video.VrVideoView;
import com.google.vr.sdk.widgets.video.VrVideoView.Options;

import java.io.IOException;

public class VrVideoActivity extends Activity {

  private static final String TAG = VrVideoActivity.class.getSimpleName();
  private static final String STATE_IS_PAUSED = "isPaused";
  private static final String STATE_PROGRESS_TIME = "progressTime";
  private static final String STATE_VIDEO_DURATION = "videoDuration";
  public static final int LOAD_VIDEO_STATUS_UNKNOWN = 0;
  public static final int LOAD_VIDEO_STATUS_SUCCESS = 1;
  public static final int LOAD_VIDEO_STATUS_ERROR = 2;
  private int loadVideoStatus = LOAD_VIDEO_STATUS_UNKNOWN;
  private Uri fileUri;
  private Options videoOptions = new Options();
  private VideoLoaderTask backgroundVideoLoaderTask;
  String vidname;
  int seek=0;
  protected VrVideoView videoWidgetView;
  private SeekBar seekBar;
  private boolean isPaused = false;
  private boolean firstTime=true;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_video_vr);
    Intent i = getIntent();
    vidname = i.getStringExtra("video");
    seek = i.getIntExtra("jump", 0);
    //vidname = "bangalore";
    seekBar = (SeekBar) findViewById(R.id.seek_bar);
    seekBar.setOnSeekBarChangeListener(new SeekBarListener());
    videoWidgetView = (VrVideoView) findViewById(R.id.video_view);
    videoWidgetView.setEventListener(new ActivityEventListener());

    final AlertDialog.Builder dialog = new AlertDialog.Builder(VrVideoActivity.this);
    dialog.setTitle("360 video")
            .setMessage("Use a VR headset for best experience, switch to VR mode using the option in the right bottom corner")
            .setCancelable(false)
            .setNegativeButton("OK! Got it!", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface paramDialogInterface, int paramInt) {
              }
            });
    dialog.show();

    loadVideoStatus = LOAD_VIDEO_STATUS_UNKNOWN;
    handleIntent(getIntent());
  }

  @Override
  protected void onNewIntent(Intent intent) {
    Log.i(TAG, this.hashCode() + ".onNewIntent()");
    setIntent(intent);
    handleIntent(intent);
  }

  private void handleIntent(Intent intent) {
    if (Intent.ACTION_VIEW.equals(intent.getAction())) {
      Log.i(TAG, "ACTION_VIEW Intent received");

      fileUri = intent.getData();
      if (fileUri == null) {
        Log.w(TAG, "No data uri specified. Use \"-d /path/filename\".");
      } else {
        Log.i(TAG, "Using file " + fileUri.toString());
      }

      videoOptions.inputFormat = intent.getIntExtra("inputFormat", Options.FORMAT_DEFAULT);
      videoOptions.inputType = intent.getIntExtra("inputType", Options.TYPE_MONO);
    } else {
      Log.i(TAG, "Intent is not ACTION_VIEW. Using the default video.");
      fileUri = null;
    }

    if (backgroundVideoLoaderTask != null) {
      backgroundVideoLoaderTask.cancel(true);
    }
    backgroundVideoLoaderTask = new VideoLoaderTask();
    backgroundVideoLoaderTask.execute(Pair.create(fileUri, videoOptions));
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    savedInstanceState.putLong(STATE_PROGRESS_TIME, videoWidgetView.getCurrentPosition());
    savedInstanceState.putLong(STATE_VIDEO_DURATION, videoWidgetView.getDuration());
    savedInstanceState.putBoolean(STATE_IS_PAUSED, isPaused);
    super.onSaveInstanceState(savedInstanceState);
  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    long progressTime = savedInstanceState.getLong(STATE_PROGRESS_TIME);
    videoWidgetView.seekTo(progressTime);
    seekBar.setMax((int) savedInstanceState.getLong(STATE_VIDEO_DURATION));
    seekBar.setProgress((int) progressTime);

    isPaused = savedInstanceState.getBoolean(STATE_IS_PAUSED);
    if (isPaused) {
      videoWidgetView.pauseVideo();
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    videoWidgetView.pauseRendering();
    isPaused = true;
  }

  @Override
  protected void onResume() {
    super.onResume();
    videoWidgetView.resumeRendering();
   }

  @Override
  protected void onDestroy() {
    videoWidgetView.shutdown();
    super.onDestroy();
  }

  private void togglePause() {
    if (isPaused) {
      videoWidgetView.playVideo();
    } else {
      videoWidgetView.pauseVideo();
    }
    isPaused = !isPaused;
    updateStatusText();
  }

  private void updateStatusText() {
    StringBuilder status = new StringBuilder();
    status.append(isPaused ? "Paused: " : "Playing: ");
    status.append(String.format("%.2f", videoWidgetView.getCurrentPosition() / 1000f));
    status.append(" / ");
    status.append(videoWidgetView.getDuration() / 1000f);
    status.append(" seconds.");
  }

   private class SeekBarListener implements SeekBar.OnSeekBarChangeListener {
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
      if (fromUser) {
        videoWidgetView.seekTo(progress);
        updateStatusText();
      }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) { }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) { }
  }

   private class ActivityEventListener extends VrVideoEventListener {
     @Override
    public void onLoadSuccess() {
      Log.i(TAG, "Successfully loaded video " + videoWidgetView.getDuration());
      loadVideoStatus = LOAD_VIDEO_STATUS_SUCCESS;
      seekBar.setMax((int) videoWidgetView.getDuration());
      updateStatusText();
      Toast.makeText(getApplicationContext(), seek+"", Toast.LENGTH_SHORT).show();
      if(firstTime==true) videoWidgetView.seekTo(seek);
      firstTime = false;
    }

    @Override
    public void onLoadError(String errorMessage) {
       loadVideoStatus = LOAD_VIDEO_STATUS_ERROR;
      Toast.makeText(
          VrVideoActivity.this, "Error loading video: " + errorMessage, Toast.LENGTH_LONG)
          .show();
      Log.e(TAG, "Error loading video: " + errorMessage);
    }

    @Override
    public void onClick() {
      togglePause();
    }

    @Override
    public void onNewFrame() {
      updateStatusText();
      seekBar.setProgress((int) videoWidgetView.getCurrentPosition());
    }

    @Override
    public void onCompletion() {
      videoWidgetView.seekTo(0);
    }
  }

  class VideoLoaderTask extends AsyncTask<Pair<Uri, Options>, Void, Boolean> {
    @Override
    protected Boolean doInBackground(Pair<Uri, Options>... fileInformation) {
      try {
         if (fileInformation == null || fileInformation.length < 1
          || fileInformation[0] == null || fileInformation[0].first == null) {
          Options options = new Options();
          options.inputType = Options.TYPE_MONO;
          videoWidgetView.loadVideoFromAsset(vidname + ".mp4", options);
          //videoWidgetView.loadVideo(Uri.parse("https://www.youtube.com/watch?v=r71a5ClzrMw"), options);
         } else {
          videoWidgetView.loadVideo(fileInformation[0].first, fileInformation[0].second);
        }
      } catch (IOException e) {
        loadVideoStatus = LOAD_VIDEO_STATUS_ERROR;
        videoWidgetView.post(new Runnable() {
          @Override
          public void run() {
            Toast
                .makeText(VrVideoActivity.this, "Error opening file. ", Toast.LENGTH_LONG)
                .show();
          }
        });
        Log.e(TAG, "Could not open video: " + e);
      }

      return true;
    }
  }
}
