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

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;

import zlyh.dmitry.recaller.Const;
import zlyh.dmitry.recaller.MainActivity;
import zlyh.dmitry.recaller.broadcast.PlayerReceiver;
import zlyh.dmitry.recaller.broadcast.PreferencesReceiver;
import zlyh.dmitry.recaller.broadcast.SqlReceiver;
import zlyh.dmitry.recaller.broadcast.ViewholderReceiver;


public class ActivityUtils {

    public static void registerReceivers(MainActivity activity) {
        final ArrayList<BroadcastReceiver> receivers = activity.getReceivers();
        receivers.add(new PlayerReceiver(activity));
        receivers.add(new PreferencesReceiver(activity));
        receivers.add(new SqlReceiver(activity));
        receivers.add(new ViewholderReceiver(activity));

        IntentFilter filter = new IntentFilter(Const.BROADCAST);

        for(BroadcastReceiver receiver : receivers) {
            LocalBroadcastManager.getInstance(activity).registerReceiver(receiver, filter);
        }
    }

    public static void unregisterReceivers(MainActivity activity){
        final ArrayList<BroadcastReceiver> receivers = activity.getReceivers();
        for(BroadcastReceiver receiver : receivers) {
            if(receiver!=null){
                LocalBroadcastManager.getInstance(activity).unregisterReceiver(receiver);
            }
        }
    }

}
