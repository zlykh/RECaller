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

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import zlyh.dmitry.recaller.RecallerApp;
import zlyh.dmitry.recaller.model.RecordModel;


public class SQLHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "recaller.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "calls";
    public static final int SELECTION_LIMIT = 15;

    public static final String C_ID = "_id";
    public static final String C_FILENAME = "filename";
    public static final String C_PATH = "path";
    public static final String C_DURATION = "duration_string";
    public static final String C_DATE = "date_string";
    public static final String C_START = "start";
    public static final String C_END = "end";
    public static final String C_FAVORITE = "favorite";
    public static final String C_CUSTOM_NAME = "custom_name";
    public static final String C_PHONE = "phone";
    public static final String C_INCOMING = "is_incoming";



    public static final String SQL_READ_LIMIT =
            "select * from " + TABLE_NAME + " order by "+C_START+" DESC limit ?, ? ";

    private static SQLHelper ourInstance = new SQLHelper(RecallerApp.getAppContext());

    public  static synchronized SQLHelper getInstance() {
        return ourInstance;
    }

    private SQLHelper(Context context) {
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" (" +
                C_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                C_FILENAME+" TEXT, "+
                C_PATH+" TEXT, "+
                C_DURATION+" TEXT, "+
                C_DATE+" TEXT," +
                C_START+" INTEGER, "+
                C_END+" INTEGER, "+
                C_FAVORITE+" INTEGER, "+
                C_CUSTOM_NAME+" TEXT, "+
                C_PHONE+" TEXT, "+
                C_INCOMING+" INTEGER" +
                ") ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("delete  from "+ TABLE_NAME);


        for(int i =0; i< 10; i++) {
            RecordModel model = new RecordModel(123*i, 123, "123", "123", "!23", "123", "123", 0);
            model.setFavorite(0);

            ContentValues values = new ContentValues();
            values.put(SQLHelper.C_FILENAME, model.getFile_name());
            values.put(SQLHelper.C_PATH, model.getPath());
            values.put(SQLHelper.C_DURATION, model.getDuration());
            values.put(SQLHelper.C_DATE, model.getReadable_time());
            values.put(SQLHelper.C_START, model.getTime_start());
            values.put(SQLHelper.C_END, model.getTime_end());
            values.put(SQLHelper.C_FAVORITE, model.isFavorite());
            values.put(SQLHelper.C_CUSTOM_NAME, model.getCustom_name());
            values.put(SQLHelper.C_PHONE, model.getPhone());
            values.put(SQLHelper.C_INCOMING, model.isIncoming());

            long id = db.insert(SQLHelper.TABLE_NAME, null,
                    values);
        }
        //  records.add(rm);
//        Cursor c = db.rawQuery("select * from " +TABLE_NAME,null);
//        if(c.moveToNext()){
//            Logger.getLogger().e("index " +c.getInt(0));
//        }
//        c.close();

    }
}
