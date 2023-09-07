package com.tbohne.sqlite.binders;

import org.checkerframework.checker.nullness.qual.Nullable;

public class EnumBinder<TEnum extends Enum<TEnum>>
		implements SimpleColumnBinder<@Nullable TEnum, @Nullable Long>
{
	private final String name;
	private final TEnum[] values;

	//new EnumBinder<MyEnum>("MyEnum", MyEnum.values()));
	public EnumBinder(String name, TEnum[] values) {
		this.name = name;
		this.values = values;
	}

	@Override
	public @Nullable TEnum fromSql(@Nullable Long columnValue) {
		if (columnValue == null) {
			return null;
		}
		if (columnValue < 0) {
			throw new IllegalArgumentException(columnValue + " is out of range of " + name);
		}
		if (columnValue >= values.length) {
			throw new IllegalArgumentException(columnValue + " is out of range of " + name);
		}
		return values[(int) (long) columnValue];
	}

	@Override
	public @Nullable Long toSql(@Nullable TEnum javaValue) {
		if (javaValue == null) {
			return null;
		}
		return (long) javaValue.ordinal();
	}
}
