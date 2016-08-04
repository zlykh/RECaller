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

import android.app.Service;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class RecordRunnable implements Runnable {
    public final static int frequency = 44100;
    private boolean recordWhileTrue = true;
    final private Service service;
    final private File file;

    public RecordRunnable(Service service, File file) {
        this.service = service;
        this.file = file;
    }

    @Override
    public void run() {
        AudioRecord record = null;
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file));
            final int minBufferSize = AudioRecord.getMinBufferSize(frequency, AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            record = new AudioRecord(MediaRecorder.AudioSource.VOICE_CALL,
                    frequency, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize);
            short buffer[] = new short[minBufferSize];
            byte[] byteBuffer = new byte[minBufferSize * 2];

            record.startRecording();

            while (recordWhileTrue) {
                    final int bufferRead = record.read(buffer, 0, minBufferSize);
                    ByteBuffer.wrap(byteBuffer, 0, bufferRead * 2).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(buffer, 0, bufferRead);
                    out.write(byteBuffer, 0, bufferRead * 2);
            }

            record.stop();
            out.close();
        } catch ( Exception e1) {
            e1.printStackTrace();
            if(record!=null){
                record.stop();
            }
            if(out!=null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            service.stopSelf();
        }
    }

    public void stopRecord(){
        recordWhileTrue = false;
    }


}
