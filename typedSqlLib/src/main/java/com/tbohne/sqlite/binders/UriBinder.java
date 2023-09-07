package com.tbohne.sqlite.binders;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.net.URI;

public class UriBinder
		implements SimpleColumnBinder<@Nullable URI, @Nullable String>
{
	@Override
	public @Nullable URI fromSql(@Nullable String columnValue) {
		if (columnValue == null) {
			return null;
		}
		return URI.create(columnValue);
	}

	@Override
	public @Nullable String toSql(@Nullable URI javaValue) {
		if (javaValue == null) {
			return null;
		}
		return javaValue.toString();
	}
}
