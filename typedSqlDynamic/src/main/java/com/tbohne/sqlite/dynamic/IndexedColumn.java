package com.tbohne.sqlite.dynamic;

import com.tbohne.sqlite.annotations.enums.Collation;
import com.tbohne.sqlite.annotations.enums.KeyOrder;

public class IndexedColumn {
	private final String sql;

	public IndexedColumn(String columnOrExpression, Collation collation, KeyOrder keyOrder) {
		StringBuilder sb = new StringBuilder();
		sb.append(columnOrExpression);
		if (collation != Collation.UNSPECIFIED) {
			sb.append(" COLLATE ").append(collation.name());
		}
		if (keyOrder != KeyOrder.UNSPECIFIED) {
			sb.append(keyOrder.name());
		}
		sql = sb.toString();
	}

	String buildSql() {
		return sql;
	}
}
