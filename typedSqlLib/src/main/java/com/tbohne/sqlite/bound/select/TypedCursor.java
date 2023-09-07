package com.tbohne.sqlite.bound.select;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.CursorWindow;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

import java.io.Closeable;
import java.util.List;

/**
 * Basically the same as CrossProcessCursor, except without methods to read rows.
 */
public interface TypedCursor
		extends Closeable
{
	@Override
	void close();

	int getCount();

	Bundle getExtras();

	@RequiresApi(api = Build.VERSION_CODES.M)
	void setExtras(Bundle extras);

	Uri getNotificationUri();

	@RequiresApi(api = Build.VERSION_CODES.Q)
	List<Uri> getNotificationUris();

	int getPosition();

	boolean isAfterLast();

	boolean isBeforeFirst();

	boolean isClosed();

	boolean isFirst();

	boolean isLast();

	boolean move(int offset);

	boolean moveToFirst();

	boolean moveToLast();

	boolean moveToNext();

	boolean moveToPosition(int position);

	boolean moveToPrevious();

	void registerContentObserver(ContentObserver observer);

	void registerDataSetObserver(DataSetObserver observer);

	Bundle respond(Bundle extras);

	void setNotificationUri(ContentResolver cr, Uri uri);

	@RequiresApi(api = Build.VERSION_CODES.Q)
	void setNotificationUris(ContentResolver cr, List<Uri> uris);

	void unregisterContentObserver(ContentObserver observer);

	void unregisterDataSetObserver(DataSetObserver observer);

	void fillWindow(int position, CursorWindow window);

	CursorWindow getWindow();
}
