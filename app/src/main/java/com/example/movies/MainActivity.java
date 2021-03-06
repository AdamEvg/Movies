package com.example.movies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movies.adapters.Adapter;
import com.example.movies.data.MainViewModel;
import com.example.movies.data.Movie;
import com.example.movies.utils.JSONUtils;
import com.example.movies.utils.NetworkUtils;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<JSONObject> {
    private RecyclerView rvPosters;
    private Adapter adapter;
    private Switch switchSort;
    private TextView textViewPopularity;
    private TextView textViewTopRated;
    private MainViewModel mainViewModel;
    private ProgressBar progressBar;

    private static int page = 1;
    private static String language;
    private static int methodOfSort;
    private static boolean isLoading = false;
    //Любое число
    public static final int LOADER_ID = 10;
    private LoaderManager loaderManager;

    //Переопределили методы для создания меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //В качестве параметра принимает пункт меню
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.main_item:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.favorite_item:
                Intent favorite = new Intent(this, FavoriteActivity.class);
                startActivity(favorite);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private int getColumnCount(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //Получаем обычные пиксели, но нужно перевести в dp, для этого разделим на плотность
        int width = (int)(displayMetrics.widthPixels/displayMetrics.density);
        return (width/185)>2 ? width/185 : 2;

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!hasConnection(this)) {
            Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
        }
        //Получили язык, установленный на устройстве
        language = Locale.getDefault().getLanguage();
        progressBar = findViewById(R.id.progressBarLoading);
        //SingleTon
        loaderManager = LoaderManager.getInstance(this);
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        rvPosters = findViewById(R.id.rvPosters);
        switchSort = findViewById(R.id.switchSort);
        textViewPopularity = findViewById(R.id.textViewPopular);
        textViewTopRated = findViewById(R.id.textViewRate);
        //Второй параметр - число колонок
        rvPosters.setLayoutManager(new GridLayoutManager(this, getColumnCount()));
        adapter = new Adapter();
        rvPosters.setAdapter(adapter);
        switchSort.setChecked(true);
        switchSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                page=1;
                setOfMethodOfSort(isChecked);
            }
        });
        switchSort.setChecked(false);
        adapter.setOnPosterClickListener(new Adapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Movie movie = adapter.getMovies().get(position);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("id", movie.getId());
                startActivity(intent);
            }
        });
        adapter.setOnReachEndListener(new Adapter.OnReachEndListener() {
            @Override
            public void onReachEnd() {
                if(!isLoading){
                    downLoadData(methodOfSort,page);
                }
            }
        });
        LiveData<List<Movie>> moviesFromLiveData = mainViewModel.getMovies();
        moviesFromLiveData.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                if(page==1){
                    adapter.setMovies(movies);
                }
            }
        });
    }

    //Метод, проверяющий соединение с интернетом( надо добавить в манифест разрешение )
    public static boolean hasConnection(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public void onClickSetPopularity(View view) {
        setOfMethodOfSort(false);
        switchSort.setChecked(false);
    }

    public void onClickSetTopRated(View view) {
        setOfMethodOfSort(true);
        switchSort.setChecked(true);
    }

    private void setOfMethodOfSort(boolean isChecked) {
        if (isChecked) {
            textViewTopRated.setTextColor(getResources().getColor(R.color.colorAccent));
            textViewPopularity.setTextColor(getResources().getColor(R.color.white_color));
            methodOfSort = NetworkUtils.TOP_RATED;
        } else {
            textViewTopRated.setTextColor(getResources().getColor(R.color.white_color));
            textViewPopularity.setTextColor(getResources().getColor(R.color.colorAccent));
            methodOfSort = NetworkUtils.POPULARITY;
        }
        downLoadData(methodOfSort, page);
    }

    private void downLoadData(int methodOfSort, int page) {
        URL url = NetworkUtils.buildURL(methodOfSort, page,language);
        Bundle bundle = new Bundle();
        bundle.putString("url", url.toString());
        //Этот метод проверит существует ли загрузчик
        //Если загрузчик существует,то он его просто перезапустит
        //А если нет, то вызовет метод initLoader()
        loaderManager.restartLoader(LOADER_ID,bundle,this);
    }

    @NonNull
    @Override
    public Loader<JSONObject> onCreateLoader(int id, @Nullable Bundle bundle) {
        NetworkUtils.JSONLoader jsonLoader = new NetworkUtils.JSONLoader(this, bundle);
        jsonLoader.setOnStartLoadingListener(new NetworkUtils.JSONLoader.onStartLoadingListener() {
            @Override
            public void onStartLoading() {
                progressBar.setVisibility(View.VISIBLE);
                isLoading = true;
            }
        });
        return jsonLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<JSONObject> loader, JSONObject jsonObject) {
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);
        if (movies != null && !movies.isEmpty()) {
            if(page == 1){
                mainViewModel.deleteAllMovies();
                adapter.clear();
            }


            for (Movie movie : movies) {
                mainViewModel.insertMovie(movie);
            }
            adapter.addMovies(movies);
            page++;
        }
        isLoading = false;
        progressBar.setVisibility(View.INVISIBLE);
        loaderManager.destroyLoader(LOADER_ID);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<JSONObject> loader) {

    }
}