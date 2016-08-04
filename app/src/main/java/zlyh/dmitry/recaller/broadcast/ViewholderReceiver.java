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

package zlyh.dmitry.recaller.broadcast;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.lang.ref.WeakReference;

import zlyh.dmitry.recaller.Const;
import zlyh.dmitry.recaller.MainActivity;
import zlyh.dmitry.recaller.model.RecordModel;
import zlyh.dmitry.recaller.utils.FileUtils;
import zlyh.dmitry.recaller.utils.SqlUtils;


public class ViewholderReceiver extends BroadcastReceiver {
    final WeakReference<Activity> activityref;

    public ViewholderReceiver(Activity activityref) {
        this.activityref = new WeakReference<>(activityref);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int command = intent.getIntExtra(Const.COMMAND, -1);
        final MainActivity activity = (MainActivity) activityref.get();
        if (activity == null) {
            return;
        }

        switch (command) {
            case Const.Viewholder.DELETE:
                deleteRecord(activity,intent);
                break;
            case Const.Viewholder.FAVORITE:
                favoriteRecord(activity,intent);
                break;
            case Const.Viewholder.RENAME:
                renameRecord(activity,intent);
                break;
        }
    }
    private void deleteRecord(MainActivity activity, Intent intent) {
        final RecordModel delete_record =  intent.getParcelableExtra(Const.MODEL);
        final int delete_pos = activity.getRecords().indexOf(delete_record);
        SqlUtils.deleteRecord(delete_record);
        activity.getRecords().remove(delete_pos);
        activity.getAdapter().notifyItemRemoved(delete_pos);

        FileUtils.deleteFile(delete_record);

    }

    private void favoriteRecord(MainActivity activity, Intent intent) {
        final RecordModel update_record =  intent.getParcelableExtra(Const.MODEL);
        final int update_pos = activity.getRecords().indexOf(update_record);
        SqlUtils.saveRecord(update_record);
        activity.getAdapter().notifyItemChanged(update_pos);
    }

    private void renameRecord(MainActivity activity, Intent intent) {
        final RecordModel rename_record =  intent.getParcelableExtra(Const.MODEL);
        final int update_pos = activity.getRecords().indexOf(rename_record);
        SqlUtils.saveRecord(rename_record);
        activity.getAdapter().notifyItemChanged(update_pos);

        FileUtils.renameFile(rename_record);
    }

}
