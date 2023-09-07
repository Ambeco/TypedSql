package com.tbohne.sqlite.annotations;

import com.tbohne.sqlite.annotations.query.ProjectionExpression;
import com.tbohne.sqlite.annotations.query.ProjectionTable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Projections primarily generates type safe cursors
 * <p>
 * These also function as an interface for a Query or Selection, so users don't have to depend on
 * the implementation details of how that Projection is populated.
 * <p>
 * Projections are used by {@link Selection}s.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface CreateProjection {
	String projectionName() default ""; //defaults to the name of the annotated class + "Projection"

	String rowName() default ""; //defaults to the name of the annotated class + "Row"

	String cursorName() default ""; //defaults to the name of the annotated class + "Cursor"

	String rawCursorName() default ""; //defaults to the name of the annotated class + "RawSqlCursor"

	ProjectionTable[] tables() default {};

	ProjectionExpression[] expressions() default {};
}
