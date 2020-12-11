package com.dalakoti07.android.di.scope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

@Scope
@Retention(RetentionPolicy.CLASS)
public @interface ActivityScope {
    //singleton is an inbuilt scope which we can use
}
