package com.tbohne.sqlite.binders;

import org.checkerframework.checker.nullness.qual.Nullable;

public class IntBinder
		implements SimpleColumnBinder<@Nullable Integer, @Nullable Long>
{
	@Override
	public @Nullable Integer fromSql(@Nullable Long columnValue) {
		if (columnValue == null) {
			return null;
		}
		if (columnValue > Integer.MAX_VALUE || columnValue < Integer.MIN_VALUE) {
			throw new IllegalArgumentException(columnValue + " is out of range of Integer");
		}
		return (int) (long) columnValue;
	}

	@Override
	public @Nullable Long toSql(@Nullable Integer javaValue) {
		if (javaValue == null) {
			return null;
		}
		return Long.valueOf(javaValue);
	}
}
