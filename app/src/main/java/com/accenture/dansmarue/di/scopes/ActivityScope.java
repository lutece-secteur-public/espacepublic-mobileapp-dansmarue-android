package com.accenture.dansmarue.di.scopes;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by PK on 22/03/2017.
 * Custom scope for the bi-dependencies between views and presenters
 */
@Documented
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ActivityScope {

}
