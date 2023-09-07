package com.tbohne.sqlite.binders;

import org.checkerframework.checker.nullness.qual.Nullable;

public class BooleanBinder
		implements SimpleColumnBinder<@Nullable Boolean, @Nullable Long>
{
	@Override
	public @Nullable Boolean fromSql(@Nullable Long columnValue) {
		if (columnValue == null) {
			return null;
		}
		if (columnValue == 0) {
			return false;
		}
		if (columnValue == 1) {
			return true;
		}
		throw new IllegalArgumentException(columnValue + " is out of range of Boolean");
	}

	@Override
	public @Nullable Long toSql(@Nullable Boolean javaValue) {
		if (javaValue == null) {
			return null;
		}
		return javaValue ? 1L : 0L;
	}
}
