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
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;
import com.sudhirkhanger.andpress.R;
import com.sudhirkhanger.andpress.adapter.WordPressPostAdapter;
import com.sudhirkhanger.andpress.model.PostColumns;

public class DetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = DetailActivity.class.getSimpleName();
    public static final int FB_MIN = 5000;
    public static final int TIMEOUT_DURATION = 1000000;
    public static final String MIME_TYPE = "text/html";
    public static final String ENCODING = "UTF-8";
    public static final String ABOUT_BLANK = "about:blank";

    private Uri postUri;
    private WebView webView;
    private ImageView featuredImageView;
    private TextView titleTextView;
    private static final int WEBVIEW_LOADER_ID = 0;

    private FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        firebaseAnalytics.setAnalyticsCollectionEnabled(true);
        firebaseAnalytics.setMinimumSessionDuration(FB_MIN);
        firebaseAnalytics.setSessionTimeoutDuration(TIMEOUT_DURATION);

        webView = (WebView) findViewById(R.id.detail_webview);
        featuredImageView = (ImageView) findViewById(R.id.detail_image);
        titleTextView = (TextView) findViewById(R.id.detail_title);

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
        Log.e(TAG, "onCreateLoader");
        return new CursorLoader(
                this,
                postUri,
                MainActivity.POST_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.e(TAG, "onLoadFinished");
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            int id = cursor.getInt(
                    cursor.getColumnIndex(PostColumns.POST_ID));
            String title = cursor.getString(
                    cursor.getColumnIndex(PostColumns.TITLE));
            String featured_image = cursor.getString(
                    cursor.getColumnIndex(PostColumns.IMAGE_URL));
            String content = cursor.getString(
                    cursor.getColumnIndex(PostColumns.CONTENT));

            webView.loadData(content, MIME_TYPE, ENCODING);
            titleTextView.setText(title);
            if (TextUtils.isEmpty(featured_image)) {
                Picasso.with(this)
                        .load(R.color.colorPrimaryDark)
                        .resize(600, 200)
                        .centerCrop()
                        .placeholder(R.color.colorPrimaryDark)
                        .into(featuredImageView);
            } else {
                Picasso.with(this)
                        .load(featured_image)
                        .resize(600, 200)
                        .centerCrop()
                        .placeholder(R.color.colorPrimaryDark)
                        .into(featuredImageView);
            }

            Bundle bundle = new Bundle();
            bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, id);
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, title);
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, MIME_TYPE);
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.e(TAG, "onLoaderReset");
        titleTextView.setText("");
        featuredImageView.setImageResource(R.color.colorPrimaryDark);
        webView.loadUrl(ABOUT_BLANK);
    }
}
