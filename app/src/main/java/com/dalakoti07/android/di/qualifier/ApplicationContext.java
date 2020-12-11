package com.dalakoti07.android.di.qualifier;

import javax.inject.Qualifier;

@Qualifier
public @interface ApplicationContext {
    //    qualifier differentiate ensures specification when dependency is provided,
//    like which exact context u want activity or application context, in component
}
