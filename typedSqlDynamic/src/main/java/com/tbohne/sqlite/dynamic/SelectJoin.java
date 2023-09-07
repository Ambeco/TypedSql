package com.tbohne.sqlite.dynamic;

import com.tbohne.sqlite.annotations.enums.JoinOperator;

public class SelectJoin {
	private final String sql;

	private SelectJoin(String sql) {
		this.sql = sql;
	}

	public static SelectJoin joinOnExpression(JoinOperator operator, SelectTable table, String expression) {
		StringBuilder sb = new StringBuilder();
		sb.append(operator.sql).append(' ').append(table.buildSql()).append(" ON ").append(expression);
		return new SelectJoin(sb.toString());
	}

	public static SelectJoin joinUsingColumns(JoinOperator operator, SelectTable table, String[] columns) {
		if (columns.length == 0) {
			throw new IllegalArgumentException("no columns passed in");
		}
		StringBuilder sb = new StringBuilder();
		sb.append(operator.sql).append(' ').append(table.buildSql()).append(" USING(");
		for (int i = 0; i < columns.length; i++) {
			sb.append(i > 0 ? ", " : " ").append(columns[i]);
		}
		sb.append(')');
		return new SelectJoin(sb.toString());
	}

	String buildSql() {
		return sql;
	}
}
