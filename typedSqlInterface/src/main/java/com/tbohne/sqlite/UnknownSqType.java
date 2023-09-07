package com.tbohne.sqlite;

public interface UnknownSqType {
	boolean isNull();

	Double getAsDouble();

	Long getAsLong();

	String getAsString();

	byte[] getAsBlob();

	enum SqlType {
		NULL,
		DOUBLE,
		LONG,
		STRING,
		BLOB
	}
}
