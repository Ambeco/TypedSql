package com.tbohne.sqlite.binders;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.BitSet;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class BitSetBinder
		implements SimpleColumnBinder<@Nullable BitSet, byte @Nullable[]>
{
	@Override
	public @Nullable BitSet fromSql(byte @Nullable[] columnValue) {
		if (columnValue == null) {
			return null;
		}
		return BitSet.valueOf(columnValue);
	}

	@Override
	public byte @Nullable[] toSql(@Nullable BitSet javaValue) {
		if (javaValue == null) {
			return null;
		}
		return javaValue.toByteArray();
	}
}
