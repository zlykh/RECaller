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
import zlyh.dmitry.recaller.model.RecordModel;
import zlyh.dmitry.recaller.services.PlayerService;

/**
 * Created by kotik on 02.08.2016.
 */
public class PlayerUtils {
    public static void startPlayerService() {
        RecallerApp.getAppContext().startService(new Intent(RecallerApp.getAppContext(), PlayerService.class));
    }

    public static void stopPlayerService() {
        RecallerApp.getAppContext().stopService(new Intent(RecallerApp.getAppContext(), PlayerService.class));
    }

    public static void startPlayingTrack(RecordModel record){
        Intent intent = new Intent(RecallerApp.getAppContext(), PlayerService.class);
        intent.putExtra(Const.COMMAND, Const.PlayerService.PLAY).putExtra(Const.MODEL, record);
        RecallerApp.getAppContext().startService(intent);
    }

    public static void stopPlayingTrack() {
        Intent stop_play = new Intent(RecallerApp.getAppContext(), PlayerService.class);
        stop_play.putExtra(Const.COMMAND, Const.PlayerService.STOP);
        RecallerApp.getAppContext().startService(stop_play);
    }
}
