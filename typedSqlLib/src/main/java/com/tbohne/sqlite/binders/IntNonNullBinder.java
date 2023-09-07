package com.tbohne.sqlite.binders;

public class IntNonNullBinder
		implements SimpleColumnBinder<Integer, Long>
{
	@Override
	public Integer fromSql(Long columnValue) {
		if (columnValue > Integer.MAX_VALUE || columnValue < Integer.MIN_VALUE) {
			throw new IllegalArgumentException(columnValue + " is out of range of Integer");
		}
		return (int) (long) columnValue;
	}

	@Override
	public Long toSql(Integer javaValue) {
		return Long.valueOf(javaValue);
	}
}
