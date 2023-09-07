package com.tbohne.sqlite.exceptions;

import org.checkerframework.checker.nullness.qual.Nullable;

public class SelectionHasNoIdException
		extends SQLiteDataException
{
	public SelectionHasNoIdException() {
	}

	public SelectionHasNoIdException(String message) {
		super(message);
	}

	public SelectionHasNoIdException(String message, Throwable cause) {
		super(message, cause);
	}

	public SelectionHasNoIdException(Throwable cause) {
		super(cause);
	}

	protected SelectionHasNoIdException(
			@Nullable String message, @Nullable Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
