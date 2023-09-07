package com.tbohne.sqlite.dynamic;

public class SelectFrameBegin {
	private final String sql;

	private SelectFrameBegin(String sql) {
		this.sql = sql;
	}

	public static SelectFrameBegin unbounded()
	{
		return new SelectFrameBegin("UNBOUNDED PRECEDING");
	}

	public static SelectFrameBegin expressionPreceding(String expression)
	{
		return new SelectFrameBegin(expression + " PRECEDING");
	}

	public static SelectFrameBegin currentRow()
	{
		return new SelectFrameBegin("CURRENT ROW");
	}

	public static SelectFrameBegin expressionFollowing(String expression)
	{
		return new SelectFrameBegin(expression + " FOLLOWING");
	}

	public String buildSql() {
		return sql;
	}
}
