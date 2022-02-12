package de.byjoker.myjfql.network.controller;

import de.byjoker.myjfql.network.util.LoginRequirement;
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

    LoginRequirement login() default LoginRequirement.SESSION_AND_NO_SESSION;

}
