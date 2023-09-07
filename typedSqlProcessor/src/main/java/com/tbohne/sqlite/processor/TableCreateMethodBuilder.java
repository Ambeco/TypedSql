package com.tbohne.sqlite.processor;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.tbohne.sqlite.annotations.enums.Collation;
import com.tbohne.sqlite.annotations.enums.ConflictAction;
import com.tbohne.sqlite.annotations.enums.ForeignKeyAction;
import com.tbohne.sqlite.annotations.enums.ForeignKeyDeferMode;
import com.tbohne.sqlite.annotations.enums.KeyOrder;
import com.tbohne.sqlite.processor.models.ColumnCheckModel;
import com.tbohne.sqlite.processor.models.ColumnCollationModel;
import com.tbohne.sqlite.processor.models.ColumnPrimaryKeyModel;
import com.tbohne.sqlite.processor.models.ColumnUniqueModel;
import com.tbohne.sqlite.processor.models.CreateTableModel;
import com.tbohne.sqlite.processor.models.DefaultValueModel;
import com.tbohne.sqlite.processor.models.ForeignKeyModel;
import com.tbohne.sqlite.processor.models.IndexedColumnModel;
import com.tbohne.sqlite.processor.models.NotNullModel;
import com.tbohne.sqlite.processor.models.TableCheckModel;
import com.tbohne.sqlite.processor.models.TableColumnModel;
import com.tbohne.sqlite.processor.models.TablePrimaryKeyModel;
import com.tbohne.sqlite.processor.models.TableUniqueModel;
import com.tbohne.sqlite.processor.util.Output;
import com.tbohne.sqlite.processor.util.StringHelpers;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;

public class TableCreateMethodBuilder {
	private final ProcessingEnvironment environment;
	private final Output messager;

	TableCreateMethodBuilder(ProcessingEnvironment environment, Output messager) {
		this.environment = environment;
		this.messager = messager;
	}

	public void addCreateTableMethod(TypeSpec.Builder classBuilder, CreateTableModel createTable) {
		MethodSpec.Builder
				builder =
				MethodSpec.methodBuilder("getCreateTable")
									.addModifiers(Modifier.PUBLIC)
									.addParameter(TypeName.BOOLEAN, "ifNotExists")
									.returns(String.class);
		builder.addCode(getCreateTableMethodBody(createTable));
		classBuilder.addMethod(builder.build());
	}

	private CodeBlock getCreateTableMethodBody(CreateTableModel createTable) {
		CodeBlock.Builder createTableString = CodeBlock.builder();
		createTableString.add("return \"CREATE");
		if (createTable.temporary) {
			createTableString.add(" TEMP");
		}
		createTableString.add(" TABLE \"\n").indent();
		createTableString.add("+ (ifNotExists ? \"IF NOT EXISTS \" : \"\")\n+ \"");
		if (createTable.schemaName != null) {
			createTableString.add(createTable.schemaName).add(".");
		}
		createTableString.add(createTable.sqlName).add(" (\"\n").indent().add("+ \"");
		appendColumnDefs(createTableString, createTable.columnList);
		appendTableConstraints(createTableString, createTable);
		createTableString.add("\"\n").unindent().add("+ \")");
		if (createTable.withoutRowId) {
			createTableString.add(" WITHOUT ROW ID");
		}
		return createTableString.add("\";").unindent().build();
	}

	private void appendColumnDefs(CodeBlock.Builder createTableString, ImmutableList<TableColumnModel> columns) {
		for (int i = 0; i < columns.size(); i++) {
			if (i > 0) {
				createTableString.add(", \"\n").add("+ \"");
			}
			TableColumnModel column = columns.get(i);
			createTableString.add(column.sqlName).add(" ").add(column.affinity.name());
			appendColumnPrimaryKey(createTableString, column);
			appendColumnNotNull(createTableString, column);
			appendColumnUnique(createTableString, column);
			appendColumnChecks(createTableString, column);
			appendColumnDefaultValue(createTableString, column);
			appendColumnCollation(createTableString, column);
			appendColumnForeignKey(createTableString, column);
		}
	}

	private void appendColumnPrimaryKey(CodeBlock.Builder createTableString, TableColumnModel column) {
		ColumnPrimaryKeyModel primaryKey = column.primaryKey;
		if (primaryKey == null) {
			return;
		}
		if (primaryKey.constraintName != null) {
			createTableString.add(" CONSTRAINT").add(primaryKey.constraintName);
		}
		createTableString.add(" PRIMARY KEY");
		if (primaryKey.order != KeyOrder.UNSPECIFIED) {
			createTableString.add(" ").add(primaryKey.order.name());
		}
		if (primaryKey.onConflict != ConflictAction.UNSPECIFIED) {
			createTableString.add(" ON CONFLICT ").add(primaryKey.onConflict.name());
		}
		if (primaryKey.autoIncrement) {
			createTableString.add(" AUTOINCREMENT");
		}
	}

