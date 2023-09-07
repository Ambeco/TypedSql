package com.tbohne.sqlite.binders;

/**
 * Generates no Java binder code for Long, String, Blob, and Double
 * <p>
 * This can be used when another column determines the type in this column.
 * This is only provided to enable bugfixes, and should never be used by design.
 */
public interface RawBinder<T>
		extends SimpleColumnBinder<Void, T>
{
}
