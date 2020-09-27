package com.example.movies.data;

import androidx.room.Entity;
import androidx.room.Ignore;

@Entity (tableName = "favorite_movies")
public class FavoriteMovie extends Movie {
    public FavoriteMovie(int id, int voteCount, String title, String originalTitle, String overview, String posterPath, String bigPosterPath, String backdropPath, double voteAverage, String releaseDate) {
        super(id, voteCount, title, originalTitle, overview, posterPath, bigPosterPath, backdropPath, voteAverage, releaseDate);
    }
    //Т.к. наш класс является сущностью в БД, то ему нужен только один конструктор,поэтому помечаем аннотацией игнор
    @Ignore
    public FavoriteMovie(Movie movie) {
        super(movie.getId(),
                movie.getVoteCount(),
                movie.getTitle(),
                movie.getOriginalTitle(),
                movie.getOverview(),
                movie.getPosterPath(),
                movie.getBigPosterPath(),
                movie.getBackdropPath(),
                movie.getVoteAverage(),
                movie.getReleaseDate());
    }
}
