package com.sk7software.spotifysongdisplay;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class TrackBroadcastReceiver extends Service {

    private static final String TAG = TrackBroadcastReceiver.class.getSimpleName();

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

            boolean showTrack = PreferencesUtil.getInstance().getBooleanPreference(PreferencesUtil.PREFERNECE_SHOW_TRACK);

            if (showTrack) {
                String action = intent.getAction();

                if (action.equals(BroadcastTypes.METADATA_CHANGED)) {
                    String trackId = intent.getStringExtra("id");

                    if (trackChanged(trackId)) {
                        String artistName = intent.getStringExtra("artist");
                        String trackName = intent.getStringExtra("track");
                        Log.d(TAG, "Track: " + trackName + "; Artist: " + artistName + " (" + trackId + ")");
                        PreferencesUtil.getInstance().addPreference(PreferencesUtil.PREFERENCE_LAST_TRACK, trackId);

                        if (trackName != null && !"".equals(trackName)) {
                            // Start overlay activity that displays title
                            Intent i = new Intent(context, OverlayActivity.class);
                            i.putExtra("track", trackName);
                            i.putExtra("artist", artistName);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(i);
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
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean trackChanged(String id) {
        String lastTrack = PreferencesUtil.getInstance().getStringPreference(PreferencesUtil.PREFERENCE_LAST_TRACK);
        return !id.equals(lastTrack);
    }
}
