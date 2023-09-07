package com.tbohne.sqlite.bound.updateBulk;

import androidx.annotation.CheckResult;

import com.google.common.util.concurrent.FluentFuture;

import java.util.Set;

/**
 * How exactly to execute the update statement on a unique row with a where clause
 **/
public interface UpdateBulkComplete<Table, RowId> {

	/**
	 * Update multiple rows
	 *
	 * This is fastest.
	 *
	 * @return count or rows updated
	 **/
	@CheckResult
	int withoutDetailsSync();
	FluentFuture<Integer> withoutDetailsAsync();

	/**
	 * Update multiple rows
	 *
	 * This requires a second query to get the ids.
	 *
	 * @return set of updated row's Ids
	 **/
	Set<RowId> withDetailsSync();
	FluentFuture<Set<RowId>> withDetailsAsync();
}
