package edu.gatech.seclass.todo;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import androidx.core.app.NotificationCompat;
import java.util.concurrent.TimeUnit;
import edu.gatech.seclass.todo.database.TickDBAdapter;
import edu.gatech.seclass.todo.util.CountDownTimer;
import edu.gatech.seclass.todo.util.Sound;
import edu.gatech.seclass.todo.util.TimeFormatUtil;
import edu.gatech.seclass.todo.util.WakeLockHelper;

public class TickService extends Service implements CountDownTimer.OnCountDownTickListener {
    public static final String ACTION_COUNTDOWN_TIMER =
            "com.icodechef.android.tick.COUNTDOWN_TIMER";
    public static final String ACTION_START = "com.icodechef.android.tick.ACTION_START";
    public static final String ACTION_PAUSE = "com.icodechef.android.tick.ACTION_PAUSE";
    public static final String ACTION_RESUME = "com.icodechef.android.tick.ACTION_RESUME";
    public static final String ACTION_STOP = "com.icodechef.android.tick.ACTION_STOP";
    public static final String ACTION_TICK = "com.icodechef.android.tick.ACTION_TICK";
    public static final String ACTION_FINISH = "com.icodechef.android.tick.ACTION_FINISH";
    public static final String ACTION_AUTO_START
            = "com.icodechef.android.tick.ACTION_AUTO_START";
    public static final String ACTION_TICK_SOUND_ON =
            "com.icodechef.android.timer.ACTION_TICK_SOUND_ON";
    public static final String ACTION_TICK_SOUND_OFF =
            "com.icodechef.android.timer.ACTION_TICK_SOUND_OFF";
    public static final String ACTION_POMODORO_MODE_ON =
            "com.icodechef.android.timer.ACTION_POMODORO_MODE_OFF";

    public static final String MILLIS_UNTIL_FINISHED = "MILLIS_UNTIL_FINISHED";
    public static final String REQUEST_ACTION = "REQUEST_ACTION";
    public static final int NOTIFICATION_ID = 1;
    public static final String WAKELOCK_ID = "tick_wakelock";

