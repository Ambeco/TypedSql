package com.tbohne.sqlite.binders;

import org.checkerframework.checker.nullness.qual.Nullable;

public class LongBinder
		implements SimpleColumnBinder<@Nullable Long, @Nullable Long>
{
	@Override
	public @Nullable Long fromSql(@Nullable Long columnValue) {
		return columnValue;
	}

	@Override
	public @Nullable Long toSql(@Nullable Long javaValue) {
		return javaValue;
	}
}
