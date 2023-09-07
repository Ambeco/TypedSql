package com.tbohne.sqlite.exceptions;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ColumnInvalidException
		extends SQLiteDataException
{
	public ColumnInvalidException() {
	}

	public ColumnInvalidException(String message) {
		super(message);
	}

	public ColumnInvalidException(String message, Throwable cause) {
		super(message, cause);
	}

	public ColumnInvalidException(Throwable cause) {
		super(cause);
	}

	protected ColumnInvalidException(
			@Nullable String message, @Nullable Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
