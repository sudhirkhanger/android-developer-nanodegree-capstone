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

package com.sudhirkhanger.andpress.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sudhirkhanger.andpress.R;
import com.sudhirkhanger.andpress.model.PostColumns;
import com.sudhirkhanger.andpress.model.PostProvider;
import com.sudhirkhanger.andpress.ui.DetailActivity;

public class WordPressPostAdapter extends CursorRecyclerViewAdapter
        <WordPressPostAdapter.WordPressViewHolder> {

    public static final String TAG = WordPressPostAdapter.class.getSimpleName();

    public static final String INTENT_KEY_POST_URI = "post_uri";
    private Context context;
    private Cursor cursor;

    public WordPressPostAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public WordPressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item, parent, false);

        return new WordPressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final WordPressViewHolder viewHolder, final Cursor cursor) {

        final String title = cursor.getString(cursor.getColumnIndex(PostColumns.TITLE));
        final String featured_image = cursor.getString(
                cursor.getColumnIndex(PostColumns.IMAGE_URL));

        viewHolder.textView.setText(title);
        if (TextUtils.isEmpty(featured_image)) {
            Picasso.with(context)
                    .load(R.color.colorPrimaryDark)
                    .resize(600, 200)
                    .centerCrop()
                    .placeholder(R.color.colorPrimaryDark)
                    .into(viewHolder.imageView);
        } else {
            Picasso.with(context)
                    .load(featured_image)
                    .resize(600, 200)
                    .centerCrop()
                    .placeholder(R.color.colorPrimaryDark)
                    .into(viewHolder.imageView);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                cursor.moveToPosition(position);
                final int id = cursor.getInt(cursor.getColumnIndex(PostColumns._ID));
                final Uri uri = PostProvider.Posts.withId(id);
                Log.e(TAG, "onClick: " + uri);
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra(INTENT_KEY_POST_URI, uri.toString());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    public static class WordPressViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;

        public WordPressViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.list_item_title);
            imageView = (ImageView) view.findViewById(R.id.list_item_image);
        }
    }
}
