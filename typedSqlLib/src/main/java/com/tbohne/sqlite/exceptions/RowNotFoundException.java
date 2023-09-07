package com.tbohne.sqlite.exceptions;

import org.checkerframework.checker.nullness.qual.Nullable;

public class RowNotFoundException
		extends SQLiteDataException
{
	public RowNotFoundException() {
	}

	public RowNotFoundException(String message) {
		super(message);
	}

	public RowNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public RowNotFoundException(Throwable cause) {
		super(cause);
	}

	protected RowNotFoundException(
			@Nullable String message, @Nullable Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
