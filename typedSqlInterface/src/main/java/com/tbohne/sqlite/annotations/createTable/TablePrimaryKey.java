package com.tbohne.sqlite.annotations.createTable;

import com.tbohne.sqlite.annotations.enums.ConflictAction;

import java.lang.annotation.Target;

@Target({})
public @interface TablePrimaryKey {
	String constraintName() default "";

	IndexedColumn[] value();

	ConflictAction onConflict() default ConflictAction.UNSPECIFIED;
}
