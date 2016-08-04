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

package zlyh.dmitry.recaller;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

import zlyh.dmitry.recaller.model.RecordModel;
import zlyh.dmitry.recaller.recordlist.RecordAdapter;
import zlyh.dmitry.recaller.utils.ActivityUtils;
import zlyh.dmitry.recaller.utils.PlayerUtils;
import zlyh.dmitry.recaller.utils.PreferenceUtils;
import zlyh.dmitry.recaller.utils.ScrollLoadMore;
import zlyh.dmitry.recaller.utils.SqlUtils;

public class MainActivity extends AppCompatActivity {
    private ArrayList<BroadcastReceiver> receivers = new ArrayList<>();

//    private PlayerReceiver playerReceiver;
//    private PreferencesReceiver preferencesReceiver;
//    private SqlReceiver sqlReceiver;
//    private ViewholderReceiver viewholderReceiver;

    private CheckBox call_out;
    private CheckBox call_in;
    private RecordAdapter adapter;
    private ArrayList<RecordModel> records = new ArrayList<>();
    private CheckBox favorites_filter;
    private CheckBox incoming_filter;
    private CheckBox outgoing_filter;
    private ScrollLoadMore scrollListener;
    private TextView noCallsText;
    private RecyclerView list;
    private MenuItem menu_notification_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            records = savedInstanceState.getParcelableArrayList(Const.MODEL);
        }

        setupUI();
        PreferenceUtils.loadCallAndFilterPreferences();
        SqlUtils.loadRecords(records.size());
        ActivityUtils.registerReceivers(this);
        PlayerUtils.startPlayerService();
    }


    @Override
    protected void onDestroy() {
        ActivityUtils.unregisterReceivers(this);
        PlayerUtils.stopPlayerService();
        super.onDestroy();
    }

    private void setupUI() {
        call_in = (CheckBox) findViewById(R.id.call_in);
        call_out = (CheckBox) findViewById(R.id.call_out);
        noCallsText = (TextView) findViewById(R.id.nocalls);
        list = (RecyclerView) findViewById(R.id.list);
        scrollListener = new ScrollLoadMore(records);
        list.addOnScrollListener(scrollListener);
        ((SimpleItemAnimator) list.getItemAnimator()).setSupportsChangeAnimations(false);


        favorites_filter = (CheckBox) findViewById(R.id.favorites_filter);
        outgoing_filter = (CheckBox) findViewById(R.id.outgoing_filter);
        incoming_filter = (CheckBox) findViewById(R.id.incoming_filter);

        adapter = new RecordAdapter(records);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);


    }

    public void attachRecordCheckedListeners() {
        call_in.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceUtils.saveRecordPreference(isChecked, call_out.isChecked());
            }
        });

        call_out.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceUtils.saveRecordPreference(call_in.isChecked(), isChecked );
            }
        });
    }

    public void attachFilterCheckedListeners() {

        favorites_filter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    adapter.addMode(RecordAdapter.FAVORITES);
                } else {
                    adapter.removeMode(RecordAdapter.FAVORITES);
                }
                PreferenceUtils.saveFilterPreference(incoming_filter.isChecked(), outgoing_filter.isChecked(), isChecked);
            }
        });

        incoming_filter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    adapter.addMode(RecordAdapter.INCOMING);
                } else {
                    adapter.removeMode(RecordAdapter.INCOMING);
                }
                PreferenceUtils.saveFilterPreference(isChecked, outgoing_filter.isChecked(), favorites_filter.isChecked());
            }
        });

        outgoing_filter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    adapter.addMode(RecordAdapter.OUTGOING);
                } else {
                    adapter.removeMode(RecordAdapter.OUTGOING);
                }
                PreferenceUtils.saveFilterPreference(incoming_filter.isChecked(), isChecked, favorites_filter.isChecked());
            }
        });

        checkPermissions();

    }

    private void checkPermissions() {
        if( ContextCompat.checkSelfPermission(this,Manifest.permission.PROCESS_OUTGOING_CALLS) == PackageManager.PERMISSION_GRANTED &&
              ContextCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
              ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &
              ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
          return;
      }


        Dexter.checkPermissions(new MultiplePermissionsListener() {
                @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                    List<String> grantedPermissions = new ArrayList<>();
                    for(PermissionGrantedResponse response: report.getGrantedPermissionResponses()){
                        if(!grantedPermissions.contains(response.getPermissionName())){
                            grantedPermissions.add(response.getPermissionName());
                        }
                    }

                    if(grantedPermissions.size()!=4){
                        call_out.setChecked(false);
                        call_in.setChecked(false);
                        call_out.setEnabled(false);
                        call_in.setEnabled(false);
                    }else{
                        call_out.setChecked(true);
                        call_in.setChecked(true);
                        call_in.setEnabled(true);
                        call_out.setEnabled(true);
                    }

                }
                @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    token.continuePermissionRequest();
                }
            }, Manifest.permission.PROCESS_OUTGOING_CALLS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Const.MODEL, records);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            records = savedInstanceState.getParcelableArrayList(Const.MODEL);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.call_menu, menu);
        menu_notification_text = menu.findItem(R.id.disable_notif);
        //onCreateOptionsMenu called after oncreate
        PreferenceUtils.loadNotificationPreference();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.remove_short:
                SqlUtils.removeShortRecords(records);
                adapter.notifyDataSetChanged();
                return true;
            case R.id.disable_notif:

                if(menu_notification_text.getTitle().toString().toLowerCase().contains("enable")) {
                    PreferenceUtils.saveNotificationPreference(true);
                }else{
                    PreferenceUtils.saveNotificationPreference(false);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public CheckBox getCall_out() {
        return call_out;
    }

    public CheckBox getCall_in() {
        return call_in;
    }

    public RecordAdapter getAdapter() {
        return adapter;
    }

    public ArrayList<RecordModel> getRecords() {
        return records;
    }

    public CheckBox getFavorites_filter() {
        return favorites_filter;
    }

    public CheckBox getIncoming_filter() {
        return incoming_filter;
    }

    public CheckBox getOutgoing_filter() {
        return outgoing_filter;
    }

    public ScrollLoadMore getScrollListener() {
        return scrollListener;
    }

    public ArrayList<BroadcastReceiver> getReceivers() {
        return receivers;
    }


    public TextView getNoCallsText() {
        return noCallsText;
    }

    public RecyclerView getList(){return list;}

    public void updateMenuText(@StringRes int resid) {
        menu_notification_text.setTitle(resid);
    }
}