package com.tbohne.sqlite.generalProjections;

import com.tbohne.sqlite.annotations.CreateProjection;
import com.tbohne.sqlite.annotations.enums.Affinity;
import com.tbohne.sqlite.annotations.query.ProjectionExpression;

/**
 * {@link DoubleProjectionSql}
 */
@CreateProjection(expressions = @ProjectionExpression(name = "value", affinity = Affinity.REAL))
public class DoubleProjection {
}
