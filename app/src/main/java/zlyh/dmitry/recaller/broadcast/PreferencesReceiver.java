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
import zlyh.dmitry.recaller.R;
import zlyh.dmitry.recaller.recordlist.RecordAdapter;

public class PreferencesReceiver extends BroadcastReceiver{
    final WeakReference<Activity> activityref;

    public PreferencesReceiver(Activity activityref) {
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
            case Const.Prefs.CALLS_LOAD:
                updatePrefsUI(activity, intent);
                break;
            case Const.Prefs.FILTER_LOAD:
                updateFilterPrefsUI(activity, intent);
                break;
            case Const.Prefs.NOTIF_LOAD://fall down
            case Const.Prefs.NOTIF_SAVE:
                updateMenuItem(activity, intent);
                break;
        }

    }

    private void updateMenuItem(MainActivity activity, Intent intent) {
        if(intent.getBooleanExtra(Const.Prefs.NOTIFICATION_OPTION,true)){
            activity.updateMenuText(R.string.disable_notification);
        }else{
            activity.updateMenuText(R.string.enable_notification);
        }
    }

    private void updatePrefsUI(MainActivity activity,Intent intent) {
        final boolean record_input = intent.getBooleanExtra(Const.Prefs.REC_IN_OPTION, true);
        final boolean record_output = intent.getBooleanExtra(Const.Prefs.REC_OUT_OPTION, true);

        activity.getCall_in().setChecked(record_input);
        activity.getCall_out().setChecked(record_output);
        activity.attachRecordCheckedListeners();
    }

    private void updateFilterPrefsUI(MainActivity activity, Intent intent) {
        final RecordAdapter adapter= activity.getAdapter();
        final boolean fav_filter = intent.getBooleanExtra(Const.Prefs.FAV_FILTER_OPTION, false);
        final boolean inc_filter = intent.getBooleanExtra(Const.Prefs.INC_FILTER_OPTION, true);
        final boolean out_filer =  intent.getBooleanExtra(Const.Prefs.OUT_FILTER_OPTION, true);

        activity.getFavorites_filter().setChecked(fav_filter);
        activity.getIncoming_filter().setChecked(inc_filter);
        activity.getOutgoing_filter().setChecked(out_filer);

        if (fav_filter) {
            adapter.addMode(RecordAdapter.FAVORITES);
        } else {
            adapter.removeMode(RecordAdapter.FAVORITES);
        }

        if (inc_filter) {
            adapter.addMode(RecordAdapter.INCOMING);
        } else {
            adapter.removeMode(RecordAdapter.INCOMING);
        }

        if (out_filer) {
            adapter.addMode(RecordAdapter.OUTGOING);
        } else {
            adapter.removeMode(RecordAdapter.OUTGOING);
        }
        activity.attachFilterCheckedListeners();
    }

}
