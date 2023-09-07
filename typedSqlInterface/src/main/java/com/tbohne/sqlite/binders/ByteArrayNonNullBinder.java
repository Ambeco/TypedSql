package com.tbohne.sqlite.binders;

public class ByteArrayNonNullBinder
		implements SimpleColumnBinder<byte[], byte[]>
{
	@Override
	public byte[] fromSql(byte[] columnValue) {
		return columnValue;
	}

	@Override
	public byte[] toSql(byte[] javaValue) {
		return javaValue;
	}
}
