package com.tbohne.sqlite.annotations.query;

import com.tbohne.sqlite.annotations.enums.Affinity;
import com.tbohne.sqlite.binders.AffinityBinder;
import com.tbohne.sqlite.binders.SimpleColumnBinder;

import java.lang.annotation.Target;

/**
 * Adds an arbitrary sql expression to a Projection
 **/
@Target({})
public @interface ProjectionExpression {
	String name();

	String sqlName() default ""; //defaults to name

	Affinity affinity();

	boolean notNull() default false;

	boolean unique() default false;

	Class<? extends SimpleColumnBinder> binder() default AffinityBinder.class;

	Class<?>[] binderGenerics() default {};
}
