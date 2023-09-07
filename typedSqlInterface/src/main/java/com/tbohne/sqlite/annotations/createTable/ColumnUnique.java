package com.tbohne.sqlite.annotations.createTable;

import com.tbohne.sqlite.annotations.enums.ConflictAction;

import java.lang.annotation.Target;

@Target({})
public @interface ColumnUnique {
	boolean value() default true;

	String constraintName() default "";

	ConflictAction onConflict() default ConflictAction.UNSPECIFIED;
}