	private void appendColumnNotNull(CodeBlock.Builder createTableString, TableColumnModel column) {
		NotNullModel notNull = column.notNull;
		if (notNull == null) {
			return;
		}
		if (notNull.constraintName != null) {
			createTableString.add(" CONSTRAINT ").add(notNull.constraintName);
		}
		createTableString.add(" NOT NULL");
		if (notNull.onConflict != ConflictAction.UNSPECIFIED) {
			createTableString.add(" ON CONFLICT ").add(notNull.onConflict.name());
		}
	}

	private void appendColumnUnique(CodeBlock.Builder createTableString, TableColumnModel column) {
		ColumnUniqueModel unique = column.unique;
		if (unique == null) {
			return;
		}
		if (unique.constraintName != null) {
			createTableString.add(" CONSTRAINT").add(unique.constraintName);
		}
		createTableString.add(" UNIQUE");
		if (unique.onConflict != ConflictAction.UNSPECIFIED) {
			createTableString.add(" ON CONFLICT ").add(unique.onConflict.name());
		}
	}

	private void appendColumnChecks(CodeBlock.Builder createTableString, TableColumnModel column) {
		ImmutableList<ColumnCheckModel> checks = column.checks;
		for (int i = 0; i < checks.size(); i++) {
			ColumnCheckModel check = checks.get(i);
			if (check.constraintName != null) {
				createTableString.add(" CONSTRAINT ").add(check.constraintName);
			}
			createTableString.add(" CHECK ").add(StringHelpers.sqlEscape(check.check));
		}
	}

	private void appendColumnDefaultValue(CodeBlock.Builder createTableString, TableColumnModel column) {
		DefaultValueModel defaultValue = column.defaultValue;
		if (defaultValue == null) {
			return;
		}
		if (defaultValue.constraintName != null) {
			createTableString.add(" CONSTRAINT ").add(defaultValue.constraintName);
		}
		createTableString.add(" DEFAULT ").add(defaultValue.rawValue);
	}

	private void appendColumnCollation(CodeBlock.Builder createTableString, TableColumnModel column) {
		ColumnCollationModel collation = column.collation;
		if (collation == null) {
			return;
		}
		if (collation.constraintName != null) {
			createTableString.add(" CONSTRAINT ").add(collation.constraintName);
		}
		createTableString.add(" COLLATE ").add(collation.collation.name());
	}

	private void appendColumnForeignKey(CodeBlock.Builder createTableString, TableColumnModel column) {
		ForeignKeyModel foreignKey = column.foreignKey;
		if (foreignKey == null) {
			return;
		}
		if (foreignKey.constraintName != null) {
			createTableString.add(" CONSTRAINT ").add(foreignKey.constraintName);
		}
		createTableString.add(" REFERENCES ").add(foreignKey.foreignTableName);
		List<String> foreignColumn = foreignKey.explicitForeignColumnNames;
		if (foreignColumn.size() > 1) {
			throw new IllegalStateException("too many columns in column foreign key");
		}
		if (foreignColumn.size() == 1) {
			createTableString.add(" (").add(foreignColumn.get(0)).add(")");
		}
		if (foreignKey.onUpdate != ForeignKeyAction.UNSPECIFIED) {
			createTableString.add(" ON UPDATE ").add(foreignKey.onUpdate.sql);
		}
		if (foreignKey.onDelete != ForeignKeyAction.UNSPECIFIED) {
			createTableString.add(" ON DELETE ").add(foreignKey.onDelete.sql);
		}
		if (foreignKey.deferred != ForeignKeyDeferMode.UNSPECIFIED) {
			createTableString.add(" ").add(foreignKey.deferred.sql);
		}
	}

	private void appendTableConstraints(CodeBlock.Builder createTableString, CreateTableModel createTable) {
		boolean hasConstraint = appendTablePrimaryKey(createTableString, createTable);
		hasConstraint = appendTableUnique(createTableString, hasConstraint, createTable);
		hasConstraint = appendTableChecks(createTableString, hasConstraint, createTable.checks);
		appendTableForeignKey(createTableString, hasConstraint, createTable);
	}

	private boolean appendTablePrimaryKey(CodeBlock.Builder createTableString, CreateTableModel createTable) {
		TablePrimaryKeyModel primaryKey = createTable.primaryKeyConstraint;
		if (primaryKey == null) {
			return false;
		}
		createTableString.add("\"\n").add("+ \"");
		if (primaryKey.constraintName != null) {
			createTableString.add(" CONSTRAINT").add(primaryKey.constraintName);
		}
		createTableString.add(" PRIMARY KEY");
		appendIndexedColumns(createTableString, primaryKey.columns);
		if (primaryKey.onConflict != ConflictAction.UNSPECIFIED) {
			createTableString.add(" ON CONFLICT ").add(primaryKey.onConflict.name());
		}
		createTableString.add(", ");
		return true;
	}

