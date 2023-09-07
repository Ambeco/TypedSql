package com.tbohne.sqlite.binders;

public class StringNonNullBinder
		implements SimpleColumnBinder<String, String>
{
	@Override
	public String fromSql(String columnValue) {
		return columnValue;
	}

	@Override
	public String toSql(String javaValue) {
		return javaValue;
	}
}
