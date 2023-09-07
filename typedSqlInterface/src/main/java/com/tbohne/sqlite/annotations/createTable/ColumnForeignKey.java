package com.tbohne.sqlite.annotations.createTable;

import com.tbohne.sqlite.annotations.enums.ForeignKeyAction;
import com.tbohne.sqlite.annotations.enums.ForeignKeyDeferMode;

import java.lang.annotation.Target;

@Target({})
public @interface ColumnForeignKey {
	String constraintName() default "";

	Class<?> value(); //Set to Void.class when using tableIfNotAutogen

	String tableIfNotAutogen() default "";

	String column() default ""; //defaults to the table's primary key

	ForeignKeyAction onDelete() default ForeignKeyAction.UNSPECIFIED;

	ForeignKeyAction onUpdate() default ForeignKeyAction.UNSPECIFIED;

	ForeignKeyDeferMode deferred() default ForeignKeyDeferMode.UNSPECIFIED;
}
