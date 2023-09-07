package com.tbohne.sqlite.annotations.enums;

public enum Compound {
	UNSPECIFIED(""),
	UNION("UNION"),
	UNION_ALL("UNION ALL"),
	INTERSECT("INTERSECT"),
	EXCEPT("EXCEPT");

	public final String sql;

	Compound(String sql) {
		this.sql = sql;
	}
}
