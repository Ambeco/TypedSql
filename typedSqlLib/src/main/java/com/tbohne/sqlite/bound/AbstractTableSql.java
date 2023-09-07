package com.tbohne.sqlite.bound;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.ArraySet;

import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.FluentFuture;
import com.google.errorprone.annotations.CheckReturnValue;
import com.google.errorprone.annotations.MustBeClosed;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.inject.Provider;

public class AbstractTableSql<TransactionId> {
	protected final Provider<TransactionListeners<TransactionId>> transactionListeners;
	protected final Executor databaseQueryExecutor;
	protected final Executor binderExecutor;

	protected final Supplier<SQLiteDatabase> databaseSync;
	protected final Supplier<FluentFuture<SQLiteDatabase>> databaseAsync;

	protected AbstractTableSql(
			Provider<TransactionListeners<TransactionId>> transactionListeners,
			Executor databaseQueryExecutor,
			Executor binderExecutor,
			Supplier<SQLiteDatabase> databaseSync,
			Supplier<FluentFuture<SQLiteDatabase>> databaseAsync) {
		this.transactionListeners = transactionListeners;
		this.databaseQueryExecutor = databaseQueryExecutor;
		this.binderExecutor = binderExecutor;
		this.databaseSync = databaseSync;
		this.databaseAsync = databaseAsync;
	}

	interface OperationCallback<TransactionId, Result> {
		void closeWithSuccess(TransactionId transactionId, Result result);

		void closeWithException(TransactionId transactionId, RuntimeException exception);
	}

	@CheckReturnValue
	public static <TransactionId, Result, ExceptionType extends RuntimeException> ExceptionType failCallback(
			ExceptionType exception, OperationCallback<TransactionId, Result> callback, TransactionId transactionId)
	{
		try {
			callback.closeWithException(transactionId, exception);
		} catch (Exception closeException) {
			exception.addSuppressed(closeException);
		}
		return exception;
	}

	@CheckReturnValue
	public static <TransactionId, Result, ExceptionType extends RuntimeException> ExceptionType failCallbacks(
			ExceptionType exception,
			@Nullable Iterable<? extends OperationCallback<TransactionId, Result>> callbacks, TransactionId transactionId)
	{
		if (callbacks == null)
			return exception;
		for (OperationCallback<TransactionId, Result> callback : callbacks) {
			try {
				callback.closeWithException(transactionId, exception);
			} catch (Exception closableException) {
				exception.addSuppressed(closableException);
			}
		}
		return exception;
	}

	public static class ClosableSetThrewExceptionException
			extends RuntimeException
	{
		public ClosableSetThrewExceptionException(Throwable cause) {
			super(cause);
		}
	}

	public static <TransactionId, Result> void succeedCallbacks(
			Result value, @Nullable Iterable<OperationCallback<TransactionId, Result>> callbacks, TransactionId transactionId)
			throws ClosableSetThrewExceptionException
	{
		if (callbacks == null)
			return;
		ClosableSetThrewExceptionException closableSetException = null;
		for (OperationCallback<TransactionId, Result> callback : callbacks) {
			try {
				callback.closeWithSuccess(transactionId, value);
			} catch (Exception closableException) {
				if (closableSetException == null)
					closableSetException = new ClosableSetThrewExceptionException(closableException);
				else
					closableSetException.addSuppressed(closableException);
			}
		}
		if (closableSetException != null)
			throw closableSetException;
	}

	static <TransactionId, Result, Input, Output extends OperationCallback<TransactionId, Result>> @Nullable Set<Output> getClosableForEachItemInSet(
			@Nullable Set<Input> set, Function<Input, Output> function, TransactionId transactionId)
	{
		if (set == null)
			return new HashSet<>();
		Set<Output> closeSet = null;
		try {
			for (Input item : set) {
				Output closable = function.apply(item);
				try {
					if (closeSet == null)
						closeSet = newMutableSet(set.size());
					closeSet.add(closable);
				} catch (RuntimeException newAddSetException) {
					throw failCallback(newAddSetException, closable, transactionId);
				}
			}
		} catch (RuntimeException listenerException) {
			throw failCallbacks(listenerException, closeSet, transactionId);
		}
		return closeSet;
	}

