package de.jokergames.jfql.jvl.controller;

import de.jokergames.jfql.jvl.util.Method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Janick
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)

public @interface ControllerDeclarer {

    Method method() default Method.GET;

    String path();

}
