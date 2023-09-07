package com.tbohne.sqlite.binders;

import org.checkerframework.checker.nullness.qual.Nullable;

public class StringBinder
		implements SimpleColumnBinder<@Nullable String, @Nullable String>
{
	@Override
	public @Nullable String fromSql(@Nullable String columnValue) {
		return columnValue;
	}

	@Override
	public @Nullable String toSql(@Nullable String javaValue) {
		return javaValue;
	}
}
