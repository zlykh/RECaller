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

import android.app.Application;
import android.content.Context;

import com.karumi.dexter.Dexter;

import zlyh.dmitry.recaller.utils.SQLHelper;


public class RecallerApp extends Application {
    public static final String PACKAGE = "zlyh.dmitry.recaller";

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Dexter.initialize(context);


    }

    public static Context getAppContext() {
        return context;
    }

    @Override
    public void onTerminate() {
        SQLHelper.getInstance().close();
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        SQLHelper.getInstance().close();
        super.onLowMemory();
    }
}
