package com.dalakoti07.android.di.components;

import android.content.Context;

import com.dalakoti07.android.MyApplication;
import com.dalakoti07.android.di.modules.ContextModule;
import com.dalakoti07.android.di.modules.RetrofitModule;
import com.dalakoti07.android.di.qualifier.ApplicationContext;
import com.dalakoti07.android.di.scope.ApplicationScope;
import com.dalakoti07.android.retrofit.APIInterface;

import dagger.Component;

@ApplicationScope
@Component(modules = {ContextModule.class, RetrofitModule.class})
public interface ApplicationComponent {
    // what is going on?
//    we use modules in components
//    and this component use context and retrofit module
//    now this component would be availabe in application scope, because of applicationScope whose instance is kept in Application class

//    exposing apiClient
    public APIInterface getApiInterface();

//    providing context which is application context as described by annotation
    @ApplicationContext
    public Context getContext();

//    inject dependency in MyApplication
    public void injectApplication(MyApplication myApplication);

//    Dagger2 would autogenerate a class named Dagger%ComponentName%. Eg. DaggerApplicationComponent.
//     injectApplication is used to allow @Inject fields in our Activity/Application.
}
