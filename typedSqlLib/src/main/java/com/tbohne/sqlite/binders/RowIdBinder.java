package com.tbohne.sqlite.binders;

import com.tbohne.sqlite.RowId;

import org.checkerframework.checker.nullness.qual.Nullable;

public class RowIdBinder<T>
		implements SimpleColumnBinder<@Nullable RowId<T>, @Nullable Long>
{
	@Override
	public @Nullable RowId<T> fromSql(@Nullable Long columnValue) {
		if (columnValue == null) {
			return null;
		}
		return new RowId<>(columnValue);
	}

	@Override
	public @Nullable Long toSql(@Nullable RowId<T> javaValue) {
		if (javaValue == null) {
			return null;
		}
		return javaValue.getValue();
	}
}
