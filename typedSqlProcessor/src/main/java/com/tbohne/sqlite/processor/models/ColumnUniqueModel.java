package com.tbohne.sqlite.processor.models;

import com.google.common.base.Strings;
import com.tbohne.sqlite.annotations.createTable.ColumnUnique;
import com.tbohne.sqlite.annotations.createTable.TableColumn;
import com.tbohne.sqlite.annotations.enums.ConflictAction;
import com.tbohne.sqlite.processor.util.Output;
import com.tbohne.sqlite.processor.util.StringHelpers;

import org.checkerframework.checker.nullness.qual.Nullable;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

/**
 * {@link com.tbohne.sqlite.annotations.createTable.ColumnUnique}
 */
public class ColumnUniqueModel {
	public final @Nullable String constraintName;
	public final ConflictAction onConflict;

	private ColumnUniqueModel(
			@Nullable String constraintName, ConflictAction onConflict)
	{
		this.constraintName = constraintName;
		this.onConflict = onConflict;
	}

	public static ColumnUniqueModel buildForImplicitPrimaryKey() {
		return new ColumnUniqueModel(null, ConflictAction.FAIL);
	}

	public static @Nullable ColumnUniqueModel build(
			ProcessingEnvironment environment,
			@Nullable Output messager,
			TypeElement tableElement,
			String columnSqlName,
			TableColumn column)
	{
		ColumnUnique unique = column.unique();
		if (!unique.value() && unique.constraintName().isEmpty() && unique.onConflict() == ConflictAction.UNSPECIFIED) {
			return null;
		}
		if (messager != null && tableElement != null && !unique.value()) {
			messager.error(tableElement, "Column %s has unique name or type but is not unique", columnSqlName);
		}
		if (messager != null && tableElement != null && !StringHelpers.validOrEmptySqlId(unique.constraintName())) {
			messager.error(
					tableElement,
					"Column %s has invalid Unique constraint name \"%s\"",
					columnSqlName,
					unique.constraintName());
		}
		String name = Strings.emptyToNull(unique.constraintName());
		return new ColumnUniqueModel(name, unique.onConflict());
	}
}
