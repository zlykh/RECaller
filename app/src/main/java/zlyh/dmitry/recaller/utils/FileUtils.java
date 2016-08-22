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

package zlyh.dmitry.recaller.utils;


import android.content.Intent;

import zlyh.dmitry.recaller.Const;
import zlyh.dmitry.recaller.RecallerApp;
import zlyh.dmitry.recaller.model.RecordModel;
import zlyh.dmitry.recaller.services.FileService;

public class FileUtils {
    public static void renameFile(RecordModel record){
        Intent intent = new Intent(RecallerApp.getAppContext(), FileService.class);
        intent.putExtra(Const.COMMAND, Const.FileService.RENAME);
        intent.putExtra(Const.FileService.FILE_PATH, record.getPath());
        intent.putExtra(Const.FileService.CUSTOM_FILE_NAME, record.getCustom_name() );
        RecallerApp.getAppContext().startService(intent);
    }

    public static void deleteFile(RecordModel record){
        Intent intent = new Intent(RecallerApp.getAppContext(), FileService.class);
        intent.putExtra(Const.COMMAND, Const.FileService.DELETE);
        intent.putExtra(Const.FileService.FILE_PATH, record.getPath());
        intent.putExtra(Const.FileService.CUSTOM_FILE_NAME, record.getCustom_name() );
        RecallerApp.getAppContext().startService(intent);
    }
}
