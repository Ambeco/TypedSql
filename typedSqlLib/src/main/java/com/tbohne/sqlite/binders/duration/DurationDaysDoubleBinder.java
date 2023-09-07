package com.tbohne.sqlite.binders.duration;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.tbohne.sqlite.binders.SimpleColumnBinder;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * This stores durations as an "ISO 8601" format string.
 *
 * <p> There are many ways to represent durations in databases, and this package has binders for many (but not all)
 * common formats. Sqlite works best with <a href="https://en.wikipedia.org/wiki/ISO_8601">"ISO 8601"</a>
 * format strings (aka {@link Duration8601Binder}), but can also work reasonably well with
 * <a href="https://en.wikipedia.org/wiki/Julian_day">Julian Date</a> (aka {@link DurationDaysDoubleBinder}).
 *
 * Sqlite can work with
 * <a href="https://en.wikipedia.org/wiki/Unix_time">Unix Seconds</a> stored as Doubles with some work (aka
 * {@link DurationSecondsDoubleBinder}).  Sqlite does not have native methods for Unix time stored as an integer
 * number of Millis or Nanos, but as those are common formats elsewhere, I provide binders for those as well.
 **/
@RequiresApi(api = Build.VERSION_CODES.O)
public class DurationDaysDoubleBinder
		implements SimpleColumnBinder<@Nullable Duration, @Nullable Double>
{
	@Override
	public @Nullable Duration fromSql(@Nullable Double columnValue) {
		if (columnValue == null) {
			return null;
		}
		double asSeconds = columnValue * TimeUnit.DAYS.toSeconds(1);
		long seconds = (long) asSeconds;
		long nanos = (long) ((asSeconds - seconds) * TimeUnit.SECONDS.toMillis(1));
		return Duration.ofSeconds(seconds, nanos);
	}

	@Override
	public @Nullable Double toSql(@Nullable Duration javaValue) {
		if (javaValue == null) {
			return null;
		}
		return javaValue.toDays()
					 + (double) javaValue.getSeconds() /  TimeUnit.DAYS.toSeconds(1)
					 + (double) javaValue.getNano() /  TimeUnit.SECONDS.toNanos(1);
	}
}
