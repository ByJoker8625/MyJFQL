package de.jokergames.jfql.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Janick
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)

public @interface EventHandler {

    String type();

}
