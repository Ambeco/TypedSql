package com.tbohne.sqlite.generalProjections;

import com.tbohne.sqlite.annotations.CreateProjection;
import com.tbohne.sqlite.annotations.enums.Affinity;
import com.tbohne.sqlite.annotations.query.ProjectionExpression;
import com.tbohne.sqlite.binders.RowIdBinder;

/**
 * {@link RowIdProjectionSql}
 */
@CreateProjection(expressions = @ProjectionExpression(name = "value", affinity = Affinity.INTEGER, binder =
		RowIdBinder.class))
public class RowIdProjection<Table> {
}
