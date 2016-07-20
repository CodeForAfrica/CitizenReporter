package org.codeforafrica.citizenreporter.eNCA.ui.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.codeforafrica.citizenreporter.eNCA.GCMIntentService;

/*
 * Clears the notification map when a user dismisses a notification
 */
public class NotificationDismissBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        GCMIntentService.clearNotificationsMap();
    }
}