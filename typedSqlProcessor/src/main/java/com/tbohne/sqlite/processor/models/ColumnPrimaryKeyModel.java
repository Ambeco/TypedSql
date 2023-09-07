package com.tbohne.sqlite.processor.models;

import com.google.common.base.Strings;
import com.tbohne.sqlite.annotations.createTable.ColumnPrimaryKey;
import com.tbohne.sqlite.annotations.createTable.TableColumn;
import com.tbohne.sqlite.annotations.enums.ConflictAction;
import com.tbohne.sqlite.annotations.enums.KeyOrder;
import com.tbohne.sqlite.processor.util.Output;
import com.tbohne.sqlite.processor.util.StringHelpers;

import org.checkerframework.checker.nullness.qual.Nullable;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

/**
 * {@link com.tbohne.sqlite.annotations.createTable.ColumnPrimaryKey}
 */
public class ColumnPrimaryKeyModel {
	public final @Nullable String constraintName;
	public final KeyOrder order;
	public final ConflictAction onConflict;
	public final boolean autoIncrement;

	private ColumnPrimaryKeyModel(
			@Nullable String constraintName, KeyOrder order, ConflictAction onConflict, boolean autoIncrement)
	{
		this.constraintName = constraintName;
		this.order = order;
		this.onConflict = onConflict;
		this.autoIncrement = autoIncrement;
	}

	public static ColumnPrimaryKeyModel buildForImplicitPrimaryKey() {
		return new ColumnPrimaryKeyModel(null, KeyOrder.UNSPECIFIED, ConflictAction.UNSPECIFIED, false);
	}

	public static @Nullable ColumnPrimaryKeyModel build(
			ProcessingEnvironment environment,
			@Nullable Output messager,
			TypeElement tableElement,
			String columnSqlName,
			TableColumn column)
	{
		ColumnPrimaryKey primaryKey = column.primaryKey();
		if (!primaryKey.value()
				&& primaryKey.constraintName().isEmpty()
				&& primaryKey.order() == KeyOrder.UNSPECIFIED
				&& primaryKey.onConflict() == ConflictAction.UNSPECIFIED
				&& !primaryKey.autoIncrement())
		{
			return null;
		}
		if (messager != null && tableElement != null && !primaryKey.value()) {
			messager.error(tableElement,
										 "Column %s has primary key constraint values but is not a primary key",
										 columnSqlName);
		}
		if (messager != null && tableElement != null && !StringHelpers.validOrEmptySqlId(primaryKey.constraintName())) {
			messager.error(tableElement,
										 "Column %s has invalid PrimaryKey constraint name \"%s\"",
										 columnSqlName,
										 primaryKey.constraintName());
		}
		String name = Strings.emptyToNull(primaryKey.constraintName());
		return new ColumnPrimaryKeyModel(name, primaryKey.order(), primaryKey.onConflict(), primaryKey.autoIncrement());
	}
}
