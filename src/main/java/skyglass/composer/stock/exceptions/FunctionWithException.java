package skyglass.composer.stock.exceptions;

import java.util.function.Function;

@FunctionalInterface
public interface FunctionWithException<T, R, E extends Throwable> {

	R apply(T t) throws E;

	public static <T, R, E extends Throwable> Function<T, R> wrapper(FunctionWithException<T, R, E> fe) {
		return t -> {
			try {
				return fe.apply(t);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		};
	}
}
