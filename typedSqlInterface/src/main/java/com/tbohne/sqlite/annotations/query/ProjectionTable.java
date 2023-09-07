package com.tbohne.sqlite.annotations.query;

import java.lang.annotation.Target;

/**
 * Adds all columns in a table or index to a Projection
 **/
@Target({})
public @interface ProjectionTable {
	Class<?> value(); //should be a @CreateTable @CreateView, or @CreateIndex

	String[] columns() default {}; //if empty, or the single column "*", then this brings in ALL columns

	String tableAlias() default ""; //useful when joining
}
