package com.tbohne.sqlite.dynamic;

import com.tbohne.sqlite.annotations.enums.Collation;
import com.tbohne.sqlite.annotations.enums.KeyOrder;
import com.tbohne.sqlite.annotations.enums.NullOrder;

public class SelectOrderBy {
	private final String sql;

	public SelectOrderBy(String expression, KeyOrder order) {
		StringBuilder sb = new StringBuilder();
		sb.append(expression);
		if (order != KeyOrder.UNSPECIFIED) {
			sb.append(' ').append(order.name());
		}
		sql = sb.toString();
	}

	public SelectOrderBy(String expression, Collation collation, KeyOrder order, NullOrder nullOrder) {
		StringBuilder sb = new StringBuilder();
		sb.append(expression);
		if (collation != Collation.UNSPECIFIED) {
			sb.append(' ').append(order.name());
		}
		if (order != KeyOrder.UNSPECIFIED) {
			sb.append(' ').append(order.name());
		}
		if (nullOrder != NullOrder.UNSPECIFIED) {
			sb.append(' ').append(nullOrder.sql);
		}
		sql = sb.toString();
	}

	public String buildSql() {
		return sql;
	}
}