	private static <T> Set<T> newMutableSet(int size) {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
			return new ArraySet<>(size);
		} else {
			return new HashSet<>(size * 2);
		}
	}

	interface BeginTransactionListener<TransactionId> {
		@MonotonicNonNull OperationCallback<TransactionId, @Nullable Void> beginTransaction(TransactionId transactionId);
	}

	interface EndTransactionListener<TransactionId> {
		@MonotonicNonNull OperationCallback<TransactionId, @Nullable Void> endTransaction(TransactionId transactionId);
	}

	interface OutsideTransactionListener<TransactionId> {
		@MonotonicNonNull OperationCallback<TransactionId, @Nullable Void> outsideTransaction(TransactionId transactionId);
	}

	interface InsideTransactionListener<TransactionId> {
		@MonotonicNonNull OperationCallback<TransactionId, @Nullable Void> insideTransaction(TransactionId transactionId);
	}

	public static class TransactionListeners<TransactionId> {
		private final @Nullable ImmutableSet<BeginTransactionListener<TransactionId>> beginTransactionListeners;
		private final @Nullable ImmutableSet<EndTransactionListener<TransactionId>> endTransactionListeners;
		private final @Nullable ImmutableSet<OutsideTransactionListener<TransactionId>> outsideTransactionListeners;
		private final @Nullable ImmutableSet<InsideTransactionListener<TransactionId>> insideTransactionListeners;

		public TransactionListeners(
				@Nullable ImmutableSet<BeginTransactionListener<TransactionId>> beginTransactionListeners,
				@Nullable ImmutableSet<EndTransactionListener<TransactionId>> endTransactionListeners,
				@Nullable ImmutableSet<OutsideTransactionListener<TransactionId>> outsideTransactionListeners,
				@Nullable ImmutableSet<InsideTransactionListener<TransactionId>> insideTransactionListeners)
		{
			this.beginTransactionListeners = beginTransactionListeners;
			this.endTransactionListeners = endTransactionListeners;
			this.outsideTransactionListeners = outsideTransactionListeners;
			this.insideTransactionListeners = insideTransactionListeners;
		}
	}

	public static class Transaction<TransactionId>
			implements AutoCloseable
	{
		private static final long NO_THREAD_HAS_TRANSACTION = 0;
		private static final AtomicLong transactionThreadId = new AtomicLong(NO_THREAD_HAS_TRANSACTION);

		private final SQLiteDatabase db;
		private final TransactionListeners<TransactionId> transactionListeners;

		private final TransactionId transactionId;
		private final long threadId;
		private final boolean isOutermostTransaction;
		private @MonotonicNonNull Set<OperationCallback<TransactionId, @Nullable Void>> outsideTransactionClosables;
		private @MonotonicNonNull Set<OperationCallback<TransactionId, @Nullable Void>> insideTransactionClosables;
		private boolean successful = false;
		private RuntimeException failedException = null;

		@MustBeClosed
		public Transaction(
				SQLiteDatabase db, TransactionListeners<TransactionId> transactionListeners, TransactionId transactionId)
		{
			this.db = db;
			this.transactionListeners = transactionListeners;
			this.threadId = Thread.currentThread().getId();
			this.transactionId = transactionId;
			this.outsideTransactionClosables = null;
			this.insideTransactionClosables = null;
			if (transactionThreadId.get() == threadId) {
				isOutermostTransaction = false;
			} else {
				isOutermostTransaction = true;
				try {
					this.outsideTransactionClosables = getClosableForEachItemInSet(
							transactionListeners.outsideTransactionListeners,
							(listener) -> listener.outsideTransaction(transactionId),
							transactionId);
					beginTransaction();
					this.insideTransactionClosables = getClosableForEachItemInSet(
							transactionListeners.insideTransactionListeners,
							(listener) -> listener.insideTransaction(transactionId),
							transactionId);
				} catch (RuntimeException e) {
					RuntimeException e2 = failCallbacks(e, insideTransactionClosables, transactionId);
					throw failCallbacks(e2, outsideTransactionClosables, transactionId);
				}
			}
		}

		public void setTransactionSuccessful() {
			successful = true;
			db.setTransactionSuccessful();
		}

		public void setTransactionFailed(RuntimeException e) {
			failedException = e;
		}

		private void beginTransaction() {
			Set<OperationCallback<TransactionId, @Nullable Void>> callbacks = getClosableForEachItemInSet(
					transactionListeners.beginTransactionListeners,
					(listener) -> listener.beginTransaction(transactionId),
					transactionId);
			try {
				db.beginTransaction();
				transactionThreadId.set(threadId);
			} catch (RuntimeException e) {
				throw failCallbacks(e, callbacks, transactionId);
			}
			succeedCallbacks(null, callbacks, transactionId);
		}

		private void endTransaction() {
			Set<OperationCallback<TransactionId, @Nullable Void>> callbacks = getClosableForEachItemInSet(
					transactionListeners.endTransactionListeners,
					(listener) -> listener.endTransaction(transactionId),
					transactionId);
			try {
				transactionThreadId.set(NO_THREAD_HAS_TRANSACTION);
				db.endTransaction();
			} catch (RuntimeException e) {
				throw failCallbacks(e, callbacks, transactionId);
			}
			succeedCallbacks(null, callbacks, transactionId);
		}

		@Override
		public void close() throws Exception {
			RuntimeException exception = failedException;
			if (!successful && exception == null) {
				exception = new SQLiteException("Transaction " + transactionId + " aborting because setTransactionSuccessful not called");
			}
			try {
				if (failedException == null)
					succeedCallbacks(null, insideTransactionClosables, transactionId);
				else
					exception = failCallbacks(failedException, insideTransactionClosables, transactionId);
			} catch (RuntimeException insideTransactionException) {
				if (exception == null)
					exception = insideTransactionException;
				else
					exception.addSuppressed(insideTransactionException);
			}
			try {
				endTransaction();
			} catch (RuntimeException endTransactionException) {
				if (exception == null)
					exception = endTransactionException;
				else
					exception.addSuppressed(endTransactionException);
			}
			try {
				if (failedException == null)
					succeedCallbacks(null, outsideTransactionClosables, transactionId);
				else
					exception = failCallbacks(failedException, outsideTransactionClosables, transactionId);
			} catch (RuntimeException outsideTransactionException) {
				if (exception == null)
					exception = outsideTransactionException;
				else
					exception.addSuppressed(outsideTransactionException);
			}
			if (exception != null)
				throw exception;
		}
	}

	@MustBeClosed
	Transaction<TransactionId> beginTransaction(TransactionId transactionId) {
		return new Transaction<>(databaseSync.get(), transactionListeners.get(), transactionId);
	}
}
