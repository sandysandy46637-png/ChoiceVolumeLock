package com.prudhviraj.choicevolumelock;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public final class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        TextView text = new TextView(this);
        text.setPadding(40, 60, 40, 40);
        text.setText(
            "Choice Volume Lock\n\n" +
            "LSPosed module.\n\n" +
            "Target: in.swiggy.deliveryapp.choice\n" +
            "Behavior: ignore Choice attempts to change STREAM_MUSIC volume.\n\n" +
            "Enable this app in LSPosed and scope it ONLY to Choice Delivery."
        );
        setContentView(text);
    }
}
