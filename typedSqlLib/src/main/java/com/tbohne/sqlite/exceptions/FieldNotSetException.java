package com.tbohne.sqlite.exceptions;

import org.checkerframework.checker.nullness.qual.Nullable;

public class FieldNotSetException
		extends RuntimeException
{
	public FieldNotSetException() {
	}

	public FieldNotSetException(String message) {
		super(message);
	}

	public FieldNotSetException(String message, Throwable cause) {
		super(message, cause);
	}

	public FieldNotSetException(Throwable cause) {
		super(cause);
	}

	protected FieldNotSetException(
			@Nullable String message, @Nullable Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
