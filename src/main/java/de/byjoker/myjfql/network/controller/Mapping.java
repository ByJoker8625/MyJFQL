package de.byjoker.myjfql.network.controller;

import de.byjoker.myjfql.network.util.AccessLevel;
import de.byjoker.myjfql.network.util.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapping {

    String path();

    RequestMethod method();

    AccessLevel access() default AccessLevel.SESSION_AND_NO_SESSION;

}
