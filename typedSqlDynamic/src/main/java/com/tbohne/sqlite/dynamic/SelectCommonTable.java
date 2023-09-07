package com.tbohne.sqlite.dynamic;

import com.tbohne.sqlite.annotations.enums.Materialized;

public class SelectCommonTable {
	private final String sql;

	public SelectCommonTable(String tableName, Materialized materialized, SyntaxSelectComplete selectStatement) {
		StringBuilder sb = new StringBuilder();
		sb.append(tableName);
		sb.append("AS ");
		if (materialized != Materialized.UNSPECIFIED) {
			sb.append(materialized.sql);
		}
		sb.append('(').append(selectStatement.buildSql()).append(')');
		sql = sb.toString();
	}

	public SelectCommonTable(
			String tableName, String[] columns, Materialized materialized, SyntaxSelectComplete selectStatement)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(tableName);
		if (columns != null && columns.length > 0) {
			for (int i = 0; i < columns.length; i++) {
				sb.append(i > 0 ? ", " : " (").append(columns[i]);
			}
			sb.append(')');
		}
		sb.append("AS ");
		if (materialized != Materialized.UNSPECIFIED) {
			sb.append(materialized.sql);
		}
		sb.append('(').append(selectStatement.buildSql()).append(')');
		sql = sb.toString();
	}

	public String buildSql() {
		return sql;
	}
}
