package com.tbohne.sqlite.annotations.createTable;

import com.tbohne.sqlite.annotations.enums.ConflictAction;
import com.tbohne.sqlite.annotations.enums.KeyOrder;

import java.lang.annotation.Target;

@Target({})
public @interface ColumnPrimaryKey {
	String constraintName() default "";

	boolean value() default true;

	KeyOrder order() default KeyOrder.UNSPECIFIED;

	ConflictAction onConflict() default ConflictAction.UNSPECIFIED;

	boolean autoIncrement() default false;
}
