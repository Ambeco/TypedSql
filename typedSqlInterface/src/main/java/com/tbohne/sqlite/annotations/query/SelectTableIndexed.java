package com.tbohne.sqlite.annotations.query;

import java.lang.annotation.Target;

@Target({})
public @interface SelectTableIndexed {
	boolean indexSpecified() default true;

	boolean isIndexed() default true;

	String indexedBy();
}
