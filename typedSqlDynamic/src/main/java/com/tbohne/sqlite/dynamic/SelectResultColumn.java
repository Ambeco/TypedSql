package com.tbohne.sqlite.dynamic;

public class SelectResultColumn {
	private final String sql;

	private SelectResultColumn(String sql) {
		this.sql = sql;
	}

	public static SelectResultColumn expression(String expression)
	{
		return new SelectResultColumn(expression);
	}

	public static SelectResultColumn expression(String expression, String alias)
	{
		return new SelectResultColumn(expression + " AS " + alias);
	}

	public static SelectResultColumn all()
	{
		return new SelectResultColumn("*");
	}

	public static SelectResultColumn allFromTable(String table)
	{
		return new SelectResultColumn(table + ".*");
	}

	public String buildSql() {
		return sql;
	}
}