	private void appendIndexedColumns(CodeBlock.Builder createTableString, ImmutableList<IndexedColumnModel> columns) {
		createTableString.add(" (");
		for (int i = 0; i < columns.size(); i++) {
			if (i != 0) {
				createTableString.add(", ");
			}
			IndexedColumnModel column = columns.get(i);
			createTableString.add(column.sqlColumnName);
			if (column.collation != Collation.UNSPECIFIED) {
				createTableString.add(" ").add(column.collation.name());
			}
			if (column.keyOrder != KeyOrder.UNSPECIFIED) {
				createTableString.add(" ").add(column.keyOrder.name());
			}
		}
		createTableString.add(")");
	}

	private boolean appendTableUnique(
			CodeBlock.Builder createTableString, boolean hasConstraint, CreateTableModel createTable)
	{
		ImmutableList<TableUniqueModel> uniques = createTable.explicitUniques;
		if (uniques.isEmpty()) {
			return hasConstraint;
		}
		for (int i = 0; i < uniques.size(); i++) {
			if (hasConstraint) {
				createTableString.add(", ");
			}
			createTableString.add("\"\n").add("+ \"");
			TableUniqueModel unique = uniques.get(i);
			if (unique.constraintName != null) {
				createTableString.add(" CONSTRAINT").add(unique.constraintName);
			}
			createTableString.add(" UNIQUE");
			appendIndexedColumns(createTableString, unique.columnList);
			if (unique.onConflict != ConflictAction.UNSPECIFIED) {
				createTableString.add(" ON CONFLICT ").add(unique.onConflict.name());
			}
			hasConstraint = true;
		}
		return true;
	}

	private boolean appendTableChecks(
			CodeBlock.Builder createTableString, boolean hasConstraint, ImmutableList<TableCheckModel> checks)
	{
		if (checks.isEmpty()) {
			return hasConstraint;
		}
		for (int i = 0; i < checks.size(); i++) {
			if (hasConstraint) {
				createTableString.add(", ");
			}
			createTableString.add("\"\n").add("+ \"");
			TableCheckModel check = checks.get(i);
			if (check.constraintName != null) {
				createTableString.add(" CONSTRAINT").add(check.constraintName);
			}
			createTableString.add(" CHECK (").add(check.check).add(")");
			hasConstraint = true;
		}
		return true;
	}

	private void appendTableForeignKey(
			CodeBlock.Builder createTableString, boolean hasConstraint, CreateTableModel createTable)
	{
		ImmutableList<ForeignKeyModel> foreignKeys = createTable.foreignKeys;
		if (foreignKeys.isEmpty()) {
			return;
		}
		for (int i = 0; i < foreignKeys.size(); i++) {
			ForeignKeyModel foreignKey = createTable.foreignKeys.get(i);
			if (hasConstraint) {
				createTableString.add(", ");
			}
			createTableString.add("\"\n").add("+ \"");
			if (foreignKey.constraintName != null) {
				createTableString.add("CONSTRAINT ").add(foreignKey.constraintName);
			}
			createTableString.add("FOREIGN KEY (");
			for (int j = 0; j < foreignKey.localColumnNames.size(); j++) {
				if (j != 0) {
					createTableString.add(", ");
				}
				createTableString.add(foreignKey.localColumnNames.get(i));
			}
			createTableString.add(") REFERENCES ").add(foreignKey.foreignTableName);
			if (!foreignKey.explicitForeignColumnNames.isEmpty()) {
				createTableString.add("(");
				for (int j = 0; j < foreignKey.explicitForeignColumnNames.size(); j++) {
					if (j != 0) {
						createTableString.add(", ");
					}
					createTableString.add(foreignKey.explicitForeignColumnNames.get(j));
				}
				createTableString.add(")");
			}
			if (foreignKey.onUpdate != ForeignKeyAction.UNSPECIFIED) {
				createTableString.add(" ON UPDATE ").add(foreignKey.onUpdate.sql);
			}
			if (foreignKey.onDelete != ForeignKeyAction.UNSPECIFIED) {
				createTableString.add(" ON DELETE ").add(foreignKey.onDelete.sql);
			}
			if (foreignKey.deferred != ForeignKeyDeferMode.UNSPECIFIED) {
				createTableString.add(" ").add(foreignKey.deferred.sql);
			}
			hasConstraint = true;
		}
	}
}
