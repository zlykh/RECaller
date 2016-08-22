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

import android.util.Log;

import java.io.File;

import zlyh.dmitry.recaller.services.FileService;

public class FileDeleteRunnable implements Runnable {

    private final FileService fileService;
    private final String path;

    public FileDeleteRunnable(FileService fileService, String path) {
        this.fileService = fileService;
        this.path = path;
    }

    @Override
    public void run() {
        try {
            File f = new File(path);
            if (f.exists()) {
               boolean del = f.delete();
                Log.e("1","del " +del + " path "+path);
            }

            fileService.decrement();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
