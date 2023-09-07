package com.tbohne.sqlite.processor.util;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Pattern;

public class StringHelpers {
	private static final Pattern
			SQL_ID_REGEX =
			Pattern.compile("[\\p{L}_][\\p{L}\\p{Nd}_#@$]*", Pattern.UNICODE_CHARACTER_CLASS);
	private static final HashSet<String> SQL_KEYWORDS = new HashSet<>(Arrays.asList("ABORT",
																																									"ACTION",
																																									"ADD",
																																									"AFTER",
																																									"ALL",
																																									"ALTER",
																																									"ALWAYS",
																																									"ANALYZE",
																																									"AND",
																																									"AS",
																																									"ASC",
																																									"ATTACH",
																																									"AUTOINCREMENT",
																																									"BEFORE",
																																									"BEGIN",
																																									"BETWEEN",
																																									"BY",
																																									"CASCADE",
																																									"CASE",
																																									"CAST",
																																									"CHECK",
																																									"COLLATE",
																																									"COLUMN",
																																									"COMMIT",
																																									"CONFLICT",
																																									"CONSTRAINT",
																																									"CREATE",
																																									"CROSS",
																																									"CURRENT",
																																									"CURRENT_DATE",
																																									"CURRENT_TIME",
																																									"CURRENT_TIMESTAMP",
																																									"DATABASE",
																																									"DEFAULT",
																																									"DEFERRABLE",
																																									"DEFERRED",
																																									"DELETE",
																																									"DESC",
																																									"DETACH",
																																									"DISTINCT",
																																									"DO",
																																									"DROP",
																																									"EACH",
																																									"ELSE",
																																									"END",
																																									"ESCAPE",
																																									"EXCEPT",
																																									"EXCLUDE",
																																									"EXCLUSIVE",
																																									"EXISTS",
																																									"EXPLAIN",
																																									"FAIL",
																																									"FILTER",
																																									"FIRST",
																																									"FOLLOWING",
																																									"FOR",
																																									"FOREIGN",
																																									"FROM",
																																									"FULL",
																																									"GENERATED",
																																									"GLOB",
																																									"GROUP",
																																									"GROUPS",
																																									"HAVING",
																																									"IF",
																																									"IGNORE",
																																									"IMMEDIATE",
																																									"IN",
																																									"INDEX",
																																									"INDEXED",
																																									"INITIALLY",
																																									"INNER",
																																									"INSERT",
																																									"INSTEAD",
																																									"INTERSECT",
																																									"INTO",
																																									"IS",
																																									"ISNULL",
																																									"JOIN",
																																									"KEY",
																																									"LAST",
																																									"LEFT",
																																									"LIKE",
																																									"LIMIT",
																																									"MATCH",
																																									"MATERIALIZED",
																																									"NATURAL",
																																									"NO",
																																									"NOT",
																																									"NOTHING",
																																									"NOTNULL",
																																									"NULL",
																																									"NULLS",
																																									"OF",
																																									"OFFSET",
																																									"ON",
																																									"OR",
																																									"ORDER",
																																									"OTHERS",
																																									"OUTER",
																																									"OVER",
																																									"PARTITION",
																																									"PLAN",
																																									"PRAGMA",
																																									"PRECEDING",
																																									"PRIMARY",
																																									"QUERY",
																																									"RAISE",
																																									"RANGE",
																																									"RECURSIVE",
																																									"REFERENCES",
																																									"REGEXP",
																																									"REINDEX",
																																									"RELEASE",
																																									"RENAME",
																																									"REPLACE",
																																									"RESTRICT",
																																									"RETURNING",
																																									"RIGHT",
																																									"ROLLBACK",
																																									"ROW",
																																									"ROWS",
																																									"SAVEPOINT",
																																									"SELECT",
																																									"SET",
																																									"TABLE",
																																									"TEMP",
																																									"TEMPORARY",
																																									"THEN",
																																									"TIES",
																																									"TO",
																																									"TRANSACTION",
																																									"TRIGGER",
																																									"UNBOUNDED",
																																									"UNION",
																																									"UNIQUE",
																																									"UPDATE",
																																									"USING",
																																									"VACUUM",
																																									"VALUES",
																																									"VIEW",
																																									"VIRTUAL",
																																									"WHEN",
																																									"WHERE",
																																									"WINDOW",
																																									"WITH",
																																									"WITHOUT"));
	private static final Pattern
			JAVA_ID_REGEX =
			Pattern.compile("[\\p{Alpha}_$][\\p{Alnum}_$]*", Pattern.UNICODE_CHARACTER_CLASS);
	private static final HashSet<String> JAVA_KEYWORDS = new HashSet<>(Arrays.asList("ABSTRACT",
																																									 "ASSERT",
																																									 "BOOLEAN",
																																									 "BREAK",
																																									 "BYTE",
																																									 "CASE",
																																									 "CATCH",
																																									 "CHAR",
																																									 "CLASS",
																																									 "CONST",
																																									 "CONTINUE",
																																									 "DEFAULT",
																																									 "DO",
																																									 "DOUBLE",
																																									 "ELSE",
																																									 "ENUM",
																																									 "EXTENDS",
																																									 "FINAL",
																																									 "FINALLY",
																																									 "FLOAT",
																																									 "FOR",
																																									 "GOTO",
																																									 "IF",
																																									 "IMPLEMENTS",
																																									 "IMPORT",
																																									 "INSTANCEOF",
																																									 "INT",
																																									 "INTERFACE",
																																									 "LONG",
																																									 "NATIVE",
																																									 "NEW",
																																									 "PACKAGE",
																																									 "PRIVATE",
																																									 "PROTECTED",
																																									 "PUBLIC",
																																									 "RETURN",
																																									 "SHORT",
																																									 "STATIC",
																																									 "STRICTFP",
																																									 "SUPER",
																																									 "SWITCH",
																																									 "SYNCHRONIZED",
																																									 "THIS",
																																									 "THROW",
																																									 "THROWS",
																																									 "TRANSIENT",
																																									 "TRY",
																																									 "VOID",
																																									 "VOLATILE",
																																									 "WHILE"));

