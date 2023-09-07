package com.tbohne.sqlite.dynamic;

import org.checkerframework.checker.nullness.qual.Nullable;

public class SelectTable {
	private final String sql;

	private SelectTable(String sql) {
		this.sql = sql;
	}

	public static SelectTable table(String table) {
		return new SelectTable(table);
	}

	public static SelectTable table(String table, String alias) {
		return new SelectTable(table + " AS " + alias);
	}

	public static SelectTable table(String table, @Nullable String alias, @Nullable String index) {
		return table(null, table, alias, index);
	}

	public static SelectTable table(
			@Nullable String schema, String table, @Nullable String alias, @Nullable String index)
	{
		StringBuilder sb = new StringBuilder();
		if (schema != null) {
			sb.append(schema).append('.');
		}
		sb.append(table);
		if (alias != null) {
			sb.append(" AS ").append(alias);
		}
		if (index != null) {
			sb.append(" INDEXED BY ").append(index);
		} else {
			sb.append(" NOT INDEXED");
		}
		return new SelectTable(sb.toString());
	}

	public static SelectTable tableFunction(
			@Nullable String schema, String tableFunction, String[] expressions, @Nullable String alias)
	{
		if (expressions.length == 0) {
			throw new IllegalArgumentException("no expressions passed in");
		}
		StringBuilder sb = new StringBuilder();
		if (schema != null) {
			sb.append(schema).append('.');
		}
		sb.append(tableFunction);
		for (int i = 0; i < expressions.length; i++) {
			sb.append(i > 0 ? ", " : "(").append(expressions[i]);
		}
		sb.append(')');
		if (alias != null) {
			sb.append(" AS ").append(alias);
		}
		return new SelectTable(sb.toString());
	}

	public static SelectTable select(StrictSelect selectStatement, @Nullable String alias) {
		StringBuilder sb = new StringBuilder();
		sb.append('(').append(selectStatement.buildSql()).append(')');
		if (alias != null) {
			sb.append(" AS ").append(alias);
		}
		return new SelectTable(sb.toString());
	}

	public static SelectTable tables(SelectTable[] tables) {
		if (tables.length == 0) {
			throw new IllegalArgumentException("no tables passed in");
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < tables.length; i++) {
			sb.append(i > 0 ? ", " : "(").append(tables[i]);
		}
		sb.append(')');
		return new SelectTable(sb.toString());
	}

	public static SelectTable join(SelectJoin join) {
		return new SelectTable('(' + join.buildSql() + ')');
	}

	public String buildSql() {
		return sql;
	}
}
