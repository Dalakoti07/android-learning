package com.dalakoti07.android.di.scope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

@Retention(RetentionPolicy.CLASS)
@Scope
public @interface ApplicationScope {
    //singleton is an inbuilt scope which we can use
}
