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

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import zlyh.dmitry.recaller.model.RecordModel;
import zlyh.dmitry.recaller.utils.SqlUtils;

public class ScrollLoadMore extends RecyclerView.OnScrollListener {
    public static final int SCROLL_OFFSET = 5;
    private boolean isLoading = false;
    private boolean hasMore = true;

    private final WeakReference<ArrayList<RecordModel>> recordsref;

    public ScrollLoadMore(ArrayList<RecordModel> records) {
        this.recordsref = new WeakReference<> (records);
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        ArrayList<RecordModel> records = recordsref.get();
        if(records!=null) {
            final int pos = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
            final int pos2 = records.size() - SCROLL_OFFSET;
            if (pos > pos2) {
                if (!isLoading && hasMore) {
                    isLoading = true;
                    SqlUtils.loadRecords(records.size());
                }
            }
        }
        super.onScrollStateChanged(recyclerView, newState);
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

}
