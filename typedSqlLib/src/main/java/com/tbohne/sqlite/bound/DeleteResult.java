package com.tbohne.sqlite.bound;

public enum DeleteResult {
	DELETED,
	ROW_FAILED_CONDITIONS, //The row existed, but didn't match the 'where' clause
	ROW_NOT_FOUND,
}
