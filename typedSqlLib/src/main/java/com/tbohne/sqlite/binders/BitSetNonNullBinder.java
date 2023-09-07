package com.tbohne.sqlite.binders;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.BitSet;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class BitSetNonNullBinder
		implements SimpleColumnBinder<BitSet, byte[]>
{
	@Override
	public BitSet fromSql(byte[] columnValue) {
		if (columnValue == null) {
			return null;
		}
		return BitSet.valueOf(columnValue);
	}

	@Override
	public byte[] toSql(BitSet javaValue) {
		if (javaValue == null) {
			return null;
		}
		return javaValue.toByteArray();
	}
}
