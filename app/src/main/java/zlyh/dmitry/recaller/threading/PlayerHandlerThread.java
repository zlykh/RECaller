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

package zlyh.dmitry.recaller.threading;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import zlyh.dmitry.recaller.Const;

public class PlayerHandlerThread extends HandlerThread {
    private Thread readBlockingThread;
    private boolean isplaying = false;
    private Handler handler;

    public PlayerHandlerThread(String name) {
        super(name);
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        handler = new Handler(this.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                        case Const.PlayerService.PLAY:
                            if(isplaying) {
                                stopTrack();
                            }
                            final String path = (String) msg.obj;
                            final int id = msg.arg1;

                            if (path != null && !path.isEmpty()) {
                                playTrack(path,id);
                            }
                            break;
                        case Const.PlayerService.STOP:
                            stopTrack();
                            break;
                    }
            }
        };
    }

    private void stopTrack() {
        if(readBlockingThread!=null) {
            readBlockingThread.interrupt();
            isplaying = false;
        }

    }

    private void playTrack(String path, int id){
        readBlockingThread = new PlayBlockThread(path, id);
        readBlockingThread.start();
        isplaying = true;

    }

    public void startPlaying(String path, int id){
        handler.sendMessage(handler.obtainMessage(Const.PlayerService.PLAY,id,0,path));
    }

    public void stopPlaying(){
        handler.sendMessage(handler.obtainMessage(Const.PlayerService.STOP));
    }

    public void shutdown(){
        if(readBlockingThread!=null){
            readBlockingThread.interrupt();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            this.quitSafely();
        }else{
            this.quit();
        }

        this.interrupt();
    }

}
