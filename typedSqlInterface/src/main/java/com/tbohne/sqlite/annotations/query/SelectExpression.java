package com.tbohne.sqlite.annotations.query;

import java.lang.annotation.Target;

@Target({})
public @interface SelectExpression {
	String name();

	String expression();
}
