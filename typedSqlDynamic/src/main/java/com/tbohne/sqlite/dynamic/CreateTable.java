package com.tbohne.sqlite.dynamic;

interface SyntaxTableCreate
		extends SyntaxTableTable
{
	SyntaxTableTable temporaryTable();

	SyntaxTableTable table();
}

interface SyntaxTableTable
		extends SyntaxTableIfNotExists
{
	SyntaxTableIfNotExists ifNotExists();
}

interface SyntaxTableIfNotExists {
	SyntaxTableName name(String tableName);

	SyntaxTableName name(String schema, String tableName);
}

interface SyntaxTableName {
	SyntaxTableComplete as(String selectStatement);

	SyntaxTableColumns columns(TableColumn[] columns);
}

interface SyntaxTableColumns
		extends SyntaxTableConstraints
{
	SyntaxTableConstraints constraints(TableConstraint[] constraints);
}

interface SyntaxTableConstraints
		extends SyntaxTableComplete
{
	SyntaxTableConstraints withoutRowId();

	SyntaxTableConstraints strict();
}

interface SyntaxTableComplete {
	String buildSql();
}

public class CreateTable
		implements SyntaxTableCreate,
							 SyntaxTableTable,
							 SyntaxTableIfNotExists,
							 SyntaxTableName,
							 SyntaxTableColumns,
							 SyntaxTableConstraints,
							 SyntaxTableComplete
{

	private final StringBuilder sb = new StringBuilder("CREATE ");
	private boolean defClosed = false;

	private CreateTable() {
	}

	public static SyntaxTableCreate create() {
		return new CreateTable();
	}

	public SyntaxTableTable temporaryTable() {
		sb.append("TEMP TABLE ");
		return this;
	}

	public SyntaxTableTable table() {
		sb.append("TEMP TABLE ");
		return this;
	}

	public SyntaxTableIfNotExists ifNotExists() {
		sb.append("IF NOT EXISTS ");
		return this;
	}

	public SyntaxTableName name(String tableName) {
		sb.append(tableName).append(' ');
		return this;
	}

	public SyntaxTableName name(String schema, String tableName) {
		sb.append(schema).append('.').append(tableName).append(' ');
		return this;
	}

	public SyntaxTableComplete as(String selectStatement) {
		sb.append(selectStatement);
		return this;
	}

	public SyntaxTableColumns columns(TableColumn[] columns) {
		for (int i = 0; i < columns.length; i++) {
			sb.append(i == 0 ? "(" : ", ").append(columns[i].buildSql());
		}
		return this;
	}

	public SyntaxTableConstraints constraints(TableConstraint[] constraints) {
		for (int i = 0; i < constraints.length; i++) {
			sb.append(i == 0 ? "(" : ", ").append(constraints[i].buildSql());
		}
		sb.append(')');
		defClosed = true;
		return this;
	}

	public SyntaxTableConstraints withoutRowId() {
		if (!defClosed) {
			sb.append(")");
		}
		sb.append(" WITHOUT ROW ID");
		return this;
	}

	public SyntaxTableConstraints strict() {
		if (!defClosed) {
			sb.append(")");
		}
		sb.append(" STRICT");
		return this;
	}

	public String buildSql() {
		if (!defClosed) {
			sb.append(")");
		}
		return sb.toString();
	}
}
