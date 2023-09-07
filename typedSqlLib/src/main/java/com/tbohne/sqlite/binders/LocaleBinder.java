package com.tbohne.sqlite.binders;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.net.URI;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class LocaleBinder
		implements SimpleColumnBinder<@Nullable Locale, @Nullable String>
{
	@Override
	public @Nullable Locale fromSql(@Nullable String columnValue) {
		if (columnValue == null) {
			return null;
		}
		return Locale.forLanguageTag(columnValue);
	}

	@Override
	public @Nullable String toSql(@Nullable Locale javaValue) {
		if (javaValue == null) {
			return null;
		}
		return javaValue.toLanguageTag();
	}
}
