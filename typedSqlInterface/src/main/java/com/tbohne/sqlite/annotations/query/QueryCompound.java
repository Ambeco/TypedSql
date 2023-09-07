package com.tbohne.sqlite.annotations.query;

import com.tbohne.sqlite.annotations.enums.Compound;

import java.lang.annotation.Target;

@Target({})
public @interface QueryCompound {
	Compound operator();

	Class<?> selection(); //should be a `@Selection`
}
