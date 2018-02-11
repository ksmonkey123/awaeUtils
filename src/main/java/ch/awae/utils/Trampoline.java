/*
 * Copyright (c) 2016 Andreas Wälchli
 */
package ch.awae.utils;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Utility class for implementing trampoline based primitive recursion.
 * <p>
 * Whenever a method has a tail call that tail call can be externalised by not
 * returning the result of the call, but instead an object instructing the
 * Trampoline to perform the call to that method and to return the value
 * provided by that function. That function can itself return the instruction to
 * call another method. This allows for iterative execution instead of recursive
 * one preventing {@link StackOverflowError StackOverflowErrors}.
 * </p>
 * <!--TODO include link to a more in-depth explanation & examples-->
 * <p>
 * This class provides all helper methods required for Trampoline
 * implementations with functions with up to two arguments (helpers for more
 * arguments may be added in the future). Use {@link #Result
 * Trampoline.Result&lt;T&gt;} (where {@code T} is the return type of the
 * complete trampolined call structure) as the return type of any involved
 * method. Normal methods invoked from inside a Trampoline must either be called
 * as a subcall and the result packed into a {@link #Result} instance using
 * {@link #result} or they can be wrapped into a {@link #Result} using
 * {@link #bounceWrapped}.
 * </p>
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 0.0.1
 */
public final class Trampoline {

	/**
	 * Helper class representing the result of a single trampoline step.
	 * <p>
	 * A Result may be the final return value or the bounce instruction to
	 * continue the trampoline. Result instances are implicitly created by
	 * calling any of the {@link #bounce} methods or the {@link #bounceWrapped}
	 * methods or the {@link #result} method.
	 * </p>
	 * 
	 * @author Andreas Wälchli
	 * @since awaeUtils 0.0.1
	 * @param <T>
	 *            the return type
	 */
	public static final class Result<T> {

		private final boolean isValue;
		private final T value;
		private final Supplier<Result<T>> supplier;

		Result(final boolean isValue, final T value, final Supplier<Result<T>> supplier) {
			this.isValue = isValue;
			this.value = isValue ? value : null;
			this.supplier = isValue ? null : supplier;
		}

		/**
		 * indicates if this Result is a return value or not.
		 * 
		 * @return {@code true} if the Result is a return value.
		 */
		final boolean isReturn() {
			return this.isValue;
		}

		/**
		 * returns the return value of this Result if it exists.
		 * 
		 * @return the return value.
		 * @throws UnsupportedOperationException
		 *             if this result does not represent a return value.
		 */
		final T get() {
			if (this.isValue)
				return this.value;
			throw new UnsupportedOperationException();
		}

		/**
		 * performs the next trampoline step and returns the result of that
		 * step.
		 * 
		 * @return the result of the performed step.
		 * @throws UnsupportedOperationException
		 *             if the Result is a return value.
		 */
		final Result<T> call() {
			if (this.isValue)
				throw new UnsupportedOperationException();
			return this.supplier.get();
		}

	}

	// ==================
	// TRAMPOLINE METHODS
	// ==================

	/**
	 * Wraps a value into a Result instance representing that value
	 * 
	 * @param value
	 *            the value to wrap into a Result
	 * @return the Result containing the {@code value}
	 */
	public static <T> Result<T> result(T value) {
		return new Result<T>(true, value, null);
	}

	/**
	 * Bounce to a {@link Supplier} for the next step.
	 * 
	 * @param f
	 *            the function
	 * @return a Result instance representing a Trampoline step. When executing
	 *         that step the Function {@code f} will be called. The return value
	 *         of that call determines the next trampoline action.
	 */
	public static <T> Result<T> bounce(Supplier<Result<T>> f) {
		return new Result<T>(false, null, f);
	}

	/**
	 * Bounce to a {@link Function} for the next step.
	 * 
	 * @param f
	 *            the function
	 * @param param0
	 *            the function parameter
	 * @return a Result instance representing a Trampoline step. When executing
	 *         that step the function {@code f} will be called with the
	 *         parameter {@code param0}. The return value of that call
	 *         determines the next trampoline action.
	 */
	public static <T, A> Result<T> bounce(Function<A, Result<T>> f, A param0) {
		return new Result<T>(false, null, () -> f.apply(param0));
	}

	/**
	 * Bounce to a {@link BiFunction} for the next step.
	 * 
	 * @param f
	 *            the function
	 * @param param0
	 *            the first function parameter
	 * @param param1
	 *            the second function parameter
	 * @return a Result instance representing a Trampoline step. When executing
	 *         that step the function {@code f} will be called with the
	 *         parameters {@code param0} and {@code param1}. The return value of
	 *         that call determines the next trampoline action.
	 */
	public static <T, A, B> Result<T> bounce(BiFunction<A, B, Result<T>> f, A param0, B param1) {
		return new Result<T>(false, null, () -> f.apply(param0, param1));
	}

	/**
	 * Bounces to a generic {@link Supplier}.
	 * <p>
	 * This method is designed to provide a hook into an arbitrary external
	 * method. This does interrupt normal trampoline functionality until the
	 * supplier function returns. The return value of the supplier is then
	 * wrapped into a Result and returned.
	 * </p>
	 * 
	 * @param f
	 *            the function
	 * @return a Result instance representing a Trampoline step. When executing
	 *         that step the Function {@code f} will be called. The return value
	 *         of that call will be wrapped into a Result and returned to the
	 *         trampoline.
	 */
	public static <T> Result<T> bounceWrapped(Supplier<T> f) {
		return new Result<T>(false, null, () -> result(f.get()));
	}

	/**
	 * Bounces to a generic {@link Function}.
	 * <p>
	 * This method is designed to provide a hook into an arbitrary external
	 * method. This does interrupt normal trampoline functionality until the
	 * function returns. The return value of the function is then wrapped into a
	 * Result and returned.
	 * </p>
	 * 
	 * @param f
	 *            the function
	 * @param param0
	 *            the function parameter
	 * @return a Result instance representing a Trampoline step. When executing
	 *         that step the function {@code f} will be called with the
	 *         parameter {@code param0}. The return value of that call will be
	 *         wrapped into a Result and returned to the trampoline.
	 */
	public static <T, A> Result<T> bounceWrapped(Function<A, T> f, A param0) {
		return new Result<T>(false, null, () -> result(f.apply(param0)));
	}

	/**
	 * Bounces to a generic {@link BiFunction}.
	 * <p>
	 * This method is designed to provide a hook into an arbitrary external
	 * method. This does interrupt normal trampoline functionality until the
	 * function returns. The return value of the function is then wrapped into a
	 * Result and returned.
	 * </p>
	 * 
	 * @param f
	 *            the function
	 * @param param0
	 *            the first function parameter
	 * @param param1
	 *            the second function parameter
	 * @return a Result instance representing a Trampoline step. When executing
	 *         that step the function {@code f} will be called with the
	 *         parameters {@code param0} and {@code param1}. The return value of
	 *         that call will be wrapped into a Result and returned to the
	 *         trampoline.
	 */
	public static <T, A, B> Result<T> bounceWrapped(BiFunction<A, B, T> f, A param0, B param1) {
		return new Result<T>(false, null, () -> result(f.apply(param0, param1)));
	}

	/**
	 * Starts a new trampoline with a given {@link #Result}.
	 * 
	 * <p>
	 * Usually the passed {@code result} is generated with one of the
	 * {@link #bounce} methods. Using a {@link #bounceWrapped} method is
	 * recommended, since those methods break the normal trampoline execution.
	 * </p>
	 * 
	 * @param result
	 *            the Result to start the trampoline from
	 * @return the return value of the last trampoline step
	 */
	public static <T> T run(Result<T> result) {
		Result<T> step = result;
		while (!step.isReturn())
			step = result.call();
		return step.get();
	}

}
