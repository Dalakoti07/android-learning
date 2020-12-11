package com.dalakoti07.android.di.components;

import com.dalakoti07.android.di.scope.ActivityScope;
import com.dalakoti07.android.ui.DetailActivity;

import dagger.Component;

@Component(dependencies = ApplicationComponent.class)
@ActivityScope
public interface DetailActivityComponent {
//    activity scope
    void inject(DetailActivity detailActivity);
}
