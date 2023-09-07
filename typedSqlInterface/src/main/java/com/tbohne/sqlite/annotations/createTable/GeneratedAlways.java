package com.tbohne.sqlite.annotations.createTable;

import com.tbohne.sqlite.annotations.enums.GeneratedAlwaysAsStorage;

public @interface GeneratedAlways {
	String value();

	GeneratedAlwaysAsStorage storage() default GeneratedAlwaysAsStorage.UNSPECIFIED;
}
