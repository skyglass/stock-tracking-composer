package skyglass.composer.utils;

import java.util.Objects;
import java.util.function.Supplier;

public class AsyncUtil {

	public static int pollResult(int size, Supplier<Integer> pollingSizeSupplier) throws InterruptedException {
		return pollResult(size, 10000, 2000, pollingSizeSupplier);
	}

	public static int pollResult(int size, int timeout, int interval, Supplier<Integer> pollingSizeSupplier) throws InterruptedException {
		int result = getResult(size, 0, timeout, interval, pollingSizeSupplier);
		if (size != result) {
			throw new RuntimeException("Failed to poll result");
		}
		return result;
	}

	public static boolean pollBooleanResult(Supplier<Boolean> pollingSupplier) throws InterruptedException {
		return pollBooleanResult(1000, 0, pollingSupplier);
	}

	public static boolean pollBooleanResult(int timeout, int interval, Supplier<Boolean> pollingSupplier) throws InterruptedException {
		boolean result = getResult(true, false, timeout, interval, pollingSupplier);
		if (!result) {
			throw new RuntimeException("Boolean Result Polling Timeout");
		}
		return result;
	}

	private static <T> T getResult(T expected, T defaultResult, int timeout, int interval, Supplier<T> pollingSupplier) throws InterruptedException {
		if (timeout <= 0 || interval < 0) {
			return defaultResult;
		}
		long startTime = System.currentTimeMillis();
		if (interval > 0) {
			Thread.sleep(interval);
		}
		while (true) {
			T pollingResult = pollingSupplier.get();
			if (Objects.equals(expected, pollingResult) || getSleepCount(startTime) > timeout) {
				if (getSleepCount(startTime) > timeout) {
					return pollingResult;
				} else {
					if (interval > 0) {
						Thread.sleep(interval);
					}
					pollingResult = pollingSupplier.get();
					if (Objects.equals(expected, pollingResult)) {
						return pollingResult;
					}
				}
			}
			if (interval > 0) {
				Thread.sleep(interval);
			}
		}
	}

	private static long getSleepCount(long startTime) {
		return System.currentTimeMillis() - startTime;
	}

}
