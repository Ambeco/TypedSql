package com.tbohne.sqlite.annotations;

import com.tbohne.sqlite.annotations.query.QueryCompound;
import com.tbohne.sqlite.annotations.query.QueryLimit;
import com.tbohne.sqlite.annotations.query.QueryOrdering;
import com.tbohne.sqlite.annotations.query.SelectWindow;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A full SQLite query for the database
 * <p>
 * Queries without runtime values can be done via this annotation, but most queries will have
 * runtime values and so will use @{link com.tbohne.sqlite.dynamic.Select} instead.
 * <p>
 * As an example, this defines a MessageTable, and a PhoneNumberTable, and then a Projection and
 * Selection that joins these based on the sender id, and a Query that rows where the message was
 * "Hello":
 *
 * <pre>
 * {@code
 * @CreateTable(columns = {
 *     TableColumn(name="id", affinity=Affinity.INTEGER, primaryKey=ColumnPrimaryKey()),
 *     TableColumn(name="number", affinity=Affinity.STRING, unique=ColumnUnique()),
 * })
 * interface PhoneNumberTable {}
 * @CreateTable(columns = {
 *     TableColumn(name="id", affinity=Affinity.INTEGER, primaryKey=ColumnPrimaryKey()),
 *     TableColumn(name="text", affinity=Affinity.STRING),
 *     TableColumn(name="sender",
 *          affinity=Affinity.INTEGER,
 *          foreignKey=ColumnForeignKey(PhoneNumberTable.class)),
 * })
 * interface MessageTable {}
 *
 * @CreateProjection(tables = {ProjectionTable(SampleTable.class), ProjectionTable(MessageTable.class)})
 * interface MessageWithSenderProjection {}
 *
 * @Selection(
 *      select=SelectColumns(projection=SampleProjection.class),
 *      fromTable=SampleTable.class,
 *      joins={SelectJoin(
 *          operator=JoinOperator.LEFT,
 *          fromTable=SelectTable(MessageTable.class),
 *          usingColumn={"sender"})})
 * interface MessageWithSenderSelection {}
 *
 * @Query(selection=MessageWithSenderSelection.class, having="text=\"Hello\"")
 * interface HelloMessagesWithSender {}
 *
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Query {
	String name() default ""; //defaults to the name of the annotated class + "Query"

	Class<?> selection(); //should be a @CreateTable, @CreateIndex, @CreateView, @CreateProjection, or @Selection.

	String where() default ""; //this is ANDed with the Selection

	String having() default ""; //this is ANDed with the Selection

	SelectWindow[] windows() default {};

	QueryCompound[] compoundWith() default {};

	QueryOrdering orderBy() default @QueryOrdering("");

	QueryLimit limit() default @QueryLimit("");
}
