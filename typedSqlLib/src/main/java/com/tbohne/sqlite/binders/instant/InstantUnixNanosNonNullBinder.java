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
public class InstantUnixNanosNonNullBinder
		implements SimpleColumnBinder<Instant, Long>
{
	@Override
	public Instant fromSql(Long columnValue) {
		if (columnValue == null) {
			return null;
		}
		return Instant.ofEpochSecond(columnValue / 1000000, columnValue % 1000000);
	}

	@Override
	public Long toSql(Instant javaValue) {
		if (javaValue == null) {
			return null;
		}
		return javaValue.toEpochMilli() * 1000 + javaValue.getNano();
	}
}
