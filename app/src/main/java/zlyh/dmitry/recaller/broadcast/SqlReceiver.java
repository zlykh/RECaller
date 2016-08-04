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
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import zlyh.dmitry.recaller.Const;
import zlyh.dmitry.recaller.MainActivity;
import zlyh.dmitry.recaller.model.RecordModel;
import zlyh.dmitry.recaller.utils.SQLHelper;


public class SqlReceiver extends BroadcastReceiver {
    final WeakReference<Activity> activityref;

    public SqlReceiver(Activity activityref) {
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
            case Const.SqlService.LOAD:
                updateRecordsList(activity, intent);
                break;
            case Const.SqlService.SAVE:
                addNewRecord(activity, intent);
                break;
            case Const.SqlService.DELETE:
                break;
        }

    }

    private void updateRecordsList(MainActivity activity, Intent intent) {
        final ArrayList<RecordModel> newlist = intent.getParcelableArrayListExtra(Const.MODEL);
        activity.getScrollListener().setHasMore(!(newlist.size() < SQLHelper.SELECTION_LIMIT));
        activity.getScrollListener().setLoading(false);

        final int old_size = activity.getRecords().size();
        activity.getRecords().addAll(newlist);
        final int new_size = activity.getRecords().size();

        activity.getAdapter().notifyItemRangeInserted(old_size, new_size - old_size);

        if(activity.getRecords().isEmpty()){
            activity.getNoCallsText().setVisibility(View.VISIBLE);
        }
    }

    private void addNewRecord(MainActivity activity, Intent intent) {
        activity.getRecords().add(0, (RecordModel) intent.getParcelableExtra(Const.MODEL));
        activity.getAdapter().notifyItemInserted(0);
        activity.getList().scrollToPosition(0);
    }
}
