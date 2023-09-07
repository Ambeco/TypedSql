package com.tbohne.sqlite.dynamic;

public class SelectFrameEnd {
	private final String sql;

	private SelectFrameEnd(String sql) {
		this.sql = sql;
	}

	public static SelectFrameEnd expressionPreceding(String expression)
	{
		return new SelectFrameEnd(expression + " PRECEDING");
	}

	public static SelectFrameEnd currentRow()
	{
		return new SelectFrameEnd("CURRENT ROW");
	}

	public static SelectFrameEnd expressionFollowing(String expression)
	{
		return new SelectFrameEnd(expression + " FOLLOWING");
	}

	public static SelectFrameEnd unboundedFollowing()
	{
		return new SelectFrameEnd("UNBOUNDED FOLLOWING");
	}

	public String buildSql() {
		return sql;
	}
}
