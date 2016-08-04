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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import zlyh.dmitry.recaller.R;
import zlyh.dmitry.recaller.model.RecordModel;

public class RecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int FAVORITES = 1;
    public static final int INCOMING = 1 << 1;
    public static final int OUTGOING = 1 << 2;

    private int mode = INCOMING | OUTGOING;

    private ArrayList<RecordModel> records = new ArrayList<>();

    public RecordAdapter(ArrayList<RecordModel> records) {
        this.records = records;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecordController(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.record_item, parent, false),this);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof RecordController) {
            ((RecordController) viewHolder).bindModel(records.get(position), mode);
        }
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public void addMode(int mode_flag){
        mode = mode | mode_flag;
        notifyDataSetChanged();
    }

    public void removeMode(int mode_flag){
        mode = mode & ~mode_flag;
        notifyDataSetChanged();
    }
}
