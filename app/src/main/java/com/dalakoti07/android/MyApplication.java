package com.dalakoti07.android;

import android.app.Activity;
import android.app.Application;

import com.dalakoti07.android.di.components.ApplicationComponent;
import com.dalakoti07.android.di.components.DaggerApplicationComponent;
import com.dalakoti07.android.di.modules.ContextModule;

public class MyApplication extends Application {
    ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        // setting the application context in contextModule and it would be provided by dagger
        //  is used to build the modules present in the component.
        applicationComponent = DaggerApplicationComponent.builder().contextModule(new ContextModule(this)).build();
        // nothing to be injected here
        applicationComponent.injectApplication(this);
    }

    public static MyApplication get(Activity activity){
        return (MyApplication) activity.getApplication();
    }

    // getApplicationComponent would be used to return the ApplicationComponent in our Activities.
    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
