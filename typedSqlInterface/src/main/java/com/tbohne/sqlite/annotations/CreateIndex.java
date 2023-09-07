package com.tbohne.sqlite.annotations;

import com.tbohne.sqlite.annotations.createTable.IndexedColumn;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 *This annotation must be on an interface member of a class with @CreateTable on it
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface CreateIndex {
	boolean unique() default false;

	String name() default ""; //defaults to the name of the annotated interface

	String sqlName() default ""; //defaults to name()

	IndexedColumn[] value();

	String where() default "";
}
