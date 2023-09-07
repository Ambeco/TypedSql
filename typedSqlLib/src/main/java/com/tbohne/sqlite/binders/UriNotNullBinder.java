package com.tbohne.sqlite.binders;

import java.net.URI;

public class UriNotNullBinder
		implements SimpleColumnBinder<URI, String>
{
	@Override
	public URI fromSql(String columnValue) {
		return URI.create(columnValue);
	}

	@Override
	public String toSql(URI javaValue) {
		return javaValue.toString();
	}
}
