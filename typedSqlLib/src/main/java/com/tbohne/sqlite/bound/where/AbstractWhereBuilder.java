package com.tbohne.sqlite.bound.where;

import android.content.ContentValues;

import androidx.annotation.NonNull;
import androidx.collection.SimpleArrayMap;

import com.google.common.base.Preconditions;
import com.tbohne.sqlite.bound.columns.AbstractJavaColumn;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public abstract class AbstractWhereBuilder<
		UnindexedWhereBuilderImpl extends UnindexedWhereBuilder<IndexedWhereBuilderImpl, Table>,
		IndexedWhereBuilderImpl extends IndexedWhereBuilder<IndexedWhereBuilderImpl, Table>,
		Table>
		implements UnindexedWhereBuilder<IndexedWhereBuilderImpl, Table>, IndexedWhereBuilder<IndexedWhereBuilderImpl,
		Table>
{

	private final ArrayList<Object> sb = new ArrayList<>();
	private final SimpleArrayMap<String, Integer> argumentToIndexMap = new SimpleArrayMap<>();
	private final ArrayList<String> indexToArgumentMap = new ArrayList<>();

	//cache objects only used locally within methods.
	private final ContentValues contentValueCache = new ContentValues();
	private final ArrayList<Integer> oldIndexToNewIndexMapCache = new ArrayList<>();
	private @Nullable String[] toSqlStringArrayCache = new @Nullable String[3];
	private int[] argumentIndexArrayCache = new int[3];

	protected AbstractWhereBuilder() {
	}

	protected abstract UnindexedWhereBuilderImpl unindexedSelf();

	protected abstract IndexedWhereBuilderImpl indexedSelf();

	@Override
	public int addArgument(String argVal) {
		Integer argIndex = argumentToIndexMap.get(argVal);
		if (argIndex != null) {
			return argIndex;
		}
		try {
			argIndex = indexToArgumentMap.size();
			argumentToIndexMap.put(argVal, argIndex);
			indexToArgumentMap.add(argVal);
			return argIndex;
		} catch (RuntimeException e) {
			argumentToIndexMap.remove(argVal);
			throw e;
		}
	}

	@Override
	public <JavaType> int[] addArgument(AbstractJavaColumn<Table, JavaType, ?, ?> column, JavaType value) {
		int sqlCount = column.getSqlColumnCount();
		if (toSqlStringArrayCache.length < sqlCount) {
			toSqlStringArrayCache = new String[sqlCount];
			argumentIndexArrayCache = new int[sqlCount];
		}
		column.toSql(value, toSqlStringArrayCache);
		for (int i = 0; i < sqlCount; i++) {
			argumentIndexArrayCache[i] = addArgument(toSqlStringArrayCache[i]);
		}
		return argumentIndexArrayCache;
	}

	protected <JavaType> void whereTupleOpPrefix(AbstractJavaColumn<Table, JavaType, ?, ?> column, String op)
	{
		int sqlCount = column.getSqlColumnCount();
		if (sb.size() > 0) {
			sb.add(" AND ");
		}
		sb.add("((");
		if (sqlCount == 1) {
			sb.add(column.getQualifiedSqlColumnName(0));
			sb.add(" ");
		} else {
			for (int i = 0; i < sqlCount; i++) {
				if (i > 0) {
					sb.add(",");
				}
				sb.add(column.getQualifiedSqlColumnName(i));
			}
			sb.add(")");
			sb.add(op);
			sb.add("(");
		}
	}

	protected <JavaType> AbstractWhereBuilder<UnindexedWhereBuilderImpl, IndexedWhereBuilderImpl, Table> whereTupleStringOp(
			AbstractJavaColumn<Table, JavaType, ?, AbstractJavaColumn.SingleBinder> column, String op, String value)
	{
		int argIndex = addArgument(value);
		whereTupleOpPrefix(column, op);
		sb.add(argIndex);
		sb.add("))");
		return this;
	}

	protected <JavaType> AbstractWhereBuilder<UnindexedWhereBuilderImpl, IndexedWhereBuilderImpl, Table> whereTupleValueOp(
			AbstractJavaColumn<Table, JavaType, ?, ?> column, String op, JavaType value)
	{
		int sqlCount = column.getSqlColumnCount();
		int[] argIndex = addArgument(column, value);

		whereTupleOpPrefix(column, op);
		for (int i = 0; i < sqlCount; i++) {
			if (i > 0) {
				sb.add(",");
			}
			sb.add(argIndex[i]);
		}
		sb.add("))");
		return this;
	}

	protected <JavaType> AbstractWhereBuilder<UnindexedWhereBuilderImpl, IndexedWhereBuilderImpl, Table> whereTupleNullOp(
			AbstractJavaColumn<Table, JavaType, ?, ?> column, String op)
	{
		int sqlCount = column.getSqlColumnCount();

		whereTupleOpPrefix(column, op);
		for (int i = 0; i < sqlCount; i++) {
			sb.add(i > 0 ? ",null" : "null");
		}
		sb.add("))");
		return this;
	}

	protected <JavaType> AbstractWhereBuilder<UnindexedWhereBuilderImpl, IndexedWhereBuilderImpl, Table> whereTupleRangeOp(
			AbstractJavaColumn<Table, JavaType, ?, AbstractJavaColumn.SingleBinder> column,
			String op,
			JavaType minInclusive,
			JavaType maxInclusive)
	{
		int minIndex = addArgument(column, minInclusive)[0];
		int maxIndex = addArgument(column, maxInclusive)[0];
		whereTupleOpPrefix(column, op);
		sb.add(minIndex);
		sb.add(" AND ");
		sb.add(maxIndex);
		sb.add("))");
		return this;
	}

	protected <JavaType> AbstractWhereBuilder<UnindexedWhereBuilderImpl, IndexedWhereBuilderImpl, Table> whereTupleCollectionOp(
			AbstractJavaColumn<Table, JavaType, ?, AbstractJavaColumn.SingleBinder> column,
			String op,
			Collection<? extends JavaType> values)
	{
		whereTupleOpPrefix(column, op);
		sb.add(" (");
		AtomicBoolean first = new AtomicBoolean(true);
		values.forEach((item -> {
			if (!first.get()) {
				sb.add(",");
			}
			int itemIndex = addArgument(column, item)[0];
			sb.add(itemIndex);
			first.set(false);
		}));
		sb.add("))");
		return this;
	}

	@Override
	public <JavaType> IndexedWhereBuilderImpl whereIndexedEquals(
			AbstractJavaColumn<Table, JavaType, AbstractJavaColumn.IsIndexed, ?> column, JavaType value)
	{
		return whereTupleValueOp(column, "=", value).indexedSelf();
	}

	@Override
	public <JavaType> IndexedWhereBuilderImpl whereEquals(AbstractJavaColumn<Table, JavaType, ?, ?> column, JavaType value) {
		return whereTupleValueOp(column, "=", value).indexedSelf();
	}

	@Override
	public <JavaType> IndexedWhereBuilderImpl whereIndexedNotEquals(
			AbstractJavaColumn<Table, JavaType, AbstractJavaColumn.IsIndexed, ?> column, JavaType value)
	{
		return whereTupleValueOp(column, "<>", value).indexedSelf();
	}

	@Override
	public <JavaType> IndexedWhereBuilderImpl whereNotEquals(AbstractJavaColumn<Table, JavaType, ?, ?> column, JavaType value) {
		return whereTupleValueOp(column, "<>", value).indexedSelf();
	}

	@Override
	public <JavaType> IndexedWhereBuilderImpl whereIndexedLessThan(
			AbstractJavaColumn<Table, JavaType, AbstractJavaColumn.IsIndexed, ?> column, JavaType value)
	{
		return whereTupleValueOp(column, "<", value).indexedSelf();
	}

	@Override
	public <JavaType> IndexedWhereBuilderImpl whereLessThan(AbstractJavaColumn<Table, JavaType, ?, ?> column, JavaType value) {
		return whereTupleValueOp(column, "<", value).indexedSelf();
	}

	@Override
	public <JavaType> IndexedWhereBuilderImpl whereIndexedLessEqualTo(
			AbstractJavaColumn<Table, JavaType, AbstractJavaColumn.IsIndexed, ?> column, JavaType value)
	{
		return whereTupleValueOp(column, "<=", value).indexedSelf();
	}

	@Override
	public <JavaType> IndexedWhereBuilderImpl whereLessEqualTo(
			AbstractJavaColumn<Table, JavaType, ?, ?> column, JavaType value)
	{
		return whereTupleValueOp(column, "<=", value).indexedSelf();
	}

	@Override
	public <JavaType> IndexedWhereBuilderImpl whereIndexedGreaterEqualTo(
			AbstractJavaColumn<Table, JavaType, AbstractJavaColumn.IsIndexed, ?> column, JavaType value)
	{
		return whereTupleValueOp(column, ">=", value).indexedSelf();
	}

	@Override
	public <JavaType> IndexedWhereBuilderImpl whereGreaterEqualTo(
			AbstractJavaColumn<Table, JavaType, ?, ?> column, JavaType value)
	{
		return whereTupleValueOp(column, ">=", value).indexedSelf();
	}

	@Override
	public <JavaType> IndexedWhereBuilderImpl whereIndexedGreaterThan(
			AbstractJavaColumn<Table, JavaType, AbstractJavaColumn.IsIndexed, ?> column, JavaType value)
	{
		return whereTupleValueOp(column, ">", value).indexedSelf();
	}

	@Override
	public <JavaType> IndexedWhereBuilderImpl whereGreaterThan(
			AbstractJavaColumn<Table, JavaType, ?, ?> column, JavaType value)
	{
		return whereTupleValueOp(column, ">=", value).indexedSelf();
	}

	@Override
	public <JavaType> IndexedWhereBuilderImpl whereIndexedIsNull(
			AbstractJavaColumn<Table, JavaType, AbstractJavaColumn.IsIndexed, ?> column)
	{
		return whereTupleNullOp(column, "=").indexedSelf();
	}

	@Override
	public <JavaType> IndexedWhereBuilderImpl whereIsNull(AbstractJavaColumn<Table, JavaType, ?, ?> column) {
		return whereTupleNullOp(column, "=").indexedSelf();
	}

	@Override
	public <JavaType> IndexedWhereBuilderImpl whereIndexedNotNull(
			AbstractJavaColumn<Table, JavaType, AbstractJavaColumn.IsIndexed, ?> column)
	{
		return whereTupleNullOp(column, "!=").indexedSelf();
	}

	@Override
	public <JavaType> IndexedWhereBuilderImpl whereNotNull(AbstractJavaColumn<Table, JavaType, ?, ?> column) {
		return whereTupleNullOp(column, "!=").indexedSelf();
	}

	@Override
	public <JavaType> IndexedWhereBuilderImpl whereIndexedBetween(
			AbstractJavaColumn<Table, JavaType, AbstractJavaColumn.IsIndexed, AbstractJavaColumn.SingleBinder> column,
			JavaType minInclusive,
			JavaType maxInclusive)
	{
		return whereTupleRangeOp(column, "BETWEEN", minInclusive, maxInclusive).indexedSelf();
	}

	@Override
	public <JavaType> IndexedWhereBuilderImpl whereBetween(
			AbstractJavaColumn<Table, JavaType, ?, AbstractJavaColumn.SingleBinder> column,
			JavaType minInclusive,
			JavaType maxInclusive)
	{
		return whereTupleRangeOp(column, "BETWEEN", minInclusive, maxInclusive).indexedSelf();
	}

	@Override
	public <JavaType> IndexedWhereBuilderImpl whereIndexedNotBetween(
			AbstractJavaColumn<Table, JavaType, AbstractJavaColumn.IsIndexed, AbstractJavaColumn.SingleBinder> column,
			JavaType minInclusive,
			JavaType maxInclusive)
	{
		return whereTupleRangeOp(column, "NOT BETWEEN", minInclusive, maxInclusive).indexedSelf();
	}

	@Override
	public <JavaType> IndexedWhereBuilderImpl whereNotBetween(
			AbstractJavaColumn<Table, JavaType, ?, AbstractJavaColumn.SingleBinder> column,
			JavaType minInclusive,
			JavaType maxInclusive)
	{
		return whereTupleRangeOp(column, "NOT BETWEEN", minInclusive, maxInclusive).indexedSelf();
	}

	@Override
	public <JavaType> IndexedWhereBuilderImpl whereIndexedIn(
			AbstractJavaColumn<Table, JavaType, AbstractJavaColumn.IsIndexed, AbstractJavaColumn.SingleBinder> column,
			Collection<JavaType> value)
	{
		return whereTupleCollectionOp(column, "IN", value).indexedSelf();
	}

	@Override
	public <JavaType> IndexedWhereBuilderImpl whereIn(
			AbstractJavaColumn<Table, JavaType, ?, AbstractJavaColumn.SingleBinder> column, Collection<JavaType> value)
	{
		return whereTupleCollectionOp(column, "IN", value).indexedSelf();
	}

	@Override
	public <JavaType> IndexedWhereBuilderImpl whereIndexedNotIn(
			AbstractJavaColumn<Table, JavaType, AbstractJavaColumn.IsIndexed, AbstractJavaColumn.SingleBinder> column,
			Collection<JavaType> value)
	{
		return whereTupleCollectionOp(column, "NOT IN", value).indexedSelf();
	}

	@Override
	public <JavaType> IndexedWhereBuilderImpl whereNotIn(
			AbstractJavaColumn<Table, JavaType, ?, AbstractJavaColumn.SingleBinder> column, Collection<JavaType> value)
	{
		return whereTupleCollectionOp(column, "NOT IN", value).indexedSelf();
	}

	@Override
	public <JavaType> IndexedWhereBuilderImpl whereIndexedLike(
			AbstractJavaColumn<Table, JavaType, AbstractJavaColumn.IsIndexed, AbstractJavaColumn.SingleBinder> column, String value)
	{
		return whereTupleStringOp(column, "LIKE", value).indexedSelf();
	}

	@Override
	public <JavaType> IndexedWhereBuilderImpl whereLike(
			AbstractJavaColumn<Table, JavaType, ?, AbstractJavaColumn.SingleBinder> column, String value)
	{
		return whereTupleStringOp(column, "LIKE", value).indexedSelf();
	}

	@Override
	public <JavaType> IndexedWhereBuilderImpl whereIndexedNotLike(
			AbstractJavaColumn<Table, JavaType, AbstractJavaColumn.IsIndexed, AbstractJavaColumn.SingleBinder> column, String value)
	{
		return whereTupleStringOp(column, "NOT LIKE", value).indexedSelf();
	}

	@Override
	public <JavaType> IndexedWhereBuilderImpl whereNotLike(
			AbstractJavaColumn<Table, JavaType, ?, AbstractJavaColumn.SingleBinder> column, String value)
	{
		return whereTupleStringOp(column, "not LIKE", value).indexedSelf();
	}

	@Override
	public <JavaType> IndexedWhereBuilderImpl whereIndexedExpression(String expression)
	{
		whereExpression(expression);
		return indexedSelf();
	}

	@Override
	public <JavaType> IndexedWhereBuilderImpl whereExpression(String expression)
	{
		if (sb.size() > 0) {
			sb.add(" AND ");
		}
		sb.add("(");
		sb.add(expression);
		sb.add(")");
		return indexedSelf();
	}

	@Override
	public <JavaType> IndexedWhereBuilderImpl whereIndexedExpression(
			AbstractJavaColumn<Table, JavaType, AbstractJavaColumn.IsIndexed, AbstractJavaColumn.SingleBinder> column,
			String expressionBeforeValue,
			JavaType value,
			String expressionAfterValue)
	{
		whereExpression(column, expressionBeforeValue, value, expressionAfterValue);
		return indexedSelf();
	}

	@Override
	public <JavaType> IndexedWhereBuilderImpl whereExpression(
			AbstractJavaColumn<Table, JavaType, ?, AbstractJavaColumn.SingleBinder> column,
			String expressionBeforeValue,
			JavaType value,
			String expressionAfterValue)
	{
		int argIndex = addArgument(column, value)[0];
		if (sb.size() > 0) {
			sb.add(" AND ");
		}
		sb.add("(");
		sb.add(expressionBeforeValue);
		sb.add(argIndex);
		sb.add(expressionAfterValue);
		sb.add(")");
		return indexedSelf();
	}

	void appendOtherBuilderImpl(IndexedWhereBuilder<IndexedWhereBuilderImpl, Table> otherBuilderInterface)
	{
		@SuppressWarnings("unchecked") AbstractWhereBuilder<UnindexedWhereBuilderImpl, IndexedWhereBuilderImpl, Table>
				otherBuilder =
				(AbstractWhereBuilder<UnindexedWhereBuilderImpl, IndexedWhereBuilderImpl, Table>) otherBuilderInterface;
		if (sb.isEmpty()) {
			sb.addAll(otherBuilder.sb);
			argumentToIndexMap.putAll(otherBuilder.argumentToIndexMap);
			indexToArgumentMap.addAll(otherBuilder.indexToArgumentMap);
			return;
		}

		oldIndexToNewIndexMapCache.clear();
		for (int oldIndex = 0; oldIndex < otherBuilder.indexToArgumentMap.size(); oldIndex++) {
			String oldArg = otherBuilder.indexToArgumentMap.get(oldIndex);
			int newIndex = addArgument(oldArg);
			oldIndexToNewIndexMapCache.add(newIndex);
		}
		sb.add("(");
		for (int i = 0; i < otherBuilder.sb.size(); i++) {
			Object item = otherBuilder.sb.get(i);
			if (item instanceof Integer) {
				sb.add(Preconditions.checkNotNull(oldIndexToNewIndexMapCache.get((Integer) item)));
			} else {
				sb.add(item);
			}
		}
		sb.add(")");
	}

	@SuppressWarnings("unchecked")
	public IndexedWhereBuilderImpl whereAnyOf(
			Function<UnindexedWhereBuilder<IndexedWhereBuilderImpl, Table>,
					IndexedWhereBuilderImpl>... subBuilders)
	{
		if (sb.size() > 0) {
			sb.add(" AND ");
		}
		sb.add("(");
		for (int i = 0; i < subBuilders.length; i++) {
			if (sb.size() > 0) {
				sb.add(" OR ");
			}
			appendOtherBuilderImpl(subBuilders[i].apply(unindexedSelf()));
		}
		sb.add(")");
		return indexedSelf();
	}

	@Override
	public IndexedWhereBuilderImpl and(IndexedWhereBuilderImpl otherBuilder) {
		if (sb.size() > 0) {
			sb.add(" AND ");
		}
		appendOtherBuilderImpl(otherBuilder);
		return indexedSelf();
	}

	@Override
	public IndexedWhereBuilderImpl withSlowTableScan(String explanation) {
		return indexedSelf();
	}

	@Override
	public String[] getArgs() {
		return indexToArgumentMap.toArray(new String[0]);
	}

	@Override
	public String build() {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < sb.size(); i++) {
			Object item = sb.get(i);
			if (item instanceof Integer) {
				s.append("?").append(item);
			} else {
				s.append(item);
			}
		}
		return s.toString();
	}

	@NonNull
	@Override
	public String toString() {
		return build();
	}
}