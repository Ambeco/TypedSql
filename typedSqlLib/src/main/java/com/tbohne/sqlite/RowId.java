package com.tbohne.sqlite;

import androidx.annotation.NonNull;

public final class RowId<T> {
	private final long value;

	public RowId(long value) {
		this.value = value;
	}

	public long getValue() {
		return value;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof RowId) {
			return ((RowId<?>) other).value == value;
		} else if (other instanceof Long) {
			return ((Long) other) == value;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Long.hashCode(value);
	}

	@NonNull
	@Override
	public String toString() {
		return Long.toString(value);
	}
}
