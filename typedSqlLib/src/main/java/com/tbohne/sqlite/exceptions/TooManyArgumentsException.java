package com.tbohne.sqlite.exceptions;

import org.checkerframework.checker.nullness.qual.Nullable;

public class TooManyArgumentsException
		extends SQLiteDataException
{
	public TooManyArgumentsException() {
	}

	public TooManyArgumentsException(String message) {
		super(message);
	}

	public TooManyArgumentsException(String message, Throwable cause) {
		super(message, cause);
	}

	public TooManyArgumentsException(Throwable cause) {
		super(cause);
	}

	protected TooManyArgumentsException(
			@Nullable String message, @Nullable Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
