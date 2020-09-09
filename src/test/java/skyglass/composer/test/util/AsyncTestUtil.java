package skyglass.composer.test.util;

import java.util.function.Supplier;

import org.junit.Assert;

public class AsyncTestUtil {

	public static int pollResult(int size, Supplier<Integer> pollingSizeSupplier) throws InterruptedException {
		return pollResult(size, 10000, 2000, pollingSizeSupplier);
	}

	public static <T> int pollResult(int size, int timeout, int interval, Supplier<Integer> pollingSizeSupplier) throws InterruptedException {
		int result = getResult(size, timeout, interval, pollingSizeSupplier);
		Assert.assertEquals(size, result);
		return result;
	}

	private static <T> int getResult(int size, int timeout, int interval, Supplier<Integer> pollingSizeSupplier) throws InterruptedException {
		if (timeout <= 0 || interval < 0) {
			return 0;
		}
		int sleepCount = interval;
		Thread.sleep(interval);
		while (true) {
			Integer pollingSize = pollingSizeSupplier.get();
			if (pollingSize.intValue() == size || sleepCount > timeout) {
				if (sleepCount > timeout) {
					return pollingSize;
				} else {
					Thread.sleep(interval);
					pollingSize = pollingSizeSupplier.get();
					if (pollingSize.intValue() == size) {
						return pollingSize;
					}
				}
			}
			Thread.sleep(interval);
			sleepCount += interval;
		}
	}

}
