package com.tbohne.sqlite.binders.instant;

import com.tbohne.sqlite.binders.SimpleColumnBinder;

import java.time.Instant;

/**
 * This stores date-times as an "ISO 8601" format string.
 *
 * <p> There are many ways to represent datetimes in databases, and this package has binders for many (but not all)
 * common formats. Sqlite works best with <a href="https://en.wikipedia.org/wiki/ISO_8601">"ISO 8601"</a>
 * format strings (aka {@link Instant8601Binder}), but can also work reasonably well with
 * <a href="https://en.wikipedia.org/wiki/Julian_day">Julian Date</a> (aka {@link InstantJulianDoubleBinder}).
 *
 * Sqlite can work with
 * <a href="https://en.wikipedia.org/wiki/Unix_time">Unix Seconds</a> stored as Doubles with some work (aka
 * {@link InstantUnixSecondsDoubleBinder}).  Sqlite does not have native methods for Unix time stored as an integer
 * number of Millis or Nanos, but as those are common formats elsewhere, I provide binders for those as well.
 **/
public class InstantJulianDoubleNonNullBinder
		implements SimpleColumnBinder<Instant, Double>
{
	@Override
	public Instant fromSql(Double columnValue) {
		long totalNanos = (long) (columnValue * 1000000000);
		long seconds = (long) (double) columnValue;
		long nanoAdjustment = (long) ((columnValue - seconds) * 1000000000);
		return Instant.ofEpochSecond(seconds, nanoAdjustment);
	}

	@Override
	public Double toSql(Instant javaValue) {
		return javaValue.getEpochSecond() + javaValue.getNano() / 1000000000.0;
	}
}
