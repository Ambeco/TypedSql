package com.tbohne.sqlite.annotations.enums;

public enum ForeignKeyAction {
	UNSPECIFIED(""),
	NO_ACTION("NO ACTION"),
	RESTRICT("RESTRICT"),
	SET_NULL("SET NULL"),
	SET_DEFAULT("SET DEFAULT"),
	CASCADE("CASCADE");

	public final String sql;

	ForeignKeyAction(String sql) {
		this.sql = sql;
	}
}
