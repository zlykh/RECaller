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

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import zlyh.dmitry.recaller.Const;
import zlyh.dmitry.recaller.threading.FileDeleteRunnable;
import zlyh.dmitry.recaller.threading.FileRenameRunnable;

public class FileService extends Service {
    private volatile int tasks_in_queue =0;
    private ExecutorService executor;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        executor = Executors.newFixedThreadPool(5);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int command = intent.getIntExtra(Const.COMMAND, -1);
        final String path = intent.getStringExtra(Const.FileService.FILE_PATH);
        final String path_no_name = path.substring(0,path.lastIndexOf("/"));
        final String custom_name = intent.getStringExtra(Const.FileService.CUSTOM_FILE_NAME);
        String new_path = path;
        if(custom_name!=null && !custom_name.isEmpty()){
            new_path = path_no_name.concat(custom_name);
        }
        if(path!=null && !path.isEmpty()) {

            synchronized (this){
                tasks_in_queue++;
            }


            switch (command) {
                case Const.FileService.RENAME:
                    executor.submit(new FileRenameRunnable(this, path, new_path));
                    break;
                case Const.FileService.DELETE:
                    executor.submit(new FileDeleteRunnable(this, new_path));
                    break;
             }
        }

        return START_REDELIVER_INTENT;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        executor.shutdownNow();
    }

    public synchronized void decrement(){
        tasks_in_queue--;
        if(tasks_in_queue <= 0){
            broadcastDone();
            stopSelf();
        }
    }

    private void broadcastDone() {
        Intent intent = new Intent(Const.FileService.BROADCAST).putExtra(Const.COMMAND,Const.FileService.DELETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }
}
