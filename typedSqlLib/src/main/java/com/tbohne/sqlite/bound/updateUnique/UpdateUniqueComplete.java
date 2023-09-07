package com.tbohne.sqlite.bound.updateUnique;

import androidx.annotation.CheckResult;

import com.google.common.util.concurrent.FluentFuture;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.tbohne.sqlite.bound.UpdateResult;

/**
 * How exactly to execute the update statement on a unique row with a where clause
 **/
public interface UpdateUniqueComplete<Table> {

	/**
	 * Update a unique row
	 *
	 * @return
	 * 	true if the row exists and constraints are met (and is therefore updated)
	 * 	false if the row does not exist or constraints are not met (and is therefore not updated)
	 **/

	@CheckResult
	boolean withoutDetailsSync();
	FluentFuture<Boolean> withoutDetailsAsync();

	/**
	 * Update a unique row if the 'where' conditions match.
	 *
	 * This requires a second query if the update fails to determine why exactly.
	 *
	 * @return one of {UPDATED, ROW_FAILED_CONDITIONS, ROW_NOT_FOUND}
	 **/
	@CheckResult
	UpdateResult withDetailsSync();
	FluentFuture<UpdateResult> withDetailsAsync();

	/**
	 * Update a unique row if the 'where' conditions match.
	 *
	 * This requires a second query if the update fails to determine why exactly
	 *
	 * @return
	 * 	true if the row exists and constraints are met (and is therefore updated)
	 * 	false if the row exists but constraints are not met (and is therefore not updated)
	 * @throws com.tbohne.sqlite.exceptions.RowNotFoundException if the row does not exist
	 **/
	boolean existingRowSync();
	FluentFuture<Boolean> existingRowAsync();
}
