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
import zlyh.dmitry.recaller.services.PreferencesService;

public class PreferenceUtils {
    public static void loadCallAndFilterPreferences() {
        Intent intent = new Intent(RecallerApp.getAppContext(), PreferencesService.class);
        intent.putExtra(Const.COMMAND, Const.Prefs.CALLS_LOAD);
        RecallerApp.getAppContext().startService(intent);

        intent = new Intent(RecallerApp.getAppContext(), PreferencesService.class);
        intent.putExtra(Const.COMMAND, Const.Prefs.FILTER_LOAD);
        RecallerApp.getAppContext().startService(intent);
    }

    public static void saveRecordPreference(boolean record_in, boolean record_out) {
        Intent intent = new Intent(RecallerApp.getAppContext(), PreferencesService.class);
        intent.putExtra(Const.COMMAND, Const.Prefs.CALLS_SAVE);
        intent.putExtra(Const.Prefs.REC_IN_OPTION, record_in);
        intent.putExtra(Const.Prefs.REC_OUT_OPTION, record_out);
        RecallerApp.getAppContext().startService(intent);
    }

    public static void saveFilterPreference(boolean inc_filter, boolean out_filter, boolean fav_filter) {
        Intent intent = new Intent(RecallerApp.getAppContext(), PreferencesService.class);
        intent.putExtra(Const.COMMAND, Const.Prefs.FILTER_SAVE);
        intent.putExtra(Const.Prefs.FAV_FILTER_OPTION, fav_filter);
        intent.putExtra(Const.Prefs.OUT_FILTER_OPTION, out_filter);
        intent.putExtra(Const.Prefs.INC_FILTER_OPTION, inc_filter);
        RecallerApp.getAppContext().startService(intent);
    }

    public static void saveNotificationPreference(boolean is_enabled) {
        Intent intent = new Intent(RecallerApp.getAppContext(), PreferencesService.class);
        intent.putExtra(Const.COMMAND, Const.Prefs.NOTIF_SAVE);
        intent.putExtra(Const.Prefs.NOTIFICATION_OPTION, is_enabled);
        RecallerApp.getAppContext().startService(intent);
    }

    public static void loadNotificationPreference(){
        Intent intent = new Intent(RecallerApp.getAppContext(), PreferencesService.class);
        intent.putExtra(Const.COMMAND, Const.Prefs.NOTIF_LOAD);
        RecallerApp.getAppContext().startService(intent);
    }
}
