package com.tbohne.sqlite.exceptions;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * An exception that means the query succeeded, but found unexpected results
 * <p>
 * A normal SQLiteException means there's something wrong with the command provided to Sqlite, or
 * that something went wrong while executing the command.
 */
public class SQLiteDataException
		extends RuntimeException
{
	public SQLiteDataException() {
	}

	public SQLiteDataException(String message) {
		super(message);
	}

	public SQLiteDataException(String message, Throwable cause) {
		super(message, cause);
	}

	public SQLiteDataException(Throwable cause) {
		super(cause);
	}

	protected SQLiteDataException(
			@Nullable String message, @Nullable Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
