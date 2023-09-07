package com.tbohne.sqlite.generalProjections;

import com.tbohne.sqlite.annotations.CreateProjection;
import com.tbohne.sqlite.annotations.enums.Affinity;
import com.tbohne.sqlite.annotations.query.ProjectionExpression;
import com.tbohne.sqlite.binders.RowIdBinder;
import com.tbohne.sqlite.binders.RowIdNonNullBinder;

/**
 * {@link RowIdNonNullProjectionSql}
 */
@CreateProjection(expressions = @ProjectionExpression(name = "value", affinity = Affinity.INTEGER, binder =
		RowIdNonNullBinder.class, notNull = true))
public class RowIdNonNullProjection<Table> {
}
