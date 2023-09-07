package com.tbohne.sqlite.annotations.query;

import java.lang.annotation.Target;

@Target({})
public @interface QueryLimit {
	String value();

	String offsetExpression() default "";

	String secondExpression() default "";
}
