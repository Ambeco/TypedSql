package com.tbohne.sqlite.sample.datatypes;

import com.tbohne.sqlite.binders.SimpleColumnBinder;

public class MoneyBinder
		implements SimpleColumnBinder<Money, Long>
{
	@Override
	public Money fromSql(Long columnValue) {
		Money m = new Money();
		m.value = columnValue;
		return m;
	}

	@Override
	public Long toSql(Money javaValue) {
		return javaValue.value;
	}
}
