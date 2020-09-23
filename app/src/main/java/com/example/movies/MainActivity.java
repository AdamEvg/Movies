package com.example.movies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movies.data.Movie;
import com.example.movies.utils.JSONUtils;
import com.example.movies.utils.NetworkUtils;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import static com.example.movies.utils.JSONUtils.getMoviesFromJSON;

public class MainActivity extends AppCompatActivity {
private RecyclerView rvPosters;
private Adapter adapter;
private Switch switchSort;
private TextView textViewPopularity;
private TextView textViewTopRated;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvPosters = findViewById(R.id.rvPosters);
        switchSort = findViewById(R.id.switchSort);
        textViewPopularity = findViewById(R.id.textViewPopular);
        textViewTopRated = findViewById(R.id.textViewRate);



        rvPosters.setLayoutManager(new GridLayoutManager(this,2));
        adapter = new Adapter();
        rvPosters.setAdapter(adapter);
        switchSort.setChecked(true);
        switchSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                setOfMethodOfSort(isChecked);
            }
        });
        switchSort.setChecked(false);
        adapter.setOnPosterClickListener(new Adapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Toast.makeText(MainActivity.this, "Позиция клика" + position, Toast.LENGTH_SHORT).show();
            }
        });
        adapter.setOnReachEndListener(new Adapter.OnReachEndListener() {
            @Override
            public void onReachEnd() {
                Toast.makeText(MainActivity.this, "Вызывается метод подгрузки", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClickSetPopularity(View view) {
        setOfMethodOfSort(false);
        switchSort.setChecked(false);
    }

    public void onClickSetTopRated(View view) {
        setOfMethodOfSort(true);
        switchSort.setChecked(true);
    }

    private void setOfMethodOfSort(boolean isChecked){
        int methodOfSort;
        if(isChecked){
            textViewTopRated.setTextColor(getResources().getColor(R.color.colorAccent));
            textViewPopularity.setTextColor(getResources().getColor(R.color.white_color));
            methodOfSort = NetworkUtils.TOP_RATED;
        }else {
            textViewTopRated.setTextColor(getResources().getColor(R.color.white_color));
            textViewPopularity.setTextColor(getResources().getColor(R.color.colorAccent));
            methodOfSort =NetworkUtils.POPULARITY;
        }
        JSONObject jsonObject = NetworkUtils.getJSONFromNetWork(methodOfSort,1);
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);
        adapter.setMovies(movies);
    }
}