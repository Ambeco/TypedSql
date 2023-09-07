package com.tbohne.sqlite.binders;

/**
 * Generates no Java binder code
 * <p>
 * When this is used, no Java is generated to read or write to this column specifically.
 * You must either use custom sql or a multibinder to read/write.
 */
public interface VoidBinder<T>
		extends SimpleColumnBinder<Void, T>
{
}
