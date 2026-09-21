package com.miui.mediaviewer;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * Receives MiuiCamera's "play the video I just recorded" request and hands it
 * to whatever handles ACTION_VIEW for video on this ROM.
 */
public class RedirectActivity extends Activity {
    private static final String TAG = "MiuiMediaViewerStub";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            forward();
        } finally {
            // Theme.NoDisplay requires finishing before onResume.
            finish();
        }
    }

    private void forward() {
        Uri uri = getIntent().getData();
        if (uri == null) {
            Log.w(TAG, "no data uri in " + getIntent());
            return;
        }
        String type = null;
        try {
            type = getContentResolver().getType(uri);
        } catch (Exception e) {
            Log.w(TAG, "getType failed for " + uri, e);
        }
        if (type == null || !type.startsWith("video/")) {
            type = "video/*";
        }

        Intent view = new Intent(Intent.ACTION_VIEW)
                .setDataAndType(uri, type)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            // We were handed a read grant by the camera; pass it on.
            startActivity(new Intent(view).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION));
        } catch (SecurityException e) {
            // No grant to forward: MediaStore uris are readable by any viewer
            // holding the media permission anyway.
            try {
                startActivity(view);
            } catch (ActivityNotFoundException e2) {
                Log.e(TAG, "no video viewer for " + uri, e2);
            }
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "no video viewer for " + uri, e);
        }
    }
}
