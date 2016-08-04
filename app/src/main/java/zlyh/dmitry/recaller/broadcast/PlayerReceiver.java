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
import java.util.ArrayList;

import zlyh.dmitry.recaller.Const;
import zlyh.dmitry.recaller.MainActivity;
import zlyh.dmitry.recaller.model.RecordModel;


public class PlayerReceiver extends BroadcastReceiver {
    final WeakReference<Activity> activityref;

    public PlayerReceiver(Activity activityref) {
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
            case Const.PlayerService.PLAY:
                updatePlayerUI(activity, intent, true);
                break;
            case Const.PlayerService.STOP:
                updatePlayerUI(activity, intent, false);
                break;
        }
    }

    private void updatePlayerUI(MainActivity activity, Intent intent, boolean isplaying) {
        final int id = intent.getIntExtra(Const.MODEL, -1);
        final ArrayList<RecordModel> records = activity.getRecords();
        for (RecordModel recordModel : records) {
            if (recordModel.getId() == id) {
                recordModel.is_playing = isplaying;
                activity.getAdapter().notifyItemChanged(records.indexOf(recordModel));
            }
        }
    }
}