	private StringHelpers() {
	}

	public static boolean validSqlId(String s) {
		if (!SQL_ID_REGEX.matcher(s).matches()) {
			return false;
		}
		if (SQL_KEYWORDS.contains(s.toUpperCase(Locale.ROOT))) {
			return false;
		}
		if (s.getBytes(StandardCharsets.UTF_8).length >= 256) {
			return false;
		}
		return true;
	}

	public static boolean validOrEmptySqlId(String s) {
		if (s.length() == 0) {
			return true;
		}
		return validSqlId(s);
	}

	public static boolean validJavaId(String s) {
		if (!JAVA_ID_REGEX.matcher(s).matches()) {
			return false;
		}
		if (JAVA_KEYWORDS.contains(s.toUpperCase(Locale.ROOT))) {
			return false;
		}
		return true;
	}

	public static boolean validOrEmptyJavaId(String s) {
		if (s.length() == 0) {
			return true;
		}
		return validJavaId(s);
	}

	public static String sqlEscape(String s) {
		return s.replace("'", "''");
	}

	public static String sqlToJavaSuffix(String sql) {
		StringBuilder java = new StringBuilder();
		for (int i = 0; i < sql.length(); i++) {
			char c = sql.charAt(i);
			if (c == '_' && i + 1 < sql.length()) {
				java.append(Character.toUpperCase(sql.charAt(i + 1)));
				i++;
			} else if (c == '_' || c == '#' || c == '@') {
				java.append('_');
			} else if (i == 0) {
				java.append(Character.toUpperCase(c));
			} else {
				java.append(c);
			}
		}
		return java.toString();
	}

	public static String sqlToJava(String sql) {
		StringBuilder java = new StringBuilder();
		for (int i = 0; i < sql.length(); i++) {
			char c = sql.charAt(i);
			if (c == '_' && i + 1 < sql.length()) {
				java.append(Character.toUpperCase(sql.charAt(i + 1)));
				i++;
			} else if (c == '_' || c == '#' || c == '@') {
				java.append('_');
			} else if (i == 0) {
				java.append(Character.toLowerCase(c));
			} else {
				java.append(c);
			}
		}
		return java.toString();
	}

	public static String javaToSql(String java) {
		StringBuilder sql = new StringBuilder();
		for (int i = 0; i < java.length(); i++) {
			char c = java.charAt(i);
			if (Character.isUpperCase(c)) {
				sql.append('_').append(Character.toLowerCase(c));
			} else {
				sql.append(c);
			}
		}
		return sql.toString();
	}
}
