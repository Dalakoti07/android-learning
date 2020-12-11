package com.dalakoti07.android.di.modules;

import android.content.Context;

import com.dalakoti07.android.di.qualifier.ActivityContext;
import com.dalakoti07.android.di.scope.ActivityScope;
import com.dalakoti07.android.ui.MainActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class MainActivityContextModule {
    //    Modules are what would provide the dependencies to the dependents via Components.

    private MainActivity mainActivity;

    public Context context;

    public MainActivityContextModule(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        context = mainActivity;
    }

//    this provide mainActivity whenever needed in the application, when its needed by adapter
    @Provides
    @ActivityScope
    public MainActivity providesMainActivity() {
        return mainActivity;
    }

//    this provide mainActivity context whenever needed in the application
    @Provides
    @ActivityScope
    @ActivityContext
    public Context provideContext() {
        return context;
    }
}
