package com.dalakoti07.android.di.modules;

import android.content.Context;

import com.dalakoti07.android.di.qualifier.ApplicationContext;
import com.dalakoti07.android.di.scope.ApplicationScope;

import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {
//    Modules are what would provide the dependencies to the dependents via Components.
    private Context context;

    public ContextModule(Context context){
        this.context=context;
    }

//    what this says is this, that we would be providing application content (Application context) whenever need in the app,
//    and scope (Activity scope) says that only one such instance exist
    @Provides
    @ApplicationScope
    @ApplicationContext
    public Context provideContext(){
        return context;
    }
}
