package com.dalakoti07.android.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.dalakoti07.android.MyApplication;
import com.dalakoti07.android.R;
import com.dalakoti07.android.adapters.RecyclerViewAdapter;
import com.dalakoti07.android.di.components.ApplicationComponent;
import com.dalakoti07.android.di.components.DaggerMainActivityComponent;
import com.dalakoti07.android.di.components.MainActivityComponent;
import com.dalakoti07.android.di.modules.MainActivityContextModule;
import com.dalakoti07.android.di.qualifier.ActivityContext;
import com.dalakoti07.android.di.qualifier.ApplicationContext;
import com.dalakoti07.android.pojo.StarWars;
import com.dalakoti07.android.retrofit.APIInterface;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.ClickListener {
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    MainActivityComponent mainActivityComponent;

    @Inject
    public RecyclerViewAdapter recyclerViewAdapter;

    @Inject
    public APIInterface apiInterface;

    @Inject
    @ApplicationContext
    public Context mContext;

    @Inject
    @ActivityContext
    public Context activityContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        ApplicationComponent applicationComponent = MyApplication.get(this).getApplicationComponent();
        mainActivityComponent = DaggerMainActivityComponent.builder()
                .mainActivityContextModule(new MainActivityContextModule(this))
                .applicationComponent(applicationComponent)
                .build();

        mainActivityComponent.injectMainActivity(this);
        recyclerView.setAdapter(recyclerViewAdapter);

        apiInterface.getPeople("json").enqueue(new Callback<StarWars>() {
            @Override
            public void onResponse(Call<StarWars> call, Response<StarWars> response) {
                populateRecyclerView(response.body().results);
                if(!response.isSuccessful()){
                    Log.d(TAG, "onResponse: unsucess"+response.code()+" errir "+response.message());
                }
            }

            @Override
            public void onFailure(Call<StarWars> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getLocalizedMessage());
            }
        });
    }

    private void populateRecyclerView(List<StarWars.People> response) {
        recyclerViewAdapter.setData(response);
    }


    @Override
    public void launchIntent(String url) {
        Toast.makeText(mContext, "RecyclerView Row selected", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(activityContext, DetailActivity.class).putExtra("url", url));
    }
}