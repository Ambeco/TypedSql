package com.tbohne.sqlite.annotations.createTable;

import java.lang.annotation.Target;

@Target({})
public @interface DefaultValue {
	String constraintName() default "";

	String value();
}
