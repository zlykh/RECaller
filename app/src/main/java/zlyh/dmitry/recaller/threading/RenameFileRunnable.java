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

import java.io.File;

import zlyh.dmitry.recaller.services.FileService;

public class RenameFileRunnable implements Runnable {

    private final FileService fileService;
    private final String path;
    private String newname;

    public RenameFileRunnable(FileService fileService, String path, String newname) {
        this.fileService = fileService;
        this.path = path;
        this.newname = newname;
    }

    @Override
    public void run() {
        try {
            File f = new File(path);
            final String path_no_name=path.substring(0,path.lastIndexOf("/"));
            File newfile = new File(path_no_name,newname);
            f.renameTo(newfile);//ignore if failed

            fileService.decrement();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
