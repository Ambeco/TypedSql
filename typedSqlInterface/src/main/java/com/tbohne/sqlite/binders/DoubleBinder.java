package com.tbohne.sqlite.binders;

import org.checkerframework.checker.nullness.qual.Nullable;

public class DoubleBinder
		implements SimpleColumnBinder<@Nullable Double, @Nullable Double>
{
	@Override
	public @Nullable Double fromSql(@Nullable Double columnValue) {
		return columnValue;
	}

	@Override
	public @Nullable Double toSql(@Nullable Double javaValue) {
		return javaValue;
	}
}
