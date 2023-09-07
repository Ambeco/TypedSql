package com.tbohne.sqlite.processor.models;

import com.google.common.base.Strings;
import com.tbohne.sqlite.annotations.createTable.ColumnCollation;
import com.tbohne.sqlite.annotations.createTable.TableColumn;
import com.tbohne.sqlite.annotations.enums.Collation;
import com.tbohne.sqlite.processor.util.Output;
import com.tbohne.sqlite.processor.util.StringHelpers;

import org.checkerframework.checker.nullness.qual.Nullable;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

/**
 * {@link com.tbohne.sqlite.annotations.createTable.ColumnCollation}
 */
public class ColumnCollationModel {
	public final @Nullable String constraintName;
	public final Collation collation;

	private ColumnCollationModel(
			@Nullable String constraintName, Collation collation)
	{
		this.constraintName = constraintName;
		this.collation = collation;
	}

	public static @Nullable ColumnCollationModel build(
			ProcessingEnvironment environment,
			@Nullable Output messager,
			TypeElement tableElement,
			String columnName,
			TableColumn column)
	{
		ColumnCollation collation = column.collation();
		if (collation.constraintName().isEmpty() && collation.value() == Collation.UNSPECIFIED) {
			return null;
		}
		if (messager != null && !StringHelpers.validOrEmptySqlId(collation.constraintName())) {
			messager.error(
					tableElement,
					"Column %s has invalid collation name \"%s\"",
					columnName,
					collation.constraintName());
		}
		String name = Strings.emptyToNull(collation.constraintName());
		if (messager != null && collation.value() == Collation.UNSPECIFIED) {
			messager.error(tableElement, "Column %s Collation \"%s\" has has name but has no collation value", columnName);
			return null;
		}
		return new ColumnCollationModel(name, collation.value());
	}
}
