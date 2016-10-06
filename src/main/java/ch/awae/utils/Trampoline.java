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
 * arguments may be added in the future). Use {@link Trampoline.Result
 * Trampoline.Result&lt;T&gt;} (where {@code T} is the return type of the
 * complete trampolined call structure) as the return type of any involved
 * method. Normal methods invoked from inside a Trampoline must either be called
 * as a subcall and the result packed into a {@link Trampoline.Result} instance
 * using {@link Trampoline#result} or they can be wrapped into a
 * {@link Trampoline.Result} using {@link Trampoline#bounceWrapped}.
 * </p>
 * 
 * @author Andreas Wälchli (andreas.waelchli@me.com)
 * @version 1.1
 * @since awaeUtils 0.0.1
 */
public final class Trampoline {
	
	public static abstract class Result<T> {

		private Result() {
		}

		boolean isReturn() {
			return false;
		}

		T get() {
			throw new UnsupportedOperationException();
		}

		abstract Result<T> call();

	}

	public static <T> Result<T> result(T value) {
		return new Result<T>() {
			@Override
			boolean isReturn() {
				return true;
			}

			@Override
			T get() {
				return value;
			}

			@Override
			public Result<T> call() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public static <T> Result<T> bounce(Supplier<Result<T>> f) {
		return new Result<T>() {

			@Override
			Result<T> call() {
				return f.get();
			}

		};
	}

	public static <T, A> Result<T> bounce(Function<A, Result<T>> f, A param0) {
		return new Result<T>() {

			@Override
			Result<T> call() {
				return f.apply(param0);
			}
		};
	}

	public static <T, A, B> Result<T> bounce(BiFunction<A, B, Result<T>> f, A param0, B param1) {
		return new Result<T>() {
			@Override
			Result<T> call() {
				return f.apply(param0, param1);
			}
		};
	}

	public static <T> Result<T> bounceWrapped(Supplier<T> f) {
		return new Result<T>() {

			@Override
			Result<T> call() {
				return result(f.get());
			}

		};
	}

	public static <T, A> Result<T> bounceWrapped(Function<A, T> f, A param0) {
		return new Result<T>() {

			@Override
			Result<T> call() {
				return result(f.apply(param0));
			}

		};
	}

	public static <T, A, B> Result<T> bounceWrapped(BiFunction<A, B, T> f, A param0, B param1) {
		return new Result<T>() {

			@Override
			Result<T> call() {
				return result(f.apply(param0, param1));
			}

		};
	}

	public static <T> T run(Result<T> result) {
		Result<T> step = result;
		while (!step.isReturn())
			step = result.call();
		return step.get();
	}

}
