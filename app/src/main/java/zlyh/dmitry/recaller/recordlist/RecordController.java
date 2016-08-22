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

package zlyh.dmitry.recaller.recordlist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import zlyh.dmitry.recaller.Const;
import zlyh.dmitry.recaller.R;
import zlyh.dmitry.recaller.RecallerApp;
import zlyh.dmitry.recaller.model.RecordModel;
import zlyh.dmitry.recaller.utils.PlayerUtils;

public class RecordController extends RecyclerView.ViewHolder {

    private final TextView phone;
    private final TextView duration;
    private final TextView text;
    private final ImageView img;
    private final ImageView favorite;
    private final ImageView playstop;
    private final ImageView edit;
    private final View rootView;
    private final TextView date;
    private RecordModel record;
    private AlertDialog mydialog;

    public RecordController(final View itemView, final RecordAdapter adapter) {
        super(itemView);
        this.rootView = itemView.findViewById(R.id.root);
        text = (TextView) itemView.findViewById(R.id.text);
        date = (TextView) itemView.findViewById(R.id.date);
        duration = (TextView) itemView.findViewById(R.id.duration);
        phone = (TextView) itemView.findViewById(R.id.phone);
        img = (ImageView) itemView.findViewById(R.id.img);
        favorite = (ImageView) itemView.findViewById(R.id.favorite);
        playstop = (ImageView) itemView.findViewById(R.id.playstop);
        edit = (ImageView) itemView.findViewById(R.id.edit);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMainPopup();
            }
        });

        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(record.isFavorite()) {
                    record.setFavorite(0);
                }else{
                    record.setFavorite(1);
                }

                localBroadcast(Const.Viewholder.FAVORITE);

            }
        });

        playstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!record.is_playing) {
                    PlayerUtils.startPlayingTrack(record);
                    //thread interrution is long, change state fast
                    record.is_playing = true;
                }else {
                    PlayerUtils.stopPlayingTrack();
                    //thread interrution is long, change state fast
                    record.is_playing = false;
                }
                adapter.notifyItemChanged(getAdapterPosition());
            }
        });
    }

    public void bindModel(final RecordModel record, int mode) {
        rootView.setVisibility(View.VISIBLE);
        this.record = record;


        if(record.is_playing){
            playstop.setImageDrawable(RecallerApp.getAppContext().getResources().getDrawable(android.R.drawable.ic_media_pause));
        }else{
            playstop.setImageDrawable(RecallerApp.getAppContext().getResources().getDrawable(android.R.drawable.ic_media_play));
        }


        if((mode & RecordAdapter.FAVORITES) != 0 && !record.isFavorite()){
            rootView.setVisibility(View.GONE);
        }

        if((mode & RecordAdapter.OUTGOING)==0 && !record.isIncoming()){
            rootView.setVisibility(View.GONE);
        }

        if((mode & RecordAdapter.INCOMING) ==0 && record.isIncoming()){
            rootView.setVisibility(View.GONE);
        }




        final String custom_name = record.getCustom_name();
        if(custom_name!=null && !custom_name.isEmpty()){
            text.setText(custom_name);
        }else{
            text.setText(record.getFile_name());
        }



        duration.setText(record.getDuration());
        phone.setText(record.getPhone());
        date.setText(record.getReadable_time());

        if(record.isFavorite()){
            favorite.setImageDrawable(RecallerApp.getAppContext().getResources().getDrawable(android.R.drawable.star_on));
        }else {
            favorite.setImageDrawable(RecallerApp.getAppContext().getResources().getDrawable(android.R.drawable.star_off));
        }

        if(record.isIncoming()){
            img.setImageDrawable(RecallerApp.getAppContext().getResources().getDrawable(android.R.drawable.sym_call_incoming));
        }else{
            img.setImageDrawable(RecallerApp.getAppContext().getResources().getDrawable(android.R.drawable.sym_call_outgoing));
        }

    }

    public void showMainPopup() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
        final View layout = LayoutInflater.from(itemView.getContext()).inflate(R.layout.main_dialog, null);
        setupDialogButtons(layout);
        builder.setView(layout);
        builder.setTitle(R.string.main_popup_title);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mydialog.dismiss();
            }
        });
        mydialog = builder.create();
        mydialog.show();
    }

    private void setupDialogButtons(final View layout) {
        final TextView rename = (TextView) layout.findViewById(R.id.rename);
        final TextView delete = (TextView) layout.findViewById(R.id.delete);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mydialog.isShowing()) {
                    mydialog.dismiss();
                }
                localBroadcast(Const.Viewholder.DELETE);

            }
        });

        rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mydialog.isShowing()) {
                    mydialog.dismiss();
                }
                showRenamePopup();

            }
        });

    }

    public void showRenamePopup(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
        final View layout = LayoutInflater.from(itemView.getContext()).inflate(R.layout.rename_dialog, null);
        final EditText name = (EditText) layout.findViewById(R.id.name);
        builder.setView(layout);
        builder.setTitle(R.string.rename_popup_title)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(!name.getText().toString().isEmpty()) {
                            record.setCustom_name(name.getText().toString());
                            localBroadcast(Const.Viewholder.RENAME);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mydialog.dismiss();
                    }
                });
        mydialog = builder.create();
        mydialog.show();
    }

    private void localBroadcast(int command){
        Intent intent = new Intent(Const.Viewholder.BROADCAST).putExtra(Const.COMMAND,command)
                .putExtra(Const.MODEL,record);
        LocalBroadcastManager.getInstance(itemView.getContext()).sendBroadcast(intent);
    }

}
