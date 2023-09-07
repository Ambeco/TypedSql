package com.tbohne.sqlite.binders;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.UUID;

public class UuidStringBinder
		implements SimpleColumnBinder<@Nullable UUID, @Nullable String>
{
	@Override
	public @Nullable UUID fromSql(@Nullable String columnValue) {
		if (columnValue == null) {
			return null;
		}
		return UUID.fromString(columnValue);
	}

	@Override
	public @Nullable String toSql(@Nullable UUID javaValue) {
		if (javaValue == null) {
			return null;
		}
		return javaValue.toString();
	}
}
