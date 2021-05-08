package skyglass.composer.sensor.domain.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.StringUtils;

/**
 * Represents a concurrency lock manager
 */
public class ObjectLockManager {
	final private static ObjectLockManager instance = new ObjectLockManager();

	final private static ConcurrentMap<String, ReentrantLock> locks = new ConcurrentHashMap<String, ReentrantLock>();

	final private ReentrantLock objectLock = new ReentrantLock();

	/**
	 * Creates or retrieve a previous lock associated with the key.
	 *
	 * @param key key associated with the lock.
	 * @return The Lock
	 */
	public static ReentrantLock getLock(String object) {
		return getLock(object, null);
	}

	public static ReentrantLock getLock(String object, String owner) {
		String key = owner == null ? object : StringUtils.join(new String[] { object, owner });
		ReentrantLock lock = locks.get(key);
		if (lock == null) {
			lock = instance.retrieveLock(key);
		}
		return lock;
	}

	private ReentrantLock retrieveLock(String key) {
		ReentrantLock lock;
		try {
			objectLock.lock();
			lock = locks.get(key);
			if (lock == null) {
				lock = new ReentrantLock();
				locks.put(key, lock);
			}
		} finally {
			objectLock.unlock();
		}
		return lock;
	}

}
