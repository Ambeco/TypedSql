package com.tbohne.sqlite.bound.select;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.CrossProcessCursor;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

import com.google.common.flogger.FluentLogger;
import com.google.common.flogger.StackSize;

import java.util.List;

public abstract class AbstractTypedCursorWrapper
		implements TypedCursor
{
	private static final FluentLogger logger = FluentLogger.forEnclosingClass();
	protected final Cursor delegate;
	private boolean didValidateWindow = false;

	protected AbstractTypedCursorWrapper(Cursor delegate) {
		this.delegate = delegate;
	}

	protected <T> T validateWindowCount(T value) {
		boolean previouslyValidated = didValidateWindow;
		didValidateWindow = true;
		if (previouslyValidated || !(delegate instanceof CrossProcessCursor)) {
			return value;
		}
		CursorWindow window = ((CrossProcessCursor) delegate).getWindow();
		if (window == null) {
			return value;
		}
		int cursorCount = delegate.getCount();
		int windowCount = window.getNumRows();
		if (windowCount > 0 && windowCount < cursorCount) {
			logger.atWarning()
						.withStackTrace(StackSize.SMALL)
						.log("Query has %d rows theoretically, but only %s rows fit in the "
								 + "CursorWindow buffer, so iterating past that may produce "
								 + "inconsistent results. This is an Android limitation. To reduce "
								 + "bugs, select fewer columns or fewer rows.", cursorCount, windowCount);
		}
		return value;
	}

	@Override
	public void close() {
		delegate.close();
	}

	public int getCount() {
		return validateWindowCount(delegate.getCount());
	}

	public Bundle getExtras() {
		return delegate.getExtras();
	}

	@RequiresApi(api = Build.VERSION_CODES.M)
	public void setExtras(Bundle extras) {
		delegate.setExtras(extras);
	}

	public Uri getNotificationUri() {
		return delegate.getNotificationUri();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	public List<Uri> getNotificationUris() {
		return delegate.getNotificationUris();
	}

	public int getPosition() {
		return delegate.getPosition();
	}

	public boolean isAfterLast() {
		return delegate.isAfterLast();
	}

	public boolean isBeforeFirst() {
		return delegate.isBeforeFirst();
	}

	public boolean isClosed() {
		return delegate.isClosed();
	}

	public boolean isFirst() {
		return delegate.isFirst();
	}

	public boolean isLast() {
		return delegate.isLast();
	}

	public boolean move(int offset) {
		return validateWindowCount(delegate.move(offset));
	}

	public boolean moveToFirst() {
		return validateWindowCount(delegate.moveToFirst());
	}

	public boolean moveToLast() {
		return validateWindowCount(delegate.moveToLast());
	}

	public boolean moveToNext() {
		return validateWindowCount(delegate.moveToNext());
	}

	public boolean moveToPosition(int position) {
		return validateWindowCount(delegate.moveToPosition(position));
	}

	public boolean moveToPrevious() {
		return validateWindowCount(delegate.moveToPrevious());
	}

	public void registerContentObserver(ContentObserver observer) {
		delegate.registerContentObserver(observer);
	}

	public void registerDataSetObserver(DataSetObserver observer) {
		delegate.registerDataSetObserver(observer);
	}

	public Bundle respond(Bundle extras) {
		return delegate.respond(extras);
	}

	public void setNotificationUri(ContentResolver cr, Uri uri) {
		delegate.setNotificationUri(cr, uri);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	public void setNotificationUris(ContentResolver cr, List<Uri> uris) {
		delegate.setNotificationUris(cr, uris);
	}

	public void unregisterContentObserver(ContentObserver observer) {
		delegate.unregisterContentObserver(observer);
	}

	public void unregisterDataSetObserver(DataSetObserver observer) {
		delegate.unregisterDataSetObserver(observer);
	}

	public void fillWindow(int position, CursorWindow window) {
		if (!(delegate instanceof CrossProcessCursor)) {
			throw new UnsupportedOperationException("delegate is not CrossProcessCursor");
		}
		((CrossProcessCursor) delegate).fillWindow(position, window);
		validateWindowCount(0);
	}

	public CursorWindow getWindow() {
		if (!(delegate instanceof CrossProcessCursor)) {
			throw new UnsupportedOperationException("delegate is not CrossProcessCursor");
		}
		return ((CrossProcessCursor) delegate).getWindow();
	}
}
