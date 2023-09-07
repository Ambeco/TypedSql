package com.tbohne.sqlite.processor.models;

import com.google.common.base.Strings;
import com.tbohne.sqlite.annotations.createTable.NotNull;
import com.tbohne.sqlite.annotations.enums.ConflictAction;
import com.tbohne.sqlite.processor.util.Output;
import com.tbohne.sqlite.processor.util.StringHelpers;

import org.checkerframework.checker.nullness.qual.Nullable;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

/**
 * {@link com.tbohne.sqlite.annotations.createTable.NotNull}
 */
public class NotNullModel {
	public final @Nullable String constraintName;
	public final ConflictAction onConflict;

	private NotNullModel(
			@Nullable String constraintName, ConflictAction onConflict)
	{
		this.constraintName = constraintName;
		this.onConflict = onConflict;
	}

	public static NotNullModel buildForImplicitPrimaryKey() {
		return new NotNullModel(null, ConflictAction.FAIL);
	}

	public static @Nullable NotNullModel build(
			ProcessingEnvironment environment,
			@Nullable Output messager,
			TypeElement tableElement,
			String columnName,
			NotNull notNull)
	{
		if (!notNull.value() && notNull.constraintName().isEmpty() && notNull.onConflict() == ConflictAction.UNSPECIFIED) {
			return null;
		}
		if (messager != null && !notNull.value()) {
			messager.error(tableElement, "Column %s has NonNull name or onConflict, but is nullable", columnName);
		}
		if (!notNull.value()) {
			return null;
		}
		if (messager != null && !StringHelpers.validOrEmptySqlId(notNull.constraintName())) {
			messager.error(
					tableElement,
					"Column %s has invalid NotNull constraint name \"%s\"",
					columnName,
					notNull.constraintName());
		}
		return new NotNullModel(Strings.emptyToNull(notNull.constraintName()), notNull.onConflict());
	}

}
