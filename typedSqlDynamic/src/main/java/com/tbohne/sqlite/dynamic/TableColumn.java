package com.tbohne.sqlite.dynamic;

import com.tbohne.sqlite.annotations.enums.Affinity;
import com.tbohne.sqlite.annotations.enums.AsStorage;
import com.tbohne.sqlite.annotations.enums.Collation;
import com.tbohne.sqlite.annotations.enums.ConflictAction;
import com.tbohne.sqlite.annotations.enums.ForeignKeyAction;
import com.tbohne.sqlite.annotations.enums.ForeignKeyDeferMode;
import com.tbohne.sqlite.annotations.enums.KeyOrder;

import org.checkerframework.checker.nullness.qual.Nullable;

public class TableColumn {
	private final StringBuilder sb = new StringBuilder();

	public TableColumn(String name, Affinity affinity) {
		sb.append(name).append(' ').append(affinity.name());
	}

	public TableColumn primaryKey(
			@Nullable String name, KeyOrder keyOrder, ConflictAction conflictAction, boolean autoIncrement)
	{
		if (keyOrder == KeyOrder.UNSPECIFIED) {
			throw new IllegalArgumentException("keyOrder is undefined");
		}
		if (conflictAction == ConflictAction.UNSPECIFIED) {
			throw new IllegalArgumentException("conflict is undefined");
		}
		if (name != null) {
			sb.append("CONSTRAINT ").append(name).append(' ');
		}
		sb.append("PRIMARY KEY ").append(keyOrder.name()).append(' ').append(conflictAction.name());
		if (autoIncrement) {
			sb.append(" AUTOINCREMENT");
		}
		return this;
	}

	public TableColumn notNull(@Nullable String name, ConflictAction conflictAction) {
		if (conflictAction == ConflictAction.UNSPECIFIED) {
			throw new IllegalArgumentException("conflict is undefined");
		}
		if (name != null) {
			sb.append("CONSTRAINT ").append(name).append(' ');
		}
		sb.append("NOT NULL ").append(conflictAction.name());
		return this;
	}

	public TableColumn unique(@Nullable String name, ConflictAction conflictAction) {
		if (conflictAction == ConflictAction.UNSPECIFIED) {
			throw new IllegalArgumentException("conflict is undefined");
		}
		if (name != null) {
			sb.append("CONSTRAINT ").append(name).append(' ');
		}
		sb.append("UNIQUE ").append(conflictAction.name());
		return this;
	}

	public TableColumn defaultString(@Nullable String name, String defaultValue) {
		if (name != null) {
			sb.append("CONSTRAINT ").append(name).append(' ');
		}
		sb.append("DEFAULT '").append(defaultValue).append('\'');
		return this;
	}

	public TableColumn defaultLong(@Nullable String name, Long defaultValue) {
		if (name != null) {
			sb.append("CONSTRAINT ").append(name).append(' ');
		}
		sb.append("DEFAULT ").append(defaultValue);
		return this;
	}

	public TableColumn defaultDouble(@Nullable String name, Double defaultValue) {
		if (name != null) {
			sb.append("CONSTRAINT ").append(name).append(' ');
		}
		sb.append("DEFAULT ").append(defaultValue);
		return this;
	}

	public TableColumn defaultExpresion(@Nullable String name, String defaultExpression) {
		if (name != null) {
			sb.append("CONSTRAINT ").append(name).append(' ');
		}
		sb.append("DEFAULT (").append(defaultExpression).append(')');
		return this;
	}

	public TableColumn collate(@Nullable String name, Collation collation) {
		if (collation == Collation.UNSPECIFIED) {
			throw new IllegalArgumentException("collation is undefined");
		}
		if (name != null) {
			sb.append("CONSTRAINT ").append(name).append(' ');
		}
		sb.append("COLLATE ").append(collation.name());
		return this;
	}

	public TableColumn references(
			@Nullable String name,
			String foreignTable,
			String @Nullable [] columns,
			ForeignKeyAction onDelete,
			ForeignKeyAction onUpdate,
			@Nullable String match,
			ForeignKeyDeferMode deferMode)
	{
		if (name != null) {
			sb.append("CONSTRAINT ").append(name).append(' ');
		}
		sb.append("REFERENCES ").append(foreignTable);
		if (columns != null) {
			sb.append('(');
			for (int i = 0; i < columns.length; i++) {
				if (i != 0) {
					sb.append(", ");
				}
				sb.append(columns[i]);
			}
			sb.append(')');
		}
		if (onDelete != ForeignKeyAction.UNSPECIFIED) {
			sb.append(" ON DELETE ").append(onDelete.sql);
		}
		if (onUpdate != ForeignKeyAction.UNSPECIFIED) {
			sb.append(" ON UPDATE ").append(onDelete.sql);
		}
		if (match != null) {
			sb.append(" MATCH ").append(name);
		}
		if (deferMode != ForeignKeyDeferMode.UNSPECIFIED) {
			sb.append(deferMode.sql);
		}
		return this;
	}

	public TableColumn generatedAs(
			@Nullable String name, boolean generateAlways, String expression, AsStorage asStorage)
	{
		if (name != null) {
			sb.append("CONSTRAINT ").append(name).append(' ');
		}
		if (generateAlways) {
			sb.append("GENERATED ALWAYS ");
		}
		sb.append("AS (").append(expression).append(')');
		if (asStorage != AsStorage.UNSPECIFIED) {
			sb.append(' ').append(asStorage.name());
		}
		return this;
	}

	public String buildSql() {
		return sb.toString();
	}

}
