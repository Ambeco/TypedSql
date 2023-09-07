package com.tbohne.sqlite.binders;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class LocaleNonNullBinder
		implements SimpleColumnBinder<Locale, String>
{
	@Override
	public Locale fromSql(String columnValue) {
		return Locale.forLanguageTag(columnValue);
	}

	@Override
	public String toSql(Locale javaValue) {
		return javaValue.toLanguageTag();
	}
}
