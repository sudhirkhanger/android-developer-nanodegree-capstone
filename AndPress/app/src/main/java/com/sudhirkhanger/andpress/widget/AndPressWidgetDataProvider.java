/*
 * Copyright 2017 Sudhir Khanger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sudhirkhanger.andpress.widget;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sudhirkhanger.andpress.model.PostColumns;
import com.sudhirkhanger.andpress.model.PostProvider;

import java.util.ArrayList;
import java.util.List;

public class AndPressWidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    private List<String> collection = new ArrayList<>();
    private Context context;
    private Intent intent;

    public AndPressWidgetDataProvider(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
    }

    private void initData() {
        collection.clear();
        ContentResolver contentResolver = context.getContentResolver();

        Cursor cursor = contentResolver.query(PostProvider.Posts.CONTENT_URI,
                new String[]{PostColumns.TITLE},
                null,
                null,
                null);

        if (cursor != null) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                collection.add(cursor.getString(cursor.getColumnIndex(PostColumns.TITLE)));
            }
            cursor.close();
        }
    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return collection.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(
                context.getPackageName(),
                android.R.layout.simple_list_item_1);
        remoteViews.setTextViewText(android.R.id.text1, collection.get(position));
        remoteViews.setTextColor(android.R.id.text1, Color.BLACK);
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
