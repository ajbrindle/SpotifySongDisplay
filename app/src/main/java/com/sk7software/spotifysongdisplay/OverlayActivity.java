package com.sk7software.spotifysongdisplay;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class OverlayActivity extends Activity {

    private int textSize = 36;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overlay);

        int position = PreferencesUtil.getInstance().getIntPreference(PreferencesUtil.PREFERENCE_DISPLAY_POSITION);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams wlp = getWindow().getAttributes();

        switch (position) {
            case 0:
                wlp.gravity = Gravity.TOP;
                break;
            case 1:
                wlp.gravity = Gravity.CENTER_VERTICAL;
                break;
            case 2:
                wlp.gravity = Gravity.BOTTOM;
                break;
            default:
                wlp.gravity = Gravity.CENTER_VERTICAL;
        }
        getWindow().setAttributes(wlp);
        setSongText();
    }

    @Override
    public void onResume() {
        super.onResume();
        setSongText();
    }

    private void setSongText() {
        String track = getIntent().getStringExtra("track");
        String artist = getIntent().getStringExtra("artist");

        // Repeat the track/artist a few times to make sure string is long enough to scroll
        // Bit of a hack, but saves a lot of fiddly code
        StringBuilder songDisplay = new StringBuilder();
        for (int i=0; i<4; i++) {
            songDisplay.append("     ");
            songDisplay.append(track);
            songDisplay.append("/");
            songDisplay.append(artist);
            songDisplay.append("    ");
        }

        int textSize = PreferencesUtil.getInstance().getIntPreference(PreferencesUtil.PREFERENCE_TEXT_SIZE);

        TextView txt = (TextView) findViewById(R.id.txtSong);
        txt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
        txt.setTextColor(Color.WHITE);
        txt.setTypeface(Typeface.DEFAULT_BOLD);
        txt.setText(songDisplay.toString());
        txt.setSelected(true);

        boolean screenOn = PreferencesUtil.getInstance().getBooleanPreference(PreferencesUtil.PREFERNECE_SCREEN_ON);
        if (screenOn) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        }
    }

    public void onClick(View v) {
//        TextView txt = (TextView)findViewById(R.id.txtSong);
//        txt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, ++textSize);
        finish();
    }

    /**
     * Pad a target string of text with spaces on the right to fill a target
     * width
     *
     * @param text  The target text
     * @param paint The TextPaint used to measure the target text and
     *              whitespaces
     * @param width The target width to fill
     * @return the original text with extra padding to fill the width
     */
    public static CharSequence padText(CharSequence text, TextPaint paint, int width) {

        // First measure the width of the text itself
        Rect textbounds = new Rect();
        paint.getTextBounds(text.toString(), 0, text.length(), textbounds);

        /**
         * check to see if it does indeed need padding to reach the target width
         */
        if (textbounds.width() > width) {
            return text;
        }

    /*
     * Measure the text of the space character (there's a bug with the
     * 'getTextBounds() method of Paint that trims the white space, thus
     * making it impossible to measure the width of a space without
     * surrounding it in arbitrary characters)
     */
        String workaroundString = "a a";
        Rect spacebounds = new Rect();
        paint.getTextBounds(workaroundString, 0, workaroundString.length(), spacebounds);

        Rect abounds = new Rect();
        paint.getTextBounds(new char[]{
                'a'
        }, 0, 1, abounds);

        float spaceWidth = spacebounds.width() - (abounds.width() * 2);

    /*
     * measure the amount of spaces needed based on the target width to fill
     * (using Math.ceil to ensure the maximum whole number of spaces)
     */
        int amountOfSpacesNeeded = (int) Math.ceil(2 * (width - textbounds.width()) / spaceWidth);

        // pad with spaces til the width is less than the text width
        return amountOfSpacesNeeded > 0 ? padRight(text.toString(), text.toString().length()
                + amountOfSpacesNeeded) : text;
    }

    /**
     * Pads a string with white space on the right of the original string
     *
     * @param s The target string
     * @param n The new target length of the string
     * @return The target string padded with whitespace on the right to its new
     * length
     */
    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }
}
