package com.tbohne.sqlite.bound.deleteUnique;

import androidx.annotation.CheckResult;

import com.google.common.util.concurrent.FluentFuture;
import com.tbohne.sqlite.bound.DeleteResult;

/**
 * How exactly to execute the update statement on a unique row with a where clause
 **/
public interface DeleteUniqueComplete<Table> {

	/**
	 * Delete a unique row
	 *
	 * @return
	 * 	true if the row existed and deletion passed all constraints (and therefore succeeded)
	 * 	false if the row does not exist or deletion failed some constraints (and therefore was not deleted)
	 **/

	@CheckResult
	boolean withoutDetailsSync();
	FluentFuture<Boolean> withoutDetailsAsync();

	/**
	 * Delete a unique row and determine if the row already existed or not
	 *
	 * This requires a second query if the update fails to determine why exactly.
	 *
	 * @return one of {UPDATED, ROW_FAILED_CONDITIONS, ROW_NOT_FOUND}
	 **/
	@CheckResult
	DeleteResult withDetailsSync();
	FluentFuture<DeleteResult> withDetailsAsync();

	/**
	 * Delete a unique row and determine if the row already existed or not
	 *
	 * This requires a second query if the update fails to determine why exactly
	 *
	 * @return
	 * 	true if the row existed and deletion passed all constraints (and therefore succeeded)
	 * 	false if the row deletion failed some constraints (and therefore was not deleted)
	 * @throws com.tbohne.sqlite.exceptions.RowNotFoundException if the row did not exist
	 **/
	boolean existingRowSync();
	FluentFuture<Boolean> existingRowAsync();
}
