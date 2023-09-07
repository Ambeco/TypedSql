package com.tbohne.sqlite.annotations.createTable;

import java.lang.annotation.Target;

@Target({})
public @interface ViewColumn {
	String name();

	Class<?> foreignTable(); //Set to Void.class when using expression

	String foreignColumn(); //Set to "" when using expression

	String expression() default "";
}
