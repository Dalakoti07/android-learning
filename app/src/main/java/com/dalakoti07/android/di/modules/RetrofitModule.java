package com.dalakoti07.android.di.modules;

import android.util.Log;

import com.dalakoti07.android.di.scope.ApplicationScope;
import com.dalakoti07.android.retrofit.APIInterface;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class RetrofitModule {
    //    Modules are what would provide the dependencies to the dependents via Components.
    private static final String TAG = "RetrofitModule";
//    if u dont use dagger than here code would be present which shows typical singleton pattern
//    but since we are using dagger, we can also tell dagger to create singleton pattern for us
//    how?
//    @provides expose the methods
//    @applicationScope ensures that the instance created here are not created more than once
//    we could have used singleton, but we have created our own scopes here application and activity scope


//    provides api client which would be used by activities and fragments
    @Provides
    @ApplicationScope
    APIInterface getApiInterface(Retrofit retroFit) {
        return retroFit.create(APIInterface.class);
    }

//    provide retrofit instance
    @Provides
    @ApplicationScope
    Retrofit getRetrofit(OkHttpClient okHttpClient) {
        Log.d(TAG, "creating retrofit instance");
        return new Retrofit.Builder()
                .baseUrl("https://swapi.dev/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

//    provide okHttp client
    @Provides
    @ApplicationScope
    OkHttpClient getOkHttpCleint(HttpLoggingInterceptor httpLoggingInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build();
    }

//    provide logging interceptor
    @Provides
    @ApplicationScope
    HttpLoggingInterceptor getHttpLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return httpLoggingInterceptor;
    }
}
