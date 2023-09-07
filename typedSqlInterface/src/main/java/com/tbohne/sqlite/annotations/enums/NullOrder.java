package com.tbohne.sqlite.annotations.enums;

public enum NullOrder {
	UNSPECIFIED(""),
	NULLS_FIRST("NULLS FIRST"),
	NULLS_LAST("NULLS LAST");

	public final String sql;

	NullOrder(String sql) {
		this.sql = sql;
	}
}