    private CountDownTimer mTimer;
    private TickApplication mApplication;
    private WakeLockHelper mWakeLockHelper;
    private TickDBAdapter mDBAdapter;
    private Sound mSound;
    private long mID;
    private NotificationManager manager;
    private Notification notification;

    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.SCREEN_ON")) {
                mWakeLockHelper.release();
            } else if (intent.getAction().equals("android.intent.action.SCREEN_OFF")) {
                mWakeLockHelper.acquire(getApplicationContext());
            }
        }
    };

    public static Intent newIntent(Context context) {
        return new Intent(context, TickService.class);
    }

    public TickService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = (TickApplication)getApplication();
        mWakeLockHelper = new WakeLockHelper(WAKELOCK_ID);
        mDBAdapter = new TickDBAdapter(getApplicationContext());
        mSound = new Sound(getApplicationContext());
        mID = 0;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        registerReceiver(mIntentReceiver, intentFilter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            switch (action) {
                case ACTION_AUTO_START:
                case ACTION_START:
                    stopTimer();

                    // 自动专注
                    if (action.equals(ACTION_AUTO_START)) {
                        Intent broadcastIntent = new Intent(ACTION_COUNTDOWN_TIMER);
                        broadcastIntent.putExtra(REQUEST_ACTION, ACTION_AUTO_START);
                        sendBroadcast(broadcastIntent);

                        mApplication.start();
                    }

                    long millsInTotal = getMillsInTotal();
                    mTimer = new CountDownTimer(millsInTotal);
                    mTimer.setOnChronometerTickListener(this);
                    mTimer.start();
                    mSound.play();
                    //startForeground(NOTIFICATION_ID, getNotification(String.valueOf(NOTIFICATION_ID),getNotificationTitle(), formatTime(millsInTotal)));
                    if (mApplication.getScene() == TickApplication.SCENE_WORK) {
                        // 插入数据
                        mDBAdapter.open();
                        mID = mDBAdapter.insert(mTimer.getStartTime(),
                                mTimer.getMinutesInFuture());
                        mDBAdapter.close();
                    }
                    break;
                case ACTION_PAUSE:
                    if (mTimer != null) {
                        mTimer.pause();
                        String text = getResources().getString(R.string.notification_time_left)
                                + " " + intent.getStringExtra("time_left");
                        getNotification(
                                String.valueOf(NOTIFICATION_ID),getNotificationTitle(), text);
                    }

                    mSound.pause();
                    break;
                case ACTION_RESUME:
                    if (mTimer != null) {
                        mTimer.resume();
                    }

                    mSound.resume();
                    break;
                case ACTION_STOP:
                    stopTimer();
                    mSound.stop();
                    break;
                case ACTION_TICK_SOUND_ON:
                    if (mTimer != null && mTimer.isRunning()) {
                        mSound.play();
                    }
                    break;
                case ACTION_TICK_SOUND_OFF:
                    mSound.stop();
                    break;
                case ACTION_POMODORO_MODE_ON:
                    // 如果处于暂停状态，但番茄模式设置为 on ,停止番茄时钟
                    if (mApplication.getState() == TickApplication.STATE_PAUSE) {
                        if (mTimer != null) {
                            mTimer.resume();
                            mSound.resume();
                            mApplication.resume();
                        }
                    }

                    break;
            }
        }

        return START_STICKY;
    }

    private void stopTimer() {
        if (isNotificationOn()) {
            cancelNotification();
        }

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
            stopForeground(true);
        }
        mWakeLockHelper.release();
    }

    private long getMillsInTotal() {
        int minutes = mApplication.getMinutesInTotal();
        return TimeUnit.MINUTES.toMillis(minutes);
    }

    @Override
    public void onCountDownTick(long millisUntilFinished) {
        mApplication.setMillisUntilFinished(millisUntilFinished);
        Intent intent = new Intent(ACTION_COUNTDOWN_TIMER);
        intent.putExtra(MILLIS_UNTIL_FINISHED, millisUntilFinished);
        intent.putExtra(REQUEST_ACTION, ACTION_TICK);
        sendBroadcast(intent);
        getNotification(String.valueOf(NOTIFICATION_ID),getNotificationTitle(), formatTime(millisUntilFinished));
    }

    @Override
    public void onCountDownFinish() {
        Intent intent = new Intent(ACTION_COUNTDOWN_TIMER);
        intent.putExtra(REQUEST_ACTION, ACTION_FINISH);
        sendBroadcast(intent);

        if (mApplication.getScene() == TickApplication.SCENE_WORK) {
            getNotification(String.valueOf(NOTIFICATION_ID),
                    getResources().getString(R.string.notification_finish),
                    getResources().getString(R.string.notification_finish_content)
            );

            if (mID > 0) {
                // 更新数据
                mDBAdapter.open();
                boolean success = mDBAdapter.update(mID);
                mDBAdapter.close();

                if (success) {
                    long amountDurations =
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                                    .getLong("pref_key_amount_durations", 0);

                    SharedPreferences.Editor editor =
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                                    .edit();
                    amountDurations += mTimer.getMinutesInFuture();
                    editor.putLong("pref_key_amount_durations", amountDurations);
                    editor.apply();
                }
            }
        } else {
            getNotification(String.valueOf(NOTIFICATION_ID),
                    getResources().getString(R.string.notification_break_finish),
                    getResources().getString(R.string.notification_break_finish_content)
            );
        }

        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getBoolean("pref_key_use_notification", true)) {

            int defaults = Notification.DEFAULT_LIGHTS;

            if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                    .getBoolean("pref_key_notification_sound", true)) {
                // 结束提示音
                Uri uri = Uri.parse(
                        "android.resource://" + getPackageName() + "/" +
                                (mApplication.getScene() == TickApplication.SCENE_WORK ?
                                        R.raw.workend :
                                        R.raw.breakend));
            }

            if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                    .getBoolean("pref_key_notification_vibrate", true)) {
                defaults |= Notification.DEFAULT_VIBRATE;

            }

        }


        mApplication.finish();
        mSound.stop();

        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getBoolean("pref_key_infinity_mode", false)) {
            Intent i = TickService.newIntent(getApplicationContext());
            i.setAction(ACTION_AUTO_START);

            PendingIntent pi = PendingIntent.getService(getApplicationContext(), 0, i, 0);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + 2000, pi);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
        mSound.release();
        unregisterReceiver(mIntentReceiver);
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private boolean isNotificationOn() {
        Intent intent = MainActivity.newIntent(getApplicationContext());
        PendingIntent pi = PendingIntent
                .getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    private void cancelNotification() {
        Intent intent = MainActivity.newIntent(getApplicationContext());
        PendingIntent pi = PendingIntent
                .getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_NO_CREATE);
        pi.cancel();
        getNotificationManager().cancel(NOTIFICATION_ID);
    }

    private Notification getNotification(String id,String title, String text) {
        manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        String channelId = id;
        String channelName = title;
        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(channelId, channelName, importance);
            channelId = id;
            channelName = title;
            importance = NotificationManager.IMPORTANCE_DEFAULT;
            createNotificationChannel(channelId, channelName, importance);
        }
        Intent intent = MainActivity.newIntent(getApplicationContext());
        PendingIntent pi = PendingIntent.getActivity(this,0,intent,0);
        notification = new NotificationCompat.Builder(this, channelId)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(text)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_stat_notify)
                .setContentIntent(pi)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon))
                .build();
        manager.notify(3, notification);


        return notification;
    }

    private String getNotificationTitle() {
        int scene = mApplication.getScene();
        int state = mApplication.getState();
        String title;

        switch (scene) {
            case TickApplication.SCENE_WORK:
                if (state == TickApplication.STATE_PAUSE) {
                    title = getResources().getString(R.string.notification_focus_pause);
                } else {
                    title = getResources().getString(R.string.notification_focus);
                }
                break;
            default:
                if (state == TickApplication.STATE_PAUSE) {
                    title = getResources().getString(R.string.notification_break_pause);
                } else {
                    title = getResources().getString(R.string.notification_break);
                }
                break;
        }

        return title;
    }

    private String formatTime(long millisUntilFinished) {
        return getResources().getString(R.string.notification_time_left)  + " " +
                TimeFormatUtil.formatTime(millisUntilFinished);
    }
   /* public void NotificationC(String title, String text){
        manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        String channelId = "chat2";
        String channelName = "聊天消息";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(channelId, channelName, importance);
            channelId = "subscribe";
            channelName = "订阅消息";
            importance = NotificationManager.IMPORTANCE_DEFAULT;
            createNotificationChannel(channelId, channelName, importance);
        }
        Intent intent = new Intent(Next.this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(Next.this,0,intent,0);
        Notification notification = new NotificationCompat.Builder(Next.this, "chat2")
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(text)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.food_ic)
                .setContentIntent(pi)
                .setColor(Color.parseColor("#F00606"))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .build();
        manager.notify(3, notification);

    }*/
    private void createNotificationChannel(String channelId, String channelName, int importance)
    {
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(channelId, channelName, importance);
        }
        NotificationManager notificationManager = (NotificationManager)getSystemService(
                NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }

    }
}
