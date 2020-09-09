package skyglass.composer.test.util;

import java.util.function.Supplier;

@FunctionalInterface
public interface SupplierWithException<T, E extends Throwable> {

	T get() throws E;

	public static <T, E extends Throwable> Supplier<T> wrapper(SupplierWithException<T, E> fe) {
		return () -> {
			try {
				return fe.get();
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		};
	}
}
