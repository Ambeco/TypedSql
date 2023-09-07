package com.tbohne.sqlite.annotations.enums;

public enum Materialized {
	UNSPECIFIED(""),
	MATERIALIZED("MATERIALIZED"),
	NOT_MATERIALIZED("NOT MATERIALIZED");

	public final String sql;

	Materialized(String sql) {
		this.sql = sql;
	}
}
