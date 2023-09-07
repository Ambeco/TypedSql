package com.tbohne.sqlite.generalProjections;

import com.tbohne.sqlite.annotations.CreateProjection;
import com.tbohne.sqlite.annotations.enums.Affinity;
import com.tbohne.sqlite.annotations.query.ProjectionExpression;
import com.tbohne.sqlite.binders.BooleanNonNullBinder;

/**
 * {@link BooleanNonNullProjectionSql}
 */
@CreateProjection(expressions = @ProjectionExpression(name = "value",
																											affinity = Affinity.INTEGER,
																											binder = BooleanNonNullBinder.class,
																											notNull = true))
public class BooleanNonNullProjection {
}
