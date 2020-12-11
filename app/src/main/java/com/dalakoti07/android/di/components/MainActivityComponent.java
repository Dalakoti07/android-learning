package com.dalakoti07.android.di.components;

import android.content.Context;

import com.dalakoti07.android.di.modules.AdapterModule;
import com.dalakoti07.android.di.qualifier.ActivityContext;
import com.dalakoti07.android.di.scope.ActivityScope;
import com.dalakoti07.android.ui.MainActivity;

import dagger.Component;

@ActivityScope
@Component(modules = AdapterModule.class, dependencies = ApplicationComponent.class)
public interface MainActivityComponent {
//    this component is binded with activity's scope thus when a new activity is created then this component is re-created

//The above component would have access to the ApplicationComponent dependencies too.

//    todo - see from where its being provided, it must be from MainActivityContextModule
    @ActivityContext
    Context getContext();

    void injectMainActivity(MainActivity mainActivity);
}
