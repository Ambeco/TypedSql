package com.tbohne.sqlite.dynamic;

import com.tbohne.sqlite.annotations.enums.ConflictAction;
import com.tbohne.sqlite.annotations.enums.ForeignKeyAction;

import org.checkerframework.checker.nullness.qual.Nullable;

public class TableConstraint {
	private final String sql;

	private TableConstraint(String sql) {
		this.sql = sql;
	}

	public static TableConstraint primaryKey(@Nullable String name, String[] columns, ConflictAction conflictAction) {
		StringBuilder sb = new StringBuilder();
		if (name != null) {
			sb.append(" CONSTRAINT ").append(name);
		}
		sb.append(" PRIMARY KEY (");
		for (int i = 0; i < columns.length; i++) {
			if (i != 0) {
				sb.append(", ");
			}
			sb.append(columns[i]);
		}
		sb.append(')');
		if (conflictAction != ConflictAction.UNSPECIFIED) {
			sb.append(' ').append(conflictAction.name());
		}
		return new TableConstraint(sb.toString());
	}

	public static TableConstraint unique(@Nullable String name, String[] columns, ConflictAction conflictAction) {
		StringBuilder sb = new StringBuilder();
		if (name != null) {
			sb.append(" CONSTRAINT ").append(name);
		}
		sb.append(" UNIQUE (");
		for (int i = 0; i < columns.length; i++) {
			if (i != 0) {
				sb.append(", ");
			}
			sb.append(columns[i]);
		}
		sb.append(')');
		if (conflictAction != ConflictAction.UNSPECIFIED) {
			sb.append(' ').append(conflictAction.name());
		}
		return new TableConstraint(sb.toString());
	}

	public static TableConstraint check(@Nullable String name, String expression) {
		StringBuilder sb = new StringBuilder();
		if (name != null) {
			sb.append(" CONSTRAINT ").append(name);
		}
		sb.append(" CHECK (").append(expression).append(')');
		return new TableConstraint(sb.toString());
	}

	public static TableConstraint foreignKey(@Nullable String name, String[] columns, ForeignKeyAction clause) {
		StringBuilder sb = new StringBuilder();
		if (name != null) {
			sb.append(" CONSTRAINT ").append(name);
		}
		sb.append(" FOREIGN KEY (");
		for (int i = 0; i < columns.length; i++) {
			if (i != 0) {
				sb.append(", ");
			}
			sb.append(columns[i]);
		}
		sb.append(')');
		if (clause != ForeignKeyAction.UNSPECIFIED) {
			sb.append(' ').append(clause.sql);
		}
		return new TableConstraint(sb.toString());
	}

	String buildSql() {
		return sql;
	}
}
