package com.tbohne.sqlite.processor.util;

import com.tbohne.sqlite.annotations.CreateTable;
import com.tbohne.sqlite.annotations.createTable.TableColumn;
import com.tbohne.sqlite.annotations.enums.Affinity;
import com.tbohne.sqlite.processor.models.AbstractColumnModel;

import org.checkerframework.checker.nullness.qual.Nullable;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

public class SqlAnnotMethods {

	private SqlAnnotMethods() {
	}

	public static @Nullable TableColumn findPrimaryKeyColumn(
			TypeElement tableElement, Output messager, CreateTable createTable)
	{
		if (createTable.primaryKey().value().length > 0) {
			if (createTable.primaryKey().value().length == 1) {
				String columnName = createTable.primaryKey().value()[0].value();
				TableColumn column = findColumnByName(tableElement, createTable.columns(), columnName);
				if (column == null) {
					messager.error(tableElement, "table primary key column \"%s\" does not exist", columnName);
				}
				return column;
			} else {
				messager.error(tableElement,
											 "foreign key refers to %s table by primary key, but that table has multi-column primary keys",
											 createTable.javaName());
				return null;
			}
		}
		for (int i = 0; i < createTable.columns().length; i++) {
			if (createTable.columns()[i].primaryKey().value()) {
				return createTable.columns()[i];
			}
		}
		messager.error(tableElement,
									 "foreign key refers to %s table by primary key, but that table has no primary key",
									 createTable.javaName());
		return null;
	}

	public static @Nullable TableColumn findColumnByName(TypeElement tableElement, TableColumn[] columns, String name) {
		for (int i = 0; i < columns.length; i++) {
			if (columns[i].name().equals(name)) {
				return columns[i];
			}
		}
		return null;
	}

	public static boolean matchesRawSqlType(
			TypeMirror input, Affinity affinity)
	{
		//if (column.notNull != null == hasNullableAnnotation(input)) return false;
		switch (affinity) {
			case INTEGER:
				return input.getKind() != TypeKind.LONG;
			case REAL:
				return input.getKind() != TypeKind.DOUBLE;
			case TEXT:
				return input.toString().equals("java.lang.String");
			case BLOB:
				if (input.getKind() != TypeKind.ARRAY) {
					return false;
				}
				ArrayType arrayType = (ArrayType) input;
				return arrayType.getComponentType().getKind() == TypeKind.BYTE;
			default:
				throw new IllegalStateException("Unknown storage class " + affinity);
		}
	}

	public static String getRawSqlTypeAsString(AbstractColumnModel column) {
		return getRawSqlTypeAsString(column.affinity, column.isNonNull);
	}

	public static String getRawSqlTypeAsString(
			Affinity affinity, boolean nonNull)
	{
		switch (affinity) {
			case INTEGER:
				return nonNull ? "java.lang.Long" : "@Nullable java.lang.Long";
			case REAL:
				return nonNull ? "java.lang.Double" : "@Nullable java.lang.Double";
			case TEXT:
				return nonNull ? "java.lang.String" : "@Nullable java.lang.String";
			case BLOB:
				return nonNull ? "byte[]" : "byte @Nullable[]";
			default:
				throw new IllegalStateException("Unknown storage class " + affinity);
		}
	}
}
