package com.wqj.alarmdemo;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.coolerfall.daemon.Daemon;

import java.util.Calendar;

import static android.app.AlarmManager.INTERVAL_DAY;

public class MainActivity extends AppCompatActivity {
    private Button setAlarm;
    private JobScheduler mJobScheduler;
    private static int REQUEST_IGNORE_BATTERY_CODE = 1;
    private TimePicker timePicker;
    private int hour;
    private int mMinute;
    private EditText curTime;
    private Calendar c;
    private AlarmManager alarmManager;
    private PendingIntent sender;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        Daemon.run(this, MainActivity.class, Daemon.INTERVAL_ONE_MINUTE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mJobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
            JobInfo.Builder builder = new JobInfo.Builder(2, new ComponentName(getPackageName(), JobSchedulerService.class.getName()));
            builder.setPeriodic(60 * 1000); //每隔60秒运行一次
            builder.setRequiresCharging(true);
            builder.setPersisted(true);  //设置设备重启后，是否重新执行任务
            builder.setRequiresDeviceIdle(true);
            if (mJobScheduler.schedule(builder.build()) <= JobScheduler.RESULT_FAILURE) {
                //If something goes wrong
            }
        }
        isIgnoreBatteryOption(this);
    }

    public void init() {
        c = Calendar.getInstance();
        curTime = findViewById(R.id.editText);
        timePicker = findViewById(R.id.timePicker);
        setAlarm = findViewById(R.id.button);
        setAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(GlobalValues.TIMER_ACTION_REPEATING);
                sender = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
                alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Log.w("onClick: ", String.valueOf(c.getTimeInMillis()));
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), sender);
                Toast.makeText(MainActivity.this, "定时提醒闹钟设置成功", Toast.LENGTH_SHORT).show();
            }
        });
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                hour = hourOfDay;
                mMinute = minute;
                curTime.setText("闹钟定时为：" + hour + "时" + mMinute + "分");
                c = timePicker(hour, mMinute);
            }
        });
    }

    /**
     * 给 API >= 18 的平台上用的灰色保活手段
     */
    public static class DaemonInnerService extends Service {

        @Override
        public void onCreate() {
            Log.i("InnerService", "");
            super.onCreate();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.i("InnerService ", "");
            startForeground(2, new Notification());
            //stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public IBinder onBind(Intent intent) {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public void onDestroy() {
            Log.i("InnerService", "");
            super.onDestroy();
        }
    }

    /**
     * 针对N以上的Doze模式
     *
     * @param activity
     */
    public static void isIgnoreBatteryOption(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                Intent intent = new Intent();
                String packageName = activity.getPackageName();
                PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
//               intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + packageName));
                    activity.startActivityForResult(intent, REQUEST_IGNORE_BATTERY_CODE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IGNORE_BATTERY_CODE) {
                //TODO something
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (requestCode == REQUEST_IGNORE_BATTERY_CODE) {
                Toast.makeText(this, "电池优化被忽略", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Calendar timePicker(int hourOfDay, int minute) {
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        //避免设置时间比当前时间小时 马上响应的情况发生
        if (c.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
            c.set(Calendar.DAY_OF_MONTH, Calendar.DAY_OF_MONTH + 1);
        }
        return c;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        alarmManager.cancel(sender);
    }
}

