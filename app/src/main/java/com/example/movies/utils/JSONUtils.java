package com.example.movies.utils;

import com.example.movies.data.Movie;
import com.example.movies.data.Review;
import com.example.movies.data.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONUtils {

    private static final String KET_RESULTS = "results";
    //Для отзывов
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_CONTENT = "content";

    //Для видео
    private static final String KEY_KEY_OF_VIDEO = "key";
    private static final String KEY_NAME = "name";
    private static final String BASE_YOUTUBE_URL = "https://www.youtube.com/watch?v=";

    //вся информация о фильме
    private static final String KEY_VOTE_COUNT = "vote_count";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_ORIGINAL_TITLE = "original_title";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_POSTER_PATH = "poster_path";
    private static final String KEY_BACKDROP_PATH = "backdrop_path";
    private static final String KEY_VOTE_AVERAGE = "vote_average";
    private static final String KEY_RELEASE_DATE = "release_date";

    public static final String BASE_POSTER_URL = "https://image.tmdb.org/t/p/";
    public static final String SMALL_POSTER_SIZE = "w185";
    public static final String BIG_POSTER_SIZE = "w780";

    //Получение из JSON массив данных о отзывах
    public static ArrayList<Review> getReviewFromJSON(JSONObject jsonObject) {
        ArrayList<Review> result = new ArrayList<>();
        if (jsonObject == null) {
            return result;
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KET_RESULTS);
            for (int positionInJSONArray = 0; positionInJSONArray < jsonArray.length(); positionInJSONArray++) {
                JSONObject objectReview = jsonArray.getJSONObject(positionInJSONArray);
                String author = objectReview.getString(KEY_AUTHOR);
                String content = objectReview.getString(KEY_CONTENT);
                result.add(new Review(author, content));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    //Получение из JSON массив данных о трейлере
    public static ArrayList<Trailer> getTrailerFromJSON(JSONObject jsonObject) {
        ArrayList<Trailer> result = new ArrayList<>();
        if (jsonObject == null) {
            return result;
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KET_RESULTS);
            for (int positionInJSONArray = 0; positionInJSONArray < jsonArray.length(); positionInJSONArray++) {
                JSONObject objectTrailers = jsonArray.getJSONObject(positionInJSONArray);
                String key = BASE_YOUTUBE_URL + objectTrailers.getString(KEY_KEY_OF_VIDEO);
                String name = objectTrailers.getString(KEY_NAME);
                result.add(new Trailer(key, name));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    //Получение из JSON массив данных о фильме
    public static ArrayList<Movie> getMoviesFromJSON(JSONObject jsonObject) {
        ArrayList<Movie> result = new ArrayList<>();
        if (jsonObject == null) {
            return result;
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KET_RESULTS);
            for (int positionInJSONArray = 0; positionInJSONArray < jsonArray.length(); positionInJSONArray++) {
                JSONObject objectMovie = jsonArray.getJSONObject(positionInJSONArray);
                int id = objectMovie.getInt(KEY_ID);
                int voteCount = objectMovie.getInt(KEY_VOTE_COUNT);
                String title = objectMovie.getString(KEY_TITLE);
                String originalTitle = objectMovie.getString(KEY_ORIGINAL_TITLE);
                String overview = objectMovie.getString(KEY_OVERVIEW);
                String posterPath = BASE_POSTER_URL + SMALL_POSTER_SIZE + objectMovie.getString(KEY_POSTER_PATH);
                String bigPosterPath = BASE_POSTER_URL + BIG_POSTER_SIZE + objectMovie.getString(KEY_POSTER_PATH);
                String backdropPath = objectMovie.getString(KEY_BACKDROP_PATH);
                double voteAverage = objectMovie.getDouble(KEY_VOTE_AVERAGE);
                String releaseDate = objectMovie.getString(KEY_RELEASE_DATE);
                //Добавили в массив
                result.add(new Movie(id, voteCount, title, originalTitle, overview,
                        posterPath, bigPosterPath, backdropPath, voteAverage, releaseDate));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }


}
