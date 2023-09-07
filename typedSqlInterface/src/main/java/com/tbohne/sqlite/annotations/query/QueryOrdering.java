package com.tbohne.sqlite.annotations.query;

import com.tbohne.sqlite.annotations.enums.Collation;
import com.tbohne.sqlite.annotations.enums.KeyOrder;
import com.tbohne.sqlite.annotations.enums.NullOrder;

import java.lang.annotation.Target;

@Target({})
public @interface QueryOrdering {
	String value();

	Collation collation() default Collation.UNSPECIFIED;

	KeyOrder order() default KeyOrder.UNSPECIFIED;

	NullOrder nullOrder() default NullOrder.UNSPECIFIED;
}
