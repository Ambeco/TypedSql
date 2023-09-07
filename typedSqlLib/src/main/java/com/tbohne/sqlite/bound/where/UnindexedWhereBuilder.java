package com.tbohne.sqlite.bound.where;

import com.tbohne.sqlite.bound.columns.AbstractJavaColumn;

import java.util.Collection;
import java.util.function.Function;

public interface UnindexedWhereBuilder<
		IndexedWhereBuilderImpl extends IndexedWhereBuilder<IndexedWhereBuilderImpl, Table>,
		Table>
		extends CompleteWhereBuilder<Table>{

	int addArgument(String argVal);

	<JavaType> int[] addArgument(AbstractJavaColumn<Table, JavaType, ?, ?> indexedColumn, JavaType value);

	<JavaType> IndexedWhereBuilderImpl whereIndexedEquals(
			AbstractJavaColumn<Table, JavaType, AbstractJavaColumn.IsIndexed, ?> indexedColumn, JavaType value);

	<JavaType> IndexedWhereBuilderImpl whereIndexedNotEquals(
			AbstractJavaColumn<Table, JavaType, AbstractJavaColumn.IsIndexed, ?> indexedColumn, JavaType value);

	<JavaType> IndexedWhereBuilderImpl whereIndexedLessThan(
			AbstractJavaColumn<Table, JavaType, AbstractJavaColumn.IsIndexed, ?> indexedColumn, JavaType value);

	<JavaType> IndexedWhereBuilderImpl whereIndexedLessEqualTo(
			AbstractJavaColumn<Table, JavaType, AbstractJavaColumn.IsIndexed, ?> indexedColumn, JavaType value);

	<JavaType> IndexedWhereBuilderImpl whereIndexedGreaterEqualTo(
			AbstractJavaColumn<Table, JavaType, AbstractJavaColumn.IsIndexed, ?> indexedColumn, JavaType value);

	<JavaType> IndexedWhereBuilderImpl whereIndexedGreaterThan(
			AbstractJavaColumn<Table, JavaType, AbstractJavaColumn.IsIndexed, ?> indexedColumn, JavaType value);

	<JavaType> IndexedWhereBuilderImpl whereIndexedIsNull(
			AbstractJavaColumn<Table, JavaType, AbstractJavaColumn.IsIndexed, ?> indexedColumn);

	<JavaType> IndexedWhereBuilderImpl whereIndexedNotNull(
			AbstractJavaColumn<Table, JavaType, AbstractJavaColumn.IsIndexed, ?> indexedColumn);

	<JavaType> IndexedWhereBuilderImpl whereIndexedBetween(
			AbstractJavaColumn<Table, JavaType, AbstractJavaColumn.IsIndexed, AbstractJavaColumn.SingleBinder> indexedColumn,
			JavaType minInclusive,
			JavaType maxInclusive);

	<JavaType> IndexedWhereBuilderImpl whereIndexedNotBetween(
			AbstractJavaColumn<Table, JavaType, AbstractJavaColumn.IsIndexed, AbstractJavaColumn.SingleBinder> column,
			JavaType minInclusive,
			JavaType maxInclusive);

	<JavaType> IndexedWhereBuilderImpl whereIndexedIn(
			AbstractJavaColumn<Table, JavaType, AbstractJavaColumn.IsIndexed, AbstractJavaColumn.SingleBinder> indexedColumn,
			Collection<JavaType> value);

	<JavaType> IndexedWhereBuilderImpl whereIndexedNotIn(
			AbstractJavaColumn<Table, JavaType, AbstractJavaColumn.IsIndexed, AbstractJavaColumn.SingleBinder> indexedColumn,
			Collection<JavaType> value);

	<JavaType> IndexedWhereBuilderImpl whereIndexedLike(
			AbstractJavaColumn<Table, JavaType, AbstractJavaColumn.IsIndexed, AbstractJavaColumn.SingleBinder> indexedColumn,
			String value);

	<JavaType> IndexedWhereBuilderImpl whereIndexedNotLike(
			AbstractJavaColumn<Table, JavaType, AbstractJavaColumn.IsIndexed, AbstractJavaColumn.SingleBinder> indexedColumn,
			String value);

	<JavaType> IndexedWhereBuilderImpl whereIndexedExpression(String expression);

	<JavaType> IndexedWhereBuilderImpl whereIndexedExpression(
			AbstractJavaColumn<Table, JavaType, AbstractJavaColumn.IsIndexed, AbstractJavaColumn.SingleBinder> indexedColumn,
			String expressionBeforeValue,
			JavaType value,
			String expressionAfterValue);

	@SuppressWarnings("unchecked")
	IndexedWhereBuilderImpl whereAnyOf(
			Function<UnindexedWhereBuilder<IndexedWhereBuilderImpl, Table>,
								IndexedWhereBuilderImpl>... subBuilders);

	IndexedWhereBuilderImpl withSlowTableScan(String explanation);

	IndexedWhereBuilderImpl and(IndexedWhereBuilderImpl andAlso);
}