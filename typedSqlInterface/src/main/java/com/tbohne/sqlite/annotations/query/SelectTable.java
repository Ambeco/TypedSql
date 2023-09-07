package com.tbohne.sqlite.annotations.query;

import java.lang.annotation.Target;

@Target({})
public @interface SelectTable {
	Class<?> value(); //should be a @CreateTable, @CreateIndex, @CreateView, or @Selection. OR should be Void.class if using fromTableByName.

	String fromTableByName() default "";

	String asAlias() default "";

	SelectTableIndexed indexedBy() default @SelectTableIndexed(indexSpecified = false, indexedBy = "");
}
