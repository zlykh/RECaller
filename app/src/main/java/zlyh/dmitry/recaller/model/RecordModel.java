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

package zlyh.dmitry.recaller.model;

import android.os.Parcel;
import android.os.Parcelable;

public class RecordModel implements Parcelable{
    private int id = -1;
    private long time_start;
    private long time_end;
    private String duration;
    private String file_name;
    private String path;
    private String readable_time;
    private int favorite;
    private String custom_name;
    private String phone;
    private int was_incoming;
    public boolean is_playing = false;

    public RecordModel() {
    }

    public RecordModel(long time_start, long time_end, String duration, String file_name,
                       String path, String readable_time, String phone, int was_incoming) {
        this.time_start = time_start;
        this.time_end = time_end;
        this.duration = duration;
        this.file_name = file_name;
        this.path = path;
        this.readable_time = readable_time;
        this.favorite = 0;
        this.custom_name = "";
        this.phone = phone;
        this.was_incoming = was_incoming;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setTime_start(long time_start) {
        this.time_start = time_start;
    }

    public void setTime_end(long time_end) {
        this.time_end = time_end;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setReadable_time(String readable_time) {
        this.readable_time = readable_time;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

    public void setCustom_name(String custom_name) {
        this.custom_name = custom_name;
    }

    public long getTime_start() {
        return time_start;
    }

    public long getTime_end() {
        return time_end;
    }

    public String getDuration() {
        return duration;
    }

    public String getFile_name() {
        return file_name;
    }

    public String getPath() {
        return path;
    }

    public String getReadable_time() {
        return readable_time;
    }

    public boolean isFavorite() {
        return favorite > 0;
    }

    public String getCustom_name() {
        return custom_name;
    }

    public String getPhone() {
        return phone;
    }

    public int getId() {
        return id;
    }

    public boolean isIncoming() {
        return was_incoming > 0;
    }

    public void setIncoming(int is_incoming) {
        this.was_incoming = is_incoming;
    }

    protected RecordModel(Parcel in) {
        id = in.readInt();
        time_start = in.readLong();
        time_end = in.readLong();
        duration = in.readString();
        file_name = in.readString();
        path = in.readString();
        readable_time = in.readString();
        favorite = in.readInt();
        custom_name = in.readString();
        phone = in.readString();
        was_incoming = in.readInt();

    }

    public static final Creator<RecordModel> CREATOR = new Creator<RecordModel>() {
        @Override
        public RecordModel createFromParcel(Parcel in) {
            return new RecordModel(in);
        }

        @Override
        public RecordModel[] newArray(int size) {
            return new RecordModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeLong(time_start);
        dest.writeLong(time_end);
        dest.writeString(duration);
        dest.writeString(file_name);
        dest.writeString(path);
        dest.writeString(readable_time);
        dest.writeInt(favorite);
        dest.writeString(custom_name);
        dest.writeString(phone);
        dest.writeInt(was_incoming);
    }

    @Override
    public String toString() {
        return "RecordModel{" +
                "id=" + id +
                "time_start=" + time_start +
                ", time_end=" + time_end +
                ", duration='" + duration + '\'' +
                ", file_name='" + file_name + '\'' +
                ", path='" + path + '\'' +
                ", readable_time='" + readable_time + '\'' +
                ", favorite=" + favorite +
                ", custom_name='" + custom_name + '\'' +
                ", phone='" + phone + '\'' +
                ", incoming='" + was_incoming + '\'' +
                '}';
    }
}
