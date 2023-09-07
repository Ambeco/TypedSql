package com.tbohne.sqlite.annotations.query;

import com.tbohne.sqlite.annotations.enums.JoinOperator;

import java.lang.annotation.Target;

@Target({})
public @interface SelectJoin {
	JoinOperator operator();

	SelectTable fromTable();

	//if you do not specify using or onExpression, and the first table has exactly one foreign column reference to this
	// table, TypedSQL will automatically join on that column.
	String[] usingColumn() default {};

	String onExpression() default "";
}
