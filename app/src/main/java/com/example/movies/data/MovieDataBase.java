package com.example.movies.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
@Database(entities = {Movie.class},version = 1,exportSchema = false)
public abstract class  MovieDataBase extends RoomDatabase {
    private static MovieDataBase dataBase;
    private static final String DB_NAME = "movies.dp";
    //Чтобы не было проблем из-за доступа к классу из разных потоков, то нужно добавить блок синзронизации
    private static final Object LOCK = new Object();

    public static MovieDataBase getInstance(Context context){
        synchronized (LOCK){
            if(dataBase == null){
                dataBase = Room.databaseBuilder(context,MovieDataBase.class,DB_NAME).build();
            }
        }

        return dataBase;
    }

    public abstract MovieDao movieDao();

}
