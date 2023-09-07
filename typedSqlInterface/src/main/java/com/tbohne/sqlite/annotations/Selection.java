package com.tbohne.sqlite.annotations;

import com.tbohne.sqlite.annotations.query.SelectColumns;
import com.tbohne.sqlite.annotations.query.SelectJoin;
import com.tbohne.sqlite.annotations.query.SelectTable;
import com.tbohne.sqlite.annotations.query.SelectWindow;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A Selection is an incomplete Query, for other code to extend it into a full Query elsewhere.
 * <p>
 * This vaguely functions like a View, telling how to fill queried columns from a table. It is most
 * useful when creating a Selection with joined tables or other complexity. Other code can simply
 * add a few where clauses to get the rows from this selection that they care about.
 * <p>
 * Selections without runtime values can be done via this annotation, but many queries will have
 * runtime values and so will use @{link com.tbohne.sqlite.dynamic.Select} instead.
 * <p>
 * See also {@link Query}, which also has has example usage
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Selection {
	String name() default ""; //defaults to the name of the annotated class + "Selection"

	boolean distinct() default false;

	SelectColumns select();

	SelectTable fromTable();

	SelectJoin[] joins() default {};

	String where() default "";

	String[] groupBy() default {};

	String having() default "";

	SelectWindow[] windows() default {};
}
