package com.tbohne.sqlite.dynamic;

import com.tbohne.sqlite.annotations.enums.Compound;
import com.tbohne.sqlite.annotations.enums.Distinct;
import com.tbohne.sqlite.annotations.enums.Recursive;

interface SyntaxSelectWithRecursive {
	SyntaxSelectSelected thenSelect(Distinct distinct, SelectResultColumn[] columns);

	StrictSelect thenValues(String[][] columns);
}

interface SyntaxSelectSelected
		extends SyntaxSelectFrom
{
	SyntaxSelectFrom from(SelectTable[] tables);

	SyntaxSelectFrom fromJoin(SelectTable table, SelectJoin[] joins);
}

interface SyntaxSelectFrom
		extends SyntaxSelectWhere
{
	SyntaxSelectWhere where(String expression);
}

interface SyntaxSelectWhere
		extends SyntaxSelectGroupBy
{
	SyntaxSelectGroupBy groupBy(String[] expressions);

	SyntaxSelectGroupBy groupBy(String[] expressions, String having);
}

interface SyntaxSelectGroupBy
		extends StrictSelect
{
	StrictSelect window(SelectWindow[] windows);
}

interface StrictSelect
		extends SyntaxCompound
{
	SyntaxCompound compound(Compound compound, StrictSelect selection);
}

interface SyntaxCompound
		extends SyntaxSelectOrderBy
{
	SyntaxSelectOrderBy orderBy(SelectOrderBy[] orderBys);
}

interface SyntaxSelectOrderBy
		extends SyntaxSelectComplete
{
	SyntaxSelectComplete limit(String expression);

	SyntaxSelectComplete limitOffset(String expression, String offset);

	SyntaxSelectComplete limitExpression(String expression, String offset);
}

interface SyntaxSelectComplete {
	String buildSql();
}

public class Select
		implements SyntaxSelectWithRecursive,
							 SyntaxSelectSelected,
							 SyntaxSelectFrom,
							 SyntaxSelectWhere,
							 SyntaxSelectGroupBy,
							 StrictSelect,
							 SyntaxCompound,
							 SyntaxSelectOrderBy,
							 SyntaxSelectComplete
{
	private final StringBuilder sb = new StringBuilder();

	private Select() {
	}

	public static SyntaxSelectWithRecursive with(Recursive recursive, SelectCommonTable[] selectCommons) {
		if (selectCommons.length == 0) {
			throw new IllegalArgumentException("no selectCommons passed in");
		}
		Select select = new Select();
		select.sb.append("WITH ");
		if (recursive == Recursive.RECURSIVE) {
			select.sb.append("RECURSIVE ");
		}
		for (int i = 0; i < selectCommons.length; i++) {
			select.sb.append(i > 0 ? ", " : " ").append(selectCommons[i].buildSql());
		}
		return select;
	}

	public static SyntaxSelectSelected select(Distinct distinct, SelectResultColumn[] columns) {
		Select select = new Select();
		return select.thenSelect(distinct, columns);
	}

	public static StrictSelect values(String[][] expressions) {
		Select select = new Select();
		return select.thenValues(expressions);
	}

	public String buildSql() {
		return sb.toString();
	}

	public StrictSelect thenValues(String[][] expressions) {
		if (expressions.length == 0) {
			throw new IllegalArgumentException("no expressions passed in");
		}
		sb.append("VALUES");
		for (int i = 0; i < expressions.length; i++) {
			if (expressions[i].length == 0) {
				throw new IllegalArgumentException("expression set " + i + " is empty");
			}
			sb.append(i > 0 ? "), " : " (");
			for (int j = 0; j < expressions[i].length; j++) {
				if (expressions[i][j].isEmpty()) {
					throw new IllegalArgumentException("expression[" + i + "][" + j + "] is empty");
				}
				sb.append(i > 0 ? ", " : "").append(expressions[i][j]);
			}
		}
		sb.append(')');
		return this;
	}

	public SyntaxSelectSelected thenSelect(Distinct distinct, SelectResultColumn[] columns) {
		if (columns.length == 0) {
			throw new IllegalArgumentException("no columns passed in");
		}
		sb.append(" SELECT");
		if (distinct != Distinct.UNSPECIFIED) {
			sb.append(distinct).append(' ');
		}
		for (int i = 0; i < columns.length; i++) {
			sb.append(i > 0 ? ", " : " ").append(columns[i].buildSql());
		}
		return this;
	}

	@Override
	public SyntaxSelectFrom from(SelectTable[] tables) {
		if (tables.length == 0) {
			throw new IllegalArgumentException("no tables passed in");
		}
		sb.append(" FROM");
		for (int i = 0; i < tables.length; i++) {
			sb.append(i > 0 ? ", " : " ").append(tables[i].buildSql());
		}
		return this;
	}

	@Override
	public SyntaxSelectFrom fromJoin(SelectTable table, SelectJoin[] joins) {
		if (joins.length == 0) {
			throw new IllegalArgumentException("no joins passed in");
		}
		sb.append(" FROM ").append(table);
		for (int i = 0; i < joins.length; i++) {
			SelectJoin join = joins[i];
			sb.append(' ').append(join.buildSql());
		}
		return this;
	}

	@Override
	public SyntaxSelectWhere where(String expression) {
		sb.append(" WHERE ").append(expression);
		return this;
	}

	@Override
	public SyntaxSelectGroupBy groupBy(String[] expressions) {
		if (expressions.length == 0) {
			throw new IllegalArgumentException("no expressions passed in");
		}
		sb.append(" GROUP BY");
		for (int i = 0; i < expressions.length; i++) {
			sb.append(i > 0 ? ", " : " ").append(expressions[i]);
		}
		return this;
	}

	@Override
	public SyntaxSelectGroupBy groupBy(String[] expressions, String having) {
		groupBy(expressions);
		sb.append(" HAVING ").append(having);
		return this;
	}

	@Override
	public StrictSelect window(SelectWindow[] windows) {
		if (windows.length == 0) {
			throw new IllegalArgumentException("no windows passed in");
		}
		sb.append(" WINDOW");
		for (int i = 0; i < windows.length; i++) {
			SelectWindow window = windows[i];
			sb.append(i > 0 ? ", " : " ").append(window.buildSql());
		}
		return this;
	}

	@Override
	public SyntaxCompound compound(Compound compound, StrictSelect selection) {
		if (compound == Compound.UNSPECIFIED) {
			throw new IllegalArgumentException(compound.name());
		}
		sb.append(' ').append(compound.sql).append(' ').append(selection.buildSql());
		return this;
	}

	@Override
	public SyntaxSelectOrderBy orderBy(SelectOrderBy[] orderBys) {
		if (orderBys.length == 0) {
			throw new IllegalArgumentException("no orderBys passed in");
		}
		sb.append(" ORDER BY");
		for (int i = 0; i < orderBys.length; i++) {
			sb.append(i > 0 ? ", " : " ").append(orderBys[i].buildSql());
		}
		return this;
	}

	@Override
	public SyntaxSelectComplete limit(String expression) {
		sb.append(" LIMIT ").append(expression);
		return this;
	}

	@Override
	public SyntaxSelectComplete limitOffset(String expression, String offset) {
		sb.append(" LIMIT ").append(expression).append(" OFFSET ").append(offset);
		return this;
	}

	@Override
	public SyntaxSelectComplete limitExpression(String expression, String offset) {
		sb.append(" LIMIT ").append(expression).append(", ").append(offset);
		return this;
	}
}
