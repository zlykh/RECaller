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

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.support.v4.content.LocalBroadcastManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import zlyh.dmitry.recaller.Const;
import zlyh.dmitry.recaller.RecallerApp;

public class PlayBlockThread extends Thread {


    private final String path;
    private final int id;

    public PlayBlockThread(String path, int id) {
        super("PlayerReadBlockThread");
        this.path = path;
        this.id = id;
    }

    @Override
    public void run() {
        AudioTrack audioTrack= null;
        FileInputStream in = null;

        try {
            File rawpcm  = new File(path);
            if (!rawpcm.exists()) {
                this.interrupt();
            }

            togglePlaying(true);

            final int audioLength = (int) rawpcm.length();
            final int minBufferSize = AudioRecord.getMinBufferSize(RecordRunnable.frequency, AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, RecordRunnable.frequency,
                    AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize, AudioTrack.MODE_STREAM);


            final int block = 256 * 1024;
            byte[] byteData = new byte[block];

            try {
                in = new FileInputStream(rawpcm);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                this.interrupt();
            }

            if (in != null) {
                try {
                    int bytesread = 0;
                    int offset;
                    audioTrack.play();
                    while (bytesread < audioLength && !isInterrupted()) {
                        offset = in.read(byteData, 0, block);
                        if (offset != -1) {
                            audioTrack.write(byteData, 0, offset);
                            bytesread += offset;
                        } else {
                            break;
                        }
                    }
                    in.close();

                    togglePlaying(false);

                    if (audioTrack.getState() == AudioTrack.PLAYSTATE_PLAYING) {
                        audioTrack.stop();
                    }

                    if(audioTrack.getState() == AudioTrack.STATE_INITIALIZED){
                        audioTrack.release();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        in.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    if (audioTrack.getState() == AudioTrack.PLAYSTATE_PLAYING) {
                        audioTrack.stop();
                    }
                    if(audioTrack.getState() == AudioTrack.STATE_INITIALIZED){
                        audioTrack.release();
                    }
                    togglePlaying(false);

                }
            }
        }catch (Exception e){
            e.printStackTrace();
            if (audioTrack != null) {
                if (audioTrack.getState() == AudioTrack.PLAYSTATE_PLAYING) {
                    audioTrack.stop();
                }
                if(audioTrack.getState() == AudioTrack.STATE_INITIALIZED){
                    audioTrack.release();
                }
            }

            if(in!=null){
                try {
                    in.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            togglePlaying(false);

        }


    }

    private void togglePlaying(boolean is_playing){
            Intent intent;
            if(is_playing) {
                 intent = new Intent(Const.PlayerService.BROADCAST).putExtra(Const.COMMAND, Const.PlayerService.PLAY)
                        .putExtra(Const.MODEL, id);
            }else{
                intent = new Intent(Const.PlayerService.BROADCAST).putExtra(Const.COMMAND, Const.PlayerService.STOP)
                        .putExtra(Const.MODEL, id);
            }
            LocalBroadcastManager.getInstance(RecallerApp.getAppContext()).sendBroadcast(intent);
    }
}
