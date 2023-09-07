package com.tbohne.sqlite.exceptions;

import org.checkerframework.checker.nullness.qual.Nullable;

public class TooManyRowsException
		extends SQLiteDataException
{
	public TooManyRowsException() {
	}

	public TooManyRowsException(String message) {
		super(message);
	}

	public TooManyRowsException(String message, Throwable cause) {
		super(message, cause);
	}

	public TooManyRowsException(Throwable cause) {
		super(cause);
	}

	protected TooManyRowsException(
			@Nullable String message, @Nullable Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
