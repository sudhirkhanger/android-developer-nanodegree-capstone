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

package com.sudhirkhanger.andpress.rest;

import android.os.AsyncTask;
import android.util.Log;

import com.sudhirkhanger.andpress.model.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class WordPressAsyncTask extends AsyncTask<String, Void, ArrayList<Post>> {

    public static final String LOG_TAG = WordPressAsyncTask.class.getSimpleName();

    public WordPressResponse delegate = null;

    public WordPressAsyncTask(WordPressResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected ArrayList<Post> doInBackground(String... params) {

        // https://gist.github.com/anonymous/1c04bf2423579e9d2dcd
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String postJsonStr = null;

        try {
            // Construct the URL for the Google Books API query
            URL url = new URL(params[0]);

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                postJsonStr = null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                postJsonStr = null;
            }
            postJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            postJsonStr = null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return jsonConvertor(postJsonStr);
    }

    @Override
    protected void onPostExecute(ArrayList<Post> postArrayList) {
        delegate.processFinish(postArrayList);
    }

    private ArrayList<Post> jsonConvertor(String postJsonStr) {
        ArrayList<Post> postArrayList = new ArrayList<>();

        try {
            JSONArray baseJsonArray = new JSONArray(postJsonStr);
            int numberOfItems = baseJsonArray.length();

            for (int i = 0; i < numberOfItems; i++) {
                JSONObject item = baseJsonArray.getJSONObject(i);

                int id = item.getInt("id");

                JSONObject titleObject = item.getJSONObject("title");
                String title = titleObject.getString("rendered");

                JSONObject contentObject = item.getJSONObject("content");
                String content = contentObject.getString("rendered");

                int featured_media = item.getInt("featured_media");

                Post post = new Post(id, title, String.valueOf(featured_media), content);
                postArrayList.add(post);
            }
        } catch (JSONException e) {
            Log.d(LOG_TAG, e.toString());
        }
        return postArrayList;
    }
}
