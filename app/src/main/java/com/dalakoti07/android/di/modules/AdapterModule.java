package com.dalakoti07.android.di.modules;

import com.dalakoti07.android.adapters.RecyclerViewAdapter;
import com.dalakoti07.android.di.scope.ActivityScope;
import com.dalakoti07.android.ui.MainActivity;

import dagger.Module;
import dagger.Provides;

@Module(includes = {MainActivityContextModule.class})
public class AdapterModule {
//    Modules are what would provide the dependencies to the dependents via Components.

//    adapter module is including MainActivityContextModule thus this module can use everything provided by MainActivityContextModule

//    It’s used to create the RecyclerViewAdapter from the POJO data.
//      Also, the ClickListener is an interface defined in the RecyclerViewAdapter class to trigger the click listener callback methods from the Activity itself.
//      It injects the MainActivity dependency since we’ve included the MainActivityContextModule in the definition.

    @Provides
    @ActivityScope
    public RecyclerViewAdapter getStarWarsPeopleLIst(RecyclerViewAdapter.ClickListener clickListener) {
        return new RecyclerViewAdapter(clickListener);
    }

    @Provides
    @ActivityScope
    public RecyclerViewAdapter.ClickListener getClickListener(MainActivity mainActivity) {
        return mainActivity;
    }
}
