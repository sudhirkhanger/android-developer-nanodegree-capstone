/*
 * Copyright 2016 Sudhir Khanger
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

package com.sudhirkhanger.andpress.model;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(authority = PostProvider.AUTHORITY, database = PostDatabase.class)
public final class PostProvider {

    public static final String AUTHORITY =
            "com.sudhirkhanger.andpress.model.PostProvider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public interface Path {
        String POSTS = "posts";
    }

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = PostDatabase.POSTS)
    public static class Posts {
        @ContentUri(
                path = Path.POSTS,
                type = "vnd.android.cursor.dir/post",
                defaultSort = PostColumns._ID + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.POSTS);

        @InexactContentUri(
                name = "POST_ID",
                path = Path.POSTS + "/#",
                type = "vnd.android.cursor.item/planet",
                whereColumn = PostColumns._ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(Path.POSTS, String.valueOf(id));
        }
    }
}
