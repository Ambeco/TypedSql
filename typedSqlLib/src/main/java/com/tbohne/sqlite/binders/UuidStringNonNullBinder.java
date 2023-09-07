package com.tbohne.sqlite.binders;

import java.util.UUID;

public class UuidStringNonNullBinder
		implements SimpleColumnBinder<UUID, String>
{
	@Override
	public UUID fromSql(String columnValue) {
		return UUID.fromString(columnValue);
	}

	@Override
	public String toSql(UUID javaValue) {
		return javaValue.toString();
	}
}
