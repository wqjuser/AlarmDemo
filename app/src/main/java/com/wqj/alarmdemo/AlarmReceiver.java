package com.wqj.alarmdemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * Created by wqj on 2017/11/13.
 */

public class AlarmReceiver extends BroadcastReceiver {
    private NotificationManager m_notificationMgr = null;
    private static final int NOTIFICATION_FLAG = 3;

    @Override
    public void onReceive(Context context, Intent intent) {
        m_notificationMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (intent.getAction().equals(GlobalValues.TIMER_ACTION_REPEATING)) {
//            Log.e("alarm_receiver", "周期闹钟");
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background);
            Intent intent1 = new Intent(context, ClockAlarmActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, 0);
            Notification notify = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.ic_launcher_background) // 设置状态栏中的小图片，尺寸一般建议在24×24
                    .setLargeIcon(bitmap) // 这里也可以设置大图标
                    .setTicker("这是闹钟") // 设置显示的提示文字
                    .setContentTitle("这是闹钟") // 设置显示的标题
                    .setContentText("这是周期闹钟啊") // 消息的详细内容
                    .setContentIntent(pendingIntent) // 关联PendingIntent
                    .setNumber(1) // 在TextView的右方显示的数字，可以在外部定义一个变量，点击累加setNumber(count),这时显示的和
                    .build(); // 需要注意build()是在API level16及之后增加的，在API11中可以使用getNotificatin()来
            notify.flags |= Notification.FLAG_AUTO_CANCEL;
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(NOTIFICATION_FLAG, notify);
//            bitmap.recycle(); //回收bitmap
        } else if (intent.getAction().equals(GlobalValues.TIMER_ACTION)) {
//            Log.e("alarm_receiver", "定时闹钟");
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background);
            Intent intent1 = new Intent(context, ClockAlarmActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, 0);
            Notification notify = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.ic_launcher_background) // 设置状态栏中的小图片，尺寸一般建议在24×24
                    .setLargeIcon(bitmap) // 这里也可以设置大图标
                    .setTicker("亲情日历") // 设置显示的提示文字
                    .setContentTitle("亲情日历") // 设置显示的标题
                    .setContentText("这是定时闹钟啊") // 消息的详细内容
                    .setContentIntent(pendingIntent) // 关联PendingIntent
                    .setNumber(1) // 在TextView的右方显示的数字，可以在外部定义一个变量，点击累加setNumber(count),这时显示的和
                    .getNotification(); // 需要注意build()是在API level16及之后增加的，在API11中可以使用getNotificatin()来
            notify.flags |= Notification.FLAG_AUTO_CANCEL;
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(NOTIFICATION_FLAG, notify);
            bitmap.recycle(); //回收bitmap
        }
    }
}
