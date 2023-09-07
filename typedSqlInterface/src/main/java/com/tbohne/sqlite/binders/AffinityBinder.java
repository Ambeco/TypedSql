package com.tbohne.sqlite.binders;

//This is a magic interface that gets automatically replaced with one of
// ByteArrayBinder, ByteArrayNonNullBinder, DoubleBinder, DoubleNonNullBinder, LongBinder,
// LongNonNullBinder, StringBinder, or StringNonNullBinder, depending on the storage class
// of the column
public interface AffinityBinder
		extends SimpleColumnBinder<Void, Void>
{
}
