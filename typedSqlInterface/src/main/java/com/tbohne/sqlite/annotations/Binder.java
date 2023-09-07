package com.tbohne.sqlite.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * Binders must have this annotation, which is used to generate the JavaColumn classes
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Binder {
}
