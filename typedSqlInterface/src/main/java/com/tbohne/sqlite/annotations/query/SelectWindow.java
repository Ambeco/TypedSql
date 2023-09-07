package com.tbohne.sqlite.annotations.query;

import java.lang.annotation.Target;

@Target({})
public @interface SelectWindow {
	String name();

	String baseName() default "";

	String[] partitionExpressions();

	QueryOrdering orderBy() default @QueryOrdering("");

	SelectWindowFrame[] frames() default {};
}
