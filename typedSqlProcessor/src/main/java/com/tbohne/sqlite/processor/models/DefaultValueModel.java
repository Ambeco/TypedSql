package com.tbohne.sqlite.processor.models;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;
import com.tbohne.sqlite.annotations.createTable.DefaultValue;
import com.tbohne.sqlite.annotations.createTable.TableColumn;
import com.tbohne.sqlite.annotations.enums.Affinity;
import com.tbohne.sqlite.processor.util.Output;
import com.tbohne.sqlite.processor.util.StringHelpers;

import org.checkerframework.checker.nullness.qual.Nullable;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

/**
 * {@link DefaultValue}
 */
public class DefaultValueModel {
	public final @Nullable String constraintName;
	public final Affinity affinity;
	public final String rawValue;
	protected final @Nullable Long longValue;
	protected final @Nullable Double doubleValue;
	protected final @Nullable String stringvalue;
	protected final byte @Nullable [] blobValue;

	private DefaultValueModel(
			@Nullable String constraintName, String rawValue, @SuppressWarnings("NullableProblems") Long value)
	{
		this.constraintName = constraintName;
		this.affinity = Affinity.INTEGER;
		this.rawValue = rawValue;
		this.longValue = value;
		doubleValue = null;
		stringvalue = null;
		blobValue = null;
	}

	public DefaultValueModel(
			@Nullable String constraintName, String rawValue, @SuppressWarnings("NullableProblems") Double value)
	{
		this.constraintName = constraintName;
		this.affinity = Affinity.REAL;
		this.rawValue = rawValue;
		this.longValue = null;
		doubleValue = value;
		stringvalue = null;
		blobValue = null;
	}

	public DefaultValueModel(
			@Nullable String constraintName, String rawValue, @SuppressWarnings("NullableProblems") String value)
	{
		this.constraintName = constraintName;
		this.affinity = Affinity.TEXT;
		this.rawValue = rawValue;
		this.longValue = null;
		doubleValue = null;
		stringvalue = value;
		blobValue = null;
	}

	public DefaultValueModel(
			@Nullable String constraintName, String rawValue, @SuppressWarnings("NullableProblems") byte[] value)
	{
		this.constraintName = constraintName;
		this.affinity = Affinity.BLOB;
		this.rawValue = rawValue;
		this.longValue = null;
		doubleValue = null;
		stringvalue = null;
		blobValue = value;
	}

	public DefaultValueModel(
			@Nullable String constraintName, Affinity affinity, String rawValue)
	{
		this.constraintName = constraintName;
		this.affinity = affinity;
		this.rawValue = rawValue;
		this.longValue = null;
		doubleValue = null;
		stringvalue = null;
		blobValue = null;
	}

	public DefaultValueModel(
			@Nullable String constraintName, Affinity affinity, String rawValue, Exception exception)
	{
		this.constraintName = constraintName;
		this.affinity = affinity;
		this.rawValue = rawValue;
		this.longValue = null;
		doubleValue = null;
		stringvalue = null;
		blobValue = null;
	}

