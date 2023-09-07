package com.tbohne.sqlite.annotations;

import com.tbohne.sqlite.annotations.createTable.ViewColumn;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * Describes the schema of a table.
 *
 * This goes on a class or interface that describes the table at a given schema level.
 *
 * typedSqlProcessor will generate a class that contains members for creating and using a sqlite
 * table. The generated class will have the same name as the class with this annotation, plus an
 * "Sql" suffix. For ease of use, one can make the annotated class inherit from the generated class
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface CreateView {
	boolean temporary() default false;

	String schemaName() default "";

	String name() default ""; //defaults to the name of the class being annotated

	String sqlName() default ""; //defaults to the name of the class being annotated

	String rowName() default "Row";

	ViewColumn[] columns();
}
