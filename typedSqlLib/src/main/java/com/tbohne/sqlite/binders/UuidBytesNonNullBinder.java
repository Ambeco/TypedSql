package com.tbohne.sqlite.binders;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.nio.ByteBuffer;
import java.util.UUID;

public class UuidBytesNonNullBinder
		implements SimpleColumnBinder<UUID, byte []>
{
	private static final ByteBuffer buffer = ByteBuffer.allocate(16);

	@Override
	public UUID fromSql(byte [] columnValue) {
		synchronized (buffer) {
			buffer.clear();
			buffer.put(columnValue);
			return new UUID(buffer.getLong(0), buffer.getLong(1));
		}
	}

	@Override
	public byte [] toSql(UUID javaValue) {
		synchronized (buffer) {
			buffer.clear();
			buffer.putLong(0, javaValue.getMostSignificantBits());
			buffer.putLong(1, javaValue.getLeastSignificantBits());
			return buffer.array();
		}
	}
}
