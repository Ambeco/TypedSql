package com.tbohne.sqlite.binders;

public class LongNonNullBinder
		implements SimpleColumnBinder<Long, Long>
{
	@Override
	public Long fromSql(Long columnValue) {
		return columnValue;
	}

	@Override
	public Long toSql(Long javaValue) {
		return javaValue;
	}
}
