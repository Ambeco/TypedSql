package com.tbohne.sqlite.annotations.createTable;

import com.tbohne.sqlite.binders.AffinityBinder;

import java.lang.annotation.Target;

@Target({})
public @interface ColumnBinder {
	// Binder class must have a public `SqlType binder#toSql(JavaType)` method.
	// Binder class must have a public `JavaType #fromSql(SqlType)` method.
	// SqlTypes must be marked @Nullable, unless the column is @NonNull.
	// Code is smaller/simpler if the methods are static.
	Class<?> value() default AffinityBinder.class; //defaults to affinity

	Class<?>[] binderGenerics() default {};

	String javaName() default ""; //defaults to parameter name of binder#toSql

	int parameterIndex() default 0;
}
