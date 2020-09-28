package com.example.movies.utils;

import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class NetworkUtils {
    private static final String API_KEY="6288b377337b42638795136e1ce6d292";
    private static final String BASE_URL="https://api.themoviedb.org/3/discover/movie";
    private static final String BASE_URL_VIDEOS= "https://api.themoviedb.org/3/movie/%s/videos";
    private static final String BASE_URL_REVIEWS= "https://api.themoviedb.org/3/movie/%s/reviews";

    private static final String PARAMS_API_KEY = "api_key";
    private static final String PARAMS_LANGUAGE = "language";
    private static final String PARAMS_SORT_BY = "sort_by";
    private static final String PARAMS_PAGE = "page";

    private static final String LANGUAGE_VALUE = "ru-RU";
    private static final String SORT_BY_POPULARITY = "popularity.desc";
    private static final String SORT_BY_TOP_RATED = "vote_average.desc";

    public static final int POPULARITY =0;
    public static final int TOP_RATED =1;

    private static URL buildURLtoReviews(int filmId){
        Uri uri = Uri.parse(String.format(BASE_URL_REVIEWS,filmId)).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY,API_KEY)
                .build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static JSONObject getJSONForReviews(int filmId){
        JSONObject result = null;
        URL url = buildURLtoReviews(filmId);
        try {
            result = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static URL buildURLtoVideos(int filmId){
        Uri uri = Uri.parse(String.format(BASE_URL_VIDEOS,filmId)).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY,API_KEY)
                .appendQueryParameter(PARAMS_LANGUAGE,LANGUAGE_VALUE)
                .build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject getJSONForVideos(int filmId){
        JSONObject result = null;
        URL url = buildURLtoVideos(filmId);
        try {
            result = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static URL buildURL(int sortBy,int page){
        URL result = null;
        String methodOfSort;
        if(sortBy == POPULARITY){
            methodOfSort = SORT_BY_POPULARITY;
        }else{
            methodOfSort = SORT_BY_TOP_RATED;
        }
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY,API_KEY)
                .appendQueryParameter(PARAMS_LANGUAGE,LANGUAGE_VALUE)
                .appendQueryParameter(PARAMS_SORT_BY,methodOfSort)
                .appendQueryParameter(PARAMS_PAGE,Integer.toString(page))
                .build();
        try {
             result = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JSONObject getJSONFromNetWork(int sortBy,int page){
        JSONObject result = null;
        URL url = buildURL(sortBy,page);
        try {
            result = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
    private static class JSONLoadTask extends AsyncTask<URL,Void, JSONObject>{

        @Override
        protected JSONObject doInBackground(URL... urls) {
            JSONObject result = null;
            if(urls == null || urls.length == 0){
                return result;
            }
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) urls[0].openConnection();
                InputStream inputStream = connection.getInputStream();
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                //Для чтения строками, а не по символам
//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder builder = new StringBuilder();
                String line = bufferedReader.readLine();
                while (line!=null){
                    builder.append(line);
                    line = bufferedReader.readLine();
                }
                result = new JSONObject(builder.toString());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }finally {
                if(connection!=null){
                    connection.disconnect();
                }
            }
            return result;
        }
    }


}
