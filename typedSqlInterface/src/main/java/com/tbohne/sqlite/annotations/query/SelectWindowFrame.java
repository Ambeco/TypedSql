package com.tbohne.sqlite.annotations.query;

import java.lang.annotation.Target;

@Target({})
public @interface SelectWindowFrame {
	// https://www.sqlite.org/lang_select.html
	String framespec() default "";
}
