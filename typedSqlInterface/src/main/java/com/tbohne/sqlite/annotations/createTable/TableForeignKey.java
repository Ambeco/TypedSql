package com.tbohne.sqlite.annotations.createTable;

import com.tbohne.sqlite.annotations.enums.ForeignKeyAction;
import com.tbohne.sqlite.annotations.enums.ForeignKeyDeferMode;

import java.lang.annotation.Target;

@Target({})
public @interface TableForeignKey {
	String constraintName() default "";

	String[] columns(); //sql names

	Class<?> foreignTable(); //Set to Void.class when using tableIfNotAutogen

	String tableIfNotAutogen() default "";

	String[] foreignColumns() default {}; //sql names

	ForeignKeyAction onDelete() default ForeignKeyAction.UNSPECIFIED;

	ForeignKeyAction onUpdate() default ForeignKeyAction.UNSPECIFIED;

	ForeignKeyDeferMode deferred() default ForeignKeyDeferMode.UNSPECIFIED;
}
