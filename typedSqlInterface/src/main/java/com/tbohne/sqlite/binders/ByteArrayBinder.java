package com.tbohne.sqlite.binders;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ByteArrayBinder
		implements SimpleColumnBinder<byte @Nullable [], byte @Nullable []>
{
	@Override
	public byte @Nullable [] fromSql(byte @Nullable [] columnValue) {
		return columnValue;
	}

	@Override
	public byte @Nullable [] toSql(byte @Nullable [] javaValue) {
		return javaValue;
	}
}
