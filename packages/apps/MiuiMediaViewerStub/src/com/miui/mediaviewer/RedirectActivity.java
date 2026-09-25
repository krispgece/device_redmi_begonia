package com.miui.mediaviewer;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
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
        // MiuiCamera hands us a read grant, but it only lives as long as this
        // activity, and we finish right away. Pass it on only when the view
        // intent goes straight to one app. When the system chooser has to ask,
        // the picked app is launched as us after we're gone, the grant check
        // fails and nothing opens ("Just once"); without the flag the viewer
        // reads the MediaStore uri with its own media permission.
        ResolveInfo ri = getPackageManager().resolveActivity(view,
                PackageManager.MATCH_DEFAULT_ONLY);
        boolean direct = ri != null && ri.activityInfo != null
                && !"android".equals(ri.activityInfo.packageName);
        if (direct && checkUriPermission(uri, Process.myPid(), Process.myUid(),
                Intent.FLAG_GRANT_READ_URI_PERMISSION)
                == PackageManager.PERMISSION_GRANTED) {
            view.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        try {
            startActivity(view);
        } catch (ActivityNotFoundException | SecurityException e) {
            Log.e(TAG, "cannot open " + uri, e);
        }
    }
}
