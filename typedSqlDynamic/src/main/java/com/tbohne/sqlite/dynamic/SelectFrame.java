package com.tbohne.sqlite.dynamic;

public class SelectFrame {
	private final String sql;

	private SelectFrame(String sql) {
		this.sql = sql;
	}

	public static SelectFrame between(Range range, SelectFrameBegin begin, SelectFrameEnd end, Exclude exclude)
	{
		String sb = range + " BETWEEN " + begin.buildSql() + " AND " + end.buildSql() + exclude.sql;
		return new SelectFrame(sb);
	}

	public static SelectFrame unbounded(Range range, Exclude exclude)
	{
		String sb = range + " UNBOUNDED " + exclude.sql;
		return new SelectFrame(sb);
	}

	public static SelectFrame expressionPreceding(Range range, String expression, Exclude exclude)
	{
		String sb = range + " " + expression + " PRECEDING" + exclude.sql;
		return new SelectFrame(sb);
	}

	public static SelectFrame currentRow(Range range, Exclude exclude)
	{
		String sb = range + " CURRENT ROW " + exclude.sql;
		return new SelectFrame(sb);
	}

	public String buildSql() {
		return sql;
	}

	enum Range {
		RANGE,
		ROWS,
		GROUPS
	}

	enum Exclude {
		UNSPECIFIED(""),
		NO_OTHERS(" EXCLUDE NO OTHERS"),
		CURRENT_ROW(" EXCLUDE CURRENT ROW"),
		GROUP(" EXCLUDE GROUP"),
		TIES(" EXCLUDE TIES");

		public final String sql;

		Exclude(String sql) {
			this.sql = sql;
		}
	}
}
