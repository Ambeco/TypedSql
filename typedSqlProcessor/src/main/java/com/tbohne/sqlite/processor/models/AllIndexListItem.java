package com.tbohne.sqlite.processor.models;

import com.google.common.collect.ImmutableList;

public class AllIndexListItem {
	public final boolean unique;
	public final TableColumnModel primaryColumn;
	public final ImmutableList<TableColumnModel> columns;

	public AllIndexListItem(boolean unique, TableColumnModel primaryColumn, ImmutableList<TableColumnModel> columns) {
		this.unique = unique;
		this.primaryColumn = primaryColumn;
		this.columns = columns;
		if (columns.size() < 1) {
			throw new IllegalStateException("columns list is empty");
		}
		if (columns.get(0) != primaryColumn) {
			throw new IllegalStateException("first entry in columns must be primary");
		}
	}
}
