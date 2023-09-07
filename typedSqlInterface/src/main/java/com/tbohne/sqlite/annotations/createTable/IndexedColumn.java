package com.tbohne.sqlite.annotations.createTable;

import com.tbohne.sqlite.annotations.enums.Collation;
import com.tbohne.sqlite.annotations.enums.KeyOrder;

import java.lang.annotation.Target;

@Target({})
public @interface IndexedColumn {
	String value(); //sql names

	Collation collation() default Collation.UNSPECIFIED;

	KeyOrder order() default KeyOrder.UNSPECIFIED;
}
