package com.tbohne.sqlite.annotations.enums;

public enum JoinOperator {
	UNSPECIFIED(""),
	LEFT_OUTER("LEFT OUTER"),
	LEFT("LEFT"),
	INNER("INNER"),
	CROSS("CROSS"),
	NATURAL_LEFT_OUTER("NATURAL LEFT OUTER"),
	NATURAL_LEFT("NATURAL LEFT"),
	NATURAL_INNER("NATURAL INNER"),
	NATURAL_CROSS("NATURAL CROSS");

	public final String sql;

	JoinOperator(String sql) {
		this.sql = sql;
	}
}
