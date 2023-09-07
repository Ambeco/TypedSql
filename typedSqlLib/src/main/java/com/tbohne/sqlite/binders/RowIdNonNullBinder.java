package com.tbohne.sqlite.binders;

import com.tbohne.sqlite.RowId;

public class RowIdNonNullBinder<T>
		implements SimpleColumnBinder<RowId<T>, Long>
{
	@Override
	public RowId<T> fromSql(Long columnValue) {
		return new RowId<>(columnValue);
	}

	@Override
	public Long toSql(RowId<T> javaValue) {
		return javaValue.getValue();
	}
}
