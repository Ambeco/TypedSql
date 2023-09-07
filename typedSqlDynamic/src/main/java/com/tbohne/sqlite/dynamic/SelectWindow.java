package com.tbohne.sqlite.dynamic;

import org.checkerframework.checker.nullness.qual.Nullable;

public class SelectWindow {
	private final String sql;

	public SelectWindow(
			@Nullable String windowName,
			String @Nullable [] partitionBy,
			SelectOrderBy[] orderBy,
			@Nullable SelectFrame frame)
	{
		StringBuilder sb = new StringBuilder("(");
		if (windowName != null) {
			sb.append(windowName);
		}
		if (partitionBy != null && partitionBy.length > 0) {
			sb.append(" PARTITION BY ");
			for (int i = 0; i < partitionBy.length; i++) {
				sb.append(i > 0 ? ", " : "").append(partitionBy[i]);
			}
		}
		if (orderBy != null && orderBy.length > 0) {
			sb.append(" ORDER BY ");
			for (int i = 0; i < orderBy.length; i++) {
				sb.append(i > 0 ? ", " : "").append(orderBy[i].buildSql());
			}
		}
		if (frame != null) {
			sb.append(' ').append(frame.buildSql());
		}
		sb.append(')');
		sql = sb.toString();
	}

	public String buildSql() {
		return sql;
	}
}
