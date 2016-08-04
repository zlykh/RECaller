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

import java.util.ArrayList;
import java.util.ListIterator;

import zlyh.dmitry.recaller.Const;
import zlyh.dmitry.recaller.RecallerApp;
import zlyh.dmitry.recaller.model.RecordModel;
import zlyh.dmitry.recaller.services.SqlService;


public class SqlUtils {
    public static final int DESIRED_LENGTH = 15 * 1000;

    public static void deleteRecord(RecordModel record){
        Intent remove_sql_intent = new Intent(RecallerApp.getAppContext(), SqlService.class);
        remove_sql_intent.putExtra(Const.COMMAND, Const.SqlService.DELETE);
        remove_sql_intent.putExtra(Const.MODEL, record.getId());
        RecallerApp.getAppContext().startService(remove_sql_intent);
    }

    public static void saveRecord(RecordModel record){
        Intent intent = new Intent(RecallerApp.getAppContext(),SqlService.class);
        intent.putExtra(Const.COMMAND,Const.SqlService.SAVE);
        intent.putExtra(Const.MODEL,record);
        RecallerApp.getAppContext().startService(intent);
    }

    public static void removeShortRecords(ArrayList<RecordModel> records) {
        ListIterator<RecordModel> iter = records.listIterator();
        while (iter.hasNext()) {
            final RecordModel record = iter.next();
            final long duration = record.getTime_end() - record.getTime_start();

            if (duration <= DESIRED_LENGTH) {
                deleteRecord(record);
                iter.remove();
            }
        }
    }

    public static void loadRecords(int size) {
        Intent intent = new Intent(RecallerApp.getAppContext(), SqlService.class);
        intent.putExtra(Const.COMMAND, Const.SqlService.LOAD);
        intent.putExtra(Const.SqlService.LOAD_CHUNK,size);
        RecallerApp.getAppContext().startService(intent);
    }
}
