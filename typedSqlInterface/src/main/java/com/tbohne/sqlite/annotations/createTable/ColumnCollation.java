package com.tbohne.sqlite.annotations.createTable;

import com.tbohne.sqlite.annotations.enums.Collation;

import java.lang.annotation.Target;

@Target({})
public @interface ColumnCollation {
	Collation value();

	String constraintName() default "";
}
