package org.codeforafrica.citizenreporter.eNCA.ui.main;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import org.codeforafrica.citizenreporter.eNCA.Constants;
import org.codeforafrica.citizenreporter.eNCA.R;
import org.wordpress.passcodelock.AppLockManager;
import org.codeforafrica.citizenreporter.eNCA.ui.posts.StoryBoard;
import org.wordpress.android.util.DeviceUtils;

/**
 * Created by nick on 12/08/15.
 */
public class QuickCaptureWidget  extends AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; i++) {
            int currentWidgetId = appWidgetIds[i];

            PendingIntent pendingPic = PendingIntent.getActivity(context, 0, capturePic(context), 0);
            PendingIntent pendingVid = PendingIntent.getActivity(context, 0, captureVid(context), 0);
            PendingIntent pendingAudio = PendingIntent.getActivity(context, 0, captureAudio(context), 0);

            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.quick_widget);
            views.setOnClickPendingIntent(R.id.button_camera, pendingPic);
            views.setOnClickPendingIntent(R.id.button_video, pendingVid);
            views.setOnClickPendingIntent(R.id.button_mic, pendingAudio);

            appWidgetManager.updateAppWidget(currentWidgetId, views);
        }
    }

    public Intent capturePic(Context context){
        AppLockManager.getInstance().setExtendedTimeout();
        boolean mShouldFinish = false;
        Intent intent = new Intent(context, StoryBoard.class);
        intent.putExtra("quick-media", DeviceUtils.getInstance().hasCamera(context)
                ? Constants.QUICK_POST_PHOTO_CAMERA
                : Constants.QUICK_POST_PHOTO_LIBRARY);
        intent.putExtra("isNew", true);
        intent.putExtra("shouldFinish", mShouldFinish);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public Intent captureVid(Context context){
        AppLockManager.getInstance().setExtendedTimeout();
        boolean mShouldFinish = false;
        Intent intent = new Intent(context, StoryBoard.class);
        intent.putExtra("quick-media", DeviceUtils.getInstance().hasCamera(context)
                ? Constants.QUICK_POST_VIDEO_CAMERA
                : Constants.QUICK_POST_PHOTO_LIBRARY);
        intent.putExtra("isNew", true);
        intent.putExtra("shouldFinish", mShouldFinish);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public Intent captureAudio(Context context){
        AppLockManager.getInstance().setExtendedTimeout();
        boolean mShouldFinish = false;
        Intent intent = new Intent(context, StoryBoard.class);
        intent.putExtra("quick-media", Constants.QUICK_POST_AUDIO_MIC);
        intent.putExtra("isNew", true);
        intent.putExtra("shouldFinish", mShouldFinish);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }
}