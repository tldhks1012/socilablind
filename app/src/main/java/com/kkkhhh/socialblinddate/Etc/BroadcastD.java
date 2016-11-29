package com.kkkhhh.socialblinddate.Etc;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.kkkhhh.socialblinddate.Activity.MainAct;
import com.kkkhhh.socialblinddate.Activity.StartAct;
import com.kkkhhh.socialblinddate.R;

/**
 * Created by Dev1 on 2016-11-29.
 */

public class BroadcastD extends BroadcastReceiver {
    String INTENT_ACTION = Intent.ACTION_BOOT_COMPLETED;
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, StartAct.class), PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.ic_action_list_my_white).setTicker("소셜블라인드데이팅").setWhen(System.currentTimeMillis())
                .setContentTitle("소셜블라인드데이팅").setContentText("200 코인이 충전되었어요")
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingIntent).setAutoCancel(true);
        notificationmanager.notify(1, builder.build());
    }
}
