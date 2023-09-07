package com.tbohne.sqlite.annotations.query;

import java.lang.annotation.Target;

/**
 * A selection of columns.
 * <p>
 * You should either set projection to an element that contains a @CreateProjection annotation.
 * or fill expressions. You may not set both.
 */
@Target({})
public @interface SelectColumns {
	Class<?> projection(); //should be a {@link com.tbohne.sqlite.annotations.CreateProjection}

	SelectExpression[] expressions() default {};
}
