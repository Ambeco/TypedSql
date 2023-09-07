package com.tbohne.sqlite.binders;

public class BooleanNonNullBinder
		implements SimpleColumnBinder<Boolean, Long>
{
	@Override
	public Boolean fromSql(Long columnValue) {
		if (columnValue == 0) {
			return false;
		}
		if (columnValue == 1) {
			return true;
		}
		throw new IllegalArgumentException(columnValue + " is out of range of Boolean");
	}

	@Override
	public Long toSql(Boolean javaValue) {
		return javaValue ? 1L : 0L;
	}
}
