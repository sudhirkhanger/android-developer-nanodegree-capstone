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

package com.sudhirkhanger.andpress.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import com.sudhirkhanger.andpress.R;
import com.sudhirkhanger.andpress.adapter.WordPressPostAdapter;
import com.sudhirkhanger.andpress.model.PostColumns;

public class DetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = DetailActivity.class.getSimpleName();

    private Uri postUri;
    private WebView webView;
    private static final int WEBVIEW_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        webView = (WebView) findViewById(R.id.webview);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            if (getIntent() != null && getIntent().getExtras() != null) {
                String uriStr = getIntent().getExtras().
                        getString(WordPressPostAdapter.INTENT_KEY_POST_URI);
                Log.e(TAG, "onCreate: " + uriStr);
                postUri = Uri.parse(uriStr);
                getSupportLoaderManager().initLoader(WEBVIEW_LOADER_ID, null, this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                postUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            String content = cursor.getString(
                    cursor.getColumnIndex(PostColumns.CONTENT));
            webView.loadData(content, "text/html", "UTF-8");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
