package com.tbohne.sqlite.binders;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.nio.ByteBuffer;
import java.util.UUID;

public class UuidBytesBinder
		implements SimpleColumnBinder<@Nullable UUID, byte @Nullable []>
{
	private static final ByteBuffer buffer = ByteBuffer.allocate(16);

	@Override
	public @Nullable UUID fromSql(byte @Nullable [] columnValue) {
		if (columnValue == null) {
			return null;
		}
		synchronized (buffer) {
			buffer.clear();
			buffer.put(columnValue);
			return new UUID(buffer.getLong(0), buffer.getLong(1));
		}
	}

	@Override
	public byte @Nullable [] toSql(@Nullable UUID javaValue) {
		if (javaValue == null) {
			return null;
		}
		synchronized (buffer) {
			buffer.clear();
			buffer.putLong(0, javaValue.getMostSignificantBits());
			buffer.putLong(1, javaValue.getLeastSignificantBits());
			return buffer.array();
		}
	}
}
