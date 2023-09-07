package com.tbohne.sqlite.binders;

public class DoubleNonNullBinder
		implements SimpleColumnBinder<Double, Double>
{
	@Override
	public Double fromSql(Double columnValue) {
		return columnValue;
	}

	@Override
	public Double toSql(Double javaValue) {
		return javaValue;
	}
}
