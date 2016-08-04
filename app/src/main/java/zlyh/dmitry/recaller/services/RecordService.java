/*
 * Copyright 2016 Dmitriy Zlykh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package zlyh.dmitry.recaller.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import zlyh.dmitry.recaller.Const;
import zlyh.dmitry.recaller.MainActivity;
import zlyh.dmitry.recaller.R;
import zlyh.dmitry.recaller.RecallerApp;
import zlyh.dmitry.recaller.model.RecordModel;
import zlyh.dmitry.recaller.threading.RecordRunnable;

public class RecordService extends Service {
    private RecordRunnable record;
    private ExecutorService executor;
    private long time_start = -1;
    private File file= null;
    private String phone;
    BlockingQueue<Runnable> nosizequeue;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
         nosizequeue = new SynchronousQueue<>();
        executor = new ThreadPoolExecutor(1, 1, 1, TimeUnit.MINUTES, nosizequeue);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int command = intent.getIntExtra(Const.COMMAND, -1);
        switch (command) {
            case Const.RecordService.START:
                time_start = intent.getLongExtra(Const.CallReceiver.TIME_MARK, -1);
                File dir = getDir();
                if (dir == null) {
                    break;
                }
                phone = intent.getStringExtra(Const.CallReceiver.PHONE_NUM);
                file = new File(dir, "r_" + String.valueOf(time_start) + "_.pcm");
                record = new RecordRunnable(this, file);
                    try {
                        executor.execute(record);

                        localBroadcast(Const.RecordService.START);
                    } catch (RejectedExecutionException e) {
                        e.printStackTrace();
                    }
                break;
            case Const.RecordService.STOP:
                final String temp_phone = intent.getStringExtra(Const.CallReceiver.PHONE_NUM);
                if(temp_phone!=null && !temp_phone.isEmpty()){
                    phone=temp_phone;
                }
                fullstop(false);
                saveSQL(intent.getLongExtra(Const.CallReceiver.TIME_MARK, -1), intent.getIntExtra(Const.CallReceiver.WAS_INCOMING, -1));
                localBroadcast(Const.RecordService.STOP);
                popNotification();
                break;
        }

        //start redeliver will ruin app if notification is alive
        return START_NOT_STICKY;

    }

    private void saveSQL(long time_end, int was_incoming) {
        if (file == null || time_end == -1 || time_start == -1) {
            return;
        }
        //usually operator intentionally breaks call after 30minutes
        final long duration = time_end - time_start;
        final String readable_duration = String.format("%1$02dm:%2$02ds",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
        final String filename = file.getName();
        final String path = file.getAbsolutePath();

        final DateFormat sdf = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        final String readable_time = sdf.format(new Date(time_start));

        Intent intent = new Intent(this, SqlService.class);
        intent.putExtra(Const.COMMAND, Const.SqlService.SAVE);
        intent.putExtra(Const.MODEL, new RecordModel(time_start, time_end, readable_duration, filename,
                path, readable_time, phone, was_incoming));
        startService(intent);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fullstop(true);
    }

    private void fullstop(boolean shutdown){
        if (record != null) {
            record.stopRecord();
            record = null;
        }

        if(shutdown) {
            try {
                executor.shutdown();
                executor.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            executor.shutdownNow();
        }

    }

    private void localBroadcast(int command) {
        Intent status_broadcast = new Intent(Const.BROADCAST).putExtra(Const.COMMAND, command);
        LocalBroadcastManager.getInstance(this).sendBroadcast(status_broadcast);
    }

    private void popNotification() {
        final SharedPreferences preferences = RecallerApp.getAppContext().
                getSharedPreferences(getString(R.string.shared_prefs), Context.MODE_PRIVATE);
        if(!preferences.getBoolean(Const.Prefs.NOTIFICATION_OPTION,true)){
            return;
        }

        Intent notifyIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder mBuilder =
                new Notification.Builder(this)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(android.R.drawable.ic_menu_call)
                        .setContentTitle(getString(R.string.recaller))
                        .setAutoCancel(true)
                        .setContentText(getString(R.string.notif_text));
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(123, mBuilder.build());
    }

    private File getDir() {
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), getString(R.string.recaller));
        if (!storageDir.mkdirs()) {
            if (!storageDir.exists()){
                storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),getString(R.string.recaller));
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        //some low-end devices has no access no external storage
                        storageDir =new  File(RecallerApp.getAppContext().getExternalFilesDir(null), getString(R.string.recaller));
                        if (!storageDir.mkdirs()) {
                            if (!storageDir.exists()) {
                                storageDir =new  File(RecallerApp.getAppContext().getFilesDir(), getString(R.string.recaller));
                                if (!storageDir.mkdirs()) {
                                    if (!storageDir.exists()) {
                                        return null;
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
        return storageDir;
    }

}