	public static @Nullable DefaultValueModel build(
			ProcessingEnvironment environment,
			@Nullable Output messager,
			TypeElement tableElement,
			String columnName,
			TableColumn column)
	{
		DefaultValue defaultValue = column.defaultValue();
		if (defaultValue.constraintName().isEmpty() && defaultValue.value().isEmpty()) {
			return null;
		}
		if (messager != null && !StringHelpers.validOrEmptySqlId(defaultValue.constraintName())) {
			messager.error(tableElement,
										 "Column %s DefaultValue has invalid constraint name \"%s\"",
										 columnName,
										 defaultValue.constraintName());
		}
		String name = Strings.emptyToNull(defaultValue.constraintName());
		if (defaultValue.value().isEmpty()) {
			if (messager != null) {
				messager.error(tableElement, "Column %s DefaultValue \"%s\" has has name but has no value", columnName, name);
			}
			return null;
		}
		if (column.affinity() != Affinity.BLOB && (defaultValue.value().equals("TRUE")
																							 || defaultValue.value()
																															.equals("FALSE")
																							 || defaultValue.value().equals("CURRENT_TIME")
																							 || defaultValue.value().equals("CURRENT_DATE")
																							 || defaultValue.value().equals("CURRENT_DATETIME")))
		{
			return new DefaultValueModel(name, column.affinity(), defaultValue.value());
		}
		//default value is an equation of some sort, so don't parse it
		if (defaultValue.value().contains("(")) {
			return new DefaultValueModel(name, defaultValue.value(), defaultValue.value());
		}
		switch (column.affinity()) {
			case INTEGER:
				try {
					Long result = Long.parseLong(defaultValue.value());
					return new DefaultValueModel(name, defaultValue.value(), result);
				} catch (NumberFormatException e) {
					if (messager != null) {
						messager.error(
								tableElement,
								"Column %s DefaultValue has invalid long \"%s\"",
								columnName,
								defaultValue.value());
					}
					return new DefaultValueModel(name, column.affinity(), defaultValue.value(), e);
				}
			case REAL:
				try {
					Double result = Double.parseDouble(defaultValue.value());
					return new DefaultValueModel(name, defaultValue.value(), result);
				} catch (NumberFormatException e) {
					if (messager != null) {
						messager.error(
								tableElement,
								"Column %s DefaultValue has invalid double \"%s\"",
								columnName,
								defaultValue.value());
					}
					return new DefaultValueModel(name, column.affinity(), defaultValue.value(), e);
				}
			case TEXT: {
				String result;
				if (defaultValue.value().charAt(0) != '\''
						|| defaultValue.value().charAt(defaultValue.value().length() - 1) != '\'')
				{
					if (messager != null) {
						messager.error(tableElement,
													 "Column %s DefaultValue has invalid String \"%s\" (should be wrapped in ' characters)",
													 columnName,
													 defaultValue.value());
					}
					result = defaultValue.value();
				} else {
					result = defaultValue.value().substring(1, defaultValue.value().length() - 1);
				}
				return new DefaultValueModel(name, defaultValue.value(), result);
			}
			case BLOB: {
				String value = defaultValue.value();
				if (value.equals("empty_blob()")) {
					return new DefaultValueModel(name, defaultValue.value(), new byte[]{});
				}
				if (value.length() < 4
						|| Character.toUpperCase(value.charAt(0)) != 'X'
						|| value.charAt(1) != '\''
						|| value.charAt(value.length() - 1) != '\''
						|| value.length() % 2 == 0 //each byte is two hex digits
				)
				{
					if (messager != null) {
						messager.error(
								tableElement,
								"Column %s DefaultValue has invalid Blob literal. It should have \"x'\" prefix and \"'\" suffix, and an even number of hex digits.",
								columnName);
					}
					return new DefaultValueModel(name, defaultValue.value(), new byte[]{});
				} else {
					try {
						byte[] result = BaseEncoding.base16().decode(value.substring(2, value.length() - 1));
						return new DefaultValueModel(name, defaultValue.value(), result);
					} catch (IllegalArgumentException e) {
						if (messager != null) {
							messager.error(tableElement,
														 "Column %s DefaultValue has invalid hex digits \"%s\"\n%s",
														 columnName,
														 defaultValue.value(),
														 e);
						}
						return new DefaultValueModel(name, column.affinity(), defaultValue.value(), e);
					}
				}
			}
			default:
				throw new IllegalStateException("Unknown storage class " + column.affinity());
		}
	}

	Long getLong() {
		return checkNotNull(longValue);
	}

	Double getDouble() {
		return checkNotNull(doubleValue);
	}

	String getString() {
		return checkNotNull(stringvalue);
	}

	byte[] getBlob() {
		return checkNotNull(blobValue);
	}
}
