package com.tbohne.sqlite.annotations.createTable;

import java.lang.annotation.Target;

@Target({})
public @interface ColumnCheck {
	String value();

	String constraintName() default "";
}
