package com.tbohne.sqlite.bound.insert;

import com.google.common.util.concurrent.FluentFuture;

/**
 * How exactly to execute the insert statement
 **/
public interface InsertWithValues<Table, RowId> {

	RowId sync();
	FluentFuture<RowId> async();
}
