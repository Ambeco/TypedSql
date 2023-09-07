package com.tbohne.sqlite.binders;

public class EnumNonNullBinder<TEnum extends Enum<TEnum>>
		implements SimpleColumnBinder<TEnum, Long>
{
	private final String name;
	private final TEnum[] values;

	//new EnumBinder<MyEnum>("MyEnum", MyEnum.values()));
	public EnumNonNullBinder(String name, TEnum[] values) {
		this.name = name;
		this.values = values;
	}

	@Override
	public TEnum fromSql(Long columnValue) {
		if (columnValue < 0) {
			throw new IllegalArgumentException(columnValue + " is out of range of " + name);
		}
		if (columnValue >= values.length) {
			throw new IllegalArgumentException(columnValue + " is out of range of " + name);
		}
		return values[(int) (long) columnValue];
	}

	@Override
	public Long toSql(TEnum javaValue) {
		return (long) javaValue.ordinal();
	}
}
