package com.tbohne.sqlite.annotations.createTable;

import java.lang.annotation.Target;

@Target({})
public @interface TableCheck {
	String constraintName() default "";

	String value();
}
