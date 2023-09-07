package com.tbohne.sqlite.bound.where;

import com.tbohne.sqlite.bound.columns.AbstractJavaColumn;

import java.util.Collection;
import java.util.function.Function;

public interface IndexedWhereBuilder<
		IndexedWhereBuilderImpl extends IndexedWhereBuilder<IndexedWhereBuilderImpl, Table>,
		Table> extends CompleteWhereBuilder<Table>
{

	int addArgument(String argVal);

	<JavaType> int[] addArgument(AbstractJavaColumn<Table, JavaType, ?, ?> indexedColumn, JavaType value);

	<JavaType> IndexedWhereBuilderImpl whereEquals(AbstractJavaColumn<Table, JavaType, ?, ?> column, JavaType value);

	<JavaType> IndexedWhereBuilderImpl whereNotEquals(AbstractJavaColumn<Table, JavaType, ?, ?> column, JavaType value);

	<JavaType> IndexedWhereBuilderImpl whereLessThan(AbstractJavaColumn<Table, JavaType, ?, ?> column, JavaType value);

	<JavaType> IndexedWhereBuilderImpl whereLessEqualTo(AbstractJavaColumn<Table, JavaType, ?, ?> column, JavaType value);

	<JavaType> IndexedWhereBuilderImpl whereGreaterEqualTo(
			AbstractJavaColumn<Table, JavaType, ?, ?> column, JavaType value);

	<JavaType> IndexedWhereBuilderImpl whereGreaterThan(AbstractJavaColumn<Table, JavaType, ?, ?> column, JavaType value);

	<JavaType> IndexedWhereBuilderImpl whereIsNull(AbstractJavaColumn<Table, JavaType, ?, ?> column);

	<JavaType> IndexedWhereBuilderImpl whereNotNull(AbstractJavaColumn<Table, JavaType, ?, ?> column);

	<JavaType> IndexedWhereBuilderImpl whereBetween(
			AbstractJavaColumn<Table, JavaType, ?, AbstractJavaColumn.SingleBinder> column,
			JavaType minInclusive,
			JavaType maxInclusive);

	<JavaType> IndexedWhereBuilderImpl whereNotBetween(
			AbstractJavaColumn<Table, JavaType, ?, AbstractJavaColumn.SingleBinder> column,
			JavaType minInclusive,
			JavaType maxInclusive);

	<JavaType> IndexedWhereBuilderImpl whereIn(
			AbstractJavaColumn<Table, JavaType, ?, AbstractJavaColumn.SingleBinder> column, Collection<JavaType> value);

	<JavaType> IndexedWhereBuilderImpl whereNotIn(
			AbstractJavaColumn<Table, JavaType, ?, AbstractJavaColumn.SingleBinder> column, Collection<JavaType> value);

	<JavaType> IndexedWhereBuilderImpl whereLike(
			AbstractJavaColumn<Table, JavaType, ?, AbstractJavaColumn.SingleBinder> indexedColumn, String value);

	<JavaType> IndexedWhereBuilderImpl whereNotLike(
			AbstractJavaColumn<Table, JavaType, ?, AbstractJavaColumn.SingleBinder> indexedColumn, String value);

	<JavaType> IndexedWhereBuilderImpl whereExpression(String expression);

	<JavaType> IndexedWhereBuilderImpl whereExpression(
			AbstractJavaColumn<Table, JavaType, ?, AbstractJavaColumn.SingleBinder> column,
			String expressionBeforeValue,
			JavaType value,
			String expressionAfterValue);

	IndexedWhereBuilderImpl and(IndexedWhereBuilderImpl andAlso);
}