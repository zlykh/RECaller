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

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

import zlyh.dmitry.recaller.Const;
import zlyh.dmitry.recaller.R;
import zlyh.dmitry.recaller.RecallerApp;
import zlyh.dmitry.recaller.services.RecordService;

public class CallReceiver extends BroadcastReceiver {
    private static boolean incoming_ringing = false;
    private static boolean incoming_offhook = false;
    private static boolean outgoing_offhook = false;
    private static boolean idle = true;
    public static String number = "unknown";
    public static String temp_number;

    private static boolean allow_outgoing_record = true;
    private static boolean allow_incoming_record = true;
    private static boolean settings_read = false;

    //avoid duplicating intent
    private static boolean commandSent = false;

    public CallReceiver() {
        if (!settings_read) {
            loadSetting();
            settings_read = true;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);

        {//temp number. it can be detected in followed intent, but not in first
            if(telephonyManager.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK){
                temp_number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            }
        }
            if (intent.getAction().equals(Const.Prefs.BROADCAST)) {
                switch (intent.getIntExtra(Const.COMMAND, -1)) {
                    case Const.Prefs.CALLS_SAVE:
                        allow_incoming_record = intent.getBooleanExtra(Const.Prefs.REC_IN_OPTION, true);
                        allow_outgoing_record = intent.getBooleanExtra(Const.Prefs.REC_OUT_OPTION, true);
                        break;
                }
            }

            if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
                number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            }

            if (intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
                switch (telephonyManager.getCallState()) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        incoming_ringing = true;
                        break;

                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        if(!commandSent) {
                            if (incoming_ringing) {
                                incoming_offhook = true;
                                number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

                                if (allow_incoming_record) {
                                    commandRecordService(context, Const.RecordService.START, -1);
                                    commandSent = true;

                                }
                            } else if (idle) {
                                outgoing_offhook = true;
                                if (allow_outgoing_record) {
                                    commandRecordService(context, Const.RecordService.START, -1);
                                    commandSent = true;

                                }
                            }
                        }
                        break;

                    case TelephonyManager.CALL_STATE_IDLE:
                            if (incoming_ringing && incoming_offhook && commandSent) {
                                incoming_ringing = false;
                                incoming_offhook = false;
                                if (allow_incoming_record) {
                                    commandRecordService(context, Const.RecordService.STOP, 1);
                                    commandSent = false;

                                }
                            } else if (idle && outgoing_offhook && commandSent) {
                                idle = false;
                                outgoing_offhook = false;
                                if (allow_outgoing_record) {
                                    commandRecordService(context, Const.RecordService.STOP, 0);
                                    commandSent = false;

                                }
                            } else {
                                idle = true;
                            }
                        break;

                }
            }

    }

    private void commandRecordService(Context context, int command,int is_incoming){
        Intent start_intent = new Intent(context,RecordService.class)
                .putExtra(Const.COMMAND,command)
                .putExtra(Const.CallReceiver.TIME_MARK, System.currentTimeMillis())
                .putExtra(Const.CallReceiver.WAS_INCOMING,is_incoming);
                 if(number==null && temp_number!=null){
                      number =temp_number;
                 }
                start_intent.putExtra(Const.CallReceiver.PHONE_NUM, number);

        context.startService(start_intent);
    }

    private synchronized void loadSetting() {
        final SharedPreferences preferences = RecallerApp.getAppContext().
                getSharedPreferences(RecallerApp.getAppContext().getString(R.string.shared_prefs), Context.MODE_PRIVATE);
        allow_incoming_record = preferences.getBoolean(Const.Prefs.REC_IN_OPTION, true);
        allow_outgoing_record = preferences.getBoolean(Const.Prefs.REC_OUT_OPTION, true);
    }
}
