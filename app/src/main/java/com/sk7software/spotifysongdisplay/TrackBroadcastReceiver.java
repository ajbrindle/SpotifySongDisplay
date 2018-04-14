package com.sk7software.spotifysongdisplay;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import com.sk7software.spotifysongdisplay.util.TextToSpeechUtil;

public class TrackBroadcastReceiver extends Service implements TextToSpeech.OnInitListener {

    private static final String TAG = TrackBroadcastReceiver.class.getSimpleName();

    private String trackName;
    private String artistName;

    static final class BroadcastTypes {
        static final String SPOTIFY_PACKAGE = "com.spotify.music";
        static final String PLAYBACK_STATE_CHANGED = SPOTIFY_PACKAGE + ".playbackstatechanged";
        static final String QUEUE_CHANGED = SPOTIFY_PACKAGE + ".queuechanged";
        static final String METADATA_CHANGED = SPOTIFY_PACKAGE + ".metadatachanged";
    }

    private final BroadcastReceiver trackReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long timeSentInMs = intent.getLongExtra("timeSent", 0L);

            // Ensure there is a context for preferences
            PreferencesUtil.init(context);

            boolean active = PreferencesUtil.getInstance().getBooleanPreference(PreferencesUtil.PREFERNECE_ACTIVE);

            if (active) {
                String action = intent.getAction();

                if (action.equals(BroadcastTypes.METADATA_CHANGED)) {
                    String trackId = intent.getStringExtra("id");

                    if (trackChanged(trackId)) {
                        artistName = intent.getStringExtra("artist");
                        trackName = intent.getStringExtra("track");
                        Log.d(TAG, "Track: " + trackName + "; Artist: " + artistName + " (" + trackId + ")");
                        PreferencesUtil.getInstance().addPreference(PreferencesUtil.PREFERENCE_LAST_TRACK, trackId);

                        if (trackName != null && !"".equals(trackName)) {
                            if (PreferencesUtil.getInstance().getBooleanPreference(PreferencesUtil.PREFERNECE_SHOW_TRACK)) {
                                // Start overlay activity that displays title
                                Intent i = new Intent(context, OverlayActivity.class);
                                i.putExtra("track", trackName);
                                i.putExtra("artist", artistName);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(i);
                            }
                            if(PreferencesUtil.getInstance().getBooleanPreference(PreferencesUtil.PREFERNECE_TTS)) {
                                if (!TextToSpeechUtil.getInstance().isRunning()) {
                                    TextToSpeechUtil.init(context, TrackBroadcastReceiver.this);
                                } else {
                                    speakTrack();
                                }
                            }
                        }
                    }
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastTypes.METADATA_CHANGED);
        registerReceiver(trackReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(trackReceiver);
        TextToSpeechUtil.getInstance().destroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean trackChanged(String id) {
        String lastTrack = PreferencesUtil.getInstance().getStringPreference(PreferencesUtil.PREFERENCE_LAST_TRACK);
        return !id.equals(lastTrack);
    }

    @Override
    public void onInit(int status) {
        TextToSpeechUtil.getInstance().onInit(status, trackName + " by " + artistName);
    }

    private void speakTrack() {
        TextToSpeechUtil.getInstance().saySomething(trackName + " by " + artistName,
                TextToSpeech.QUEUE_FLUSH);
    }
}
