package com.tbohne.sqlite.bound.where;

public interface CompleteWhereBuilder<Table> {

	String[] getArgs();

	String build();
}