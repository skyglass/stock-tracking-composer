package skyglass.composer.sensor.domain.service;

import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import skyglass.composer.utils.AsyncUtil;

@Component
public class DatabaseLockManager {

	private static final String DEFAULT_OWNER = "123";

	@Autowired
	private DatabaseLockRepository repository;

	public boolean tryLock(String object) {
		return tryLock(object, DEFAULT_OWNER);
	}

	public boolean tryLock(String object, String owner) {
		ReentrantLock lock = ObjectLockManager.getLock(object, owner);
		lock.lock();
		try {
			return AsyncUtil.pollBooleanResult(() -> _tryLock(object, owner));
		} catch (Exception e) {
			try {
				return AsyncUtil.pollBooleanResult(() -> _releaseLock(object, owner));
			} catch (Exception e2) {
				throw new RuntimeException(e2);
			}
		} finally {
			lock.unlock();
		}
	}

	public boolean releaseLock(String object) {
		return releaseLock(object, DEFAULT_OWNER);
	}

	public boolean releaseLock(String object, String owner) {
		ReentrantLock lock = ObjectLockManager.getLock(object, owner);
		lock.lock();
		try {
			return AsyncUtil.pollBooleanResult(() -> _releaseLock(object, owner));
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
	}

	private boolean _tryLock(String object, String owner) {
		try {
			repository.insertLock(object, owner);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean _releaseLock(String object, String owner) {
		try {
			repository.deleteLock(object, owner);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
