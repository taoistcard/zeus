package zeus.live.data;

import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MutedUserManager{

	private HashMap<String, MutedUser> users=new HashMap<>();
	
	ReadWriteLock lock = new ReentrantReadWriteLock(true);
	
	public void put(String key, MutedUser user)
	{
		lock.writeLock().lock();
		users.put(key, user);
		lock.writeLock().unlock();
	}
	
	public void remove(String key)
	{
		lock.writeLock().lock();
		users.remove(key);
		lock.writeLock().unlock();
	}
	
	public boolean containsKey(String key)
	{
		boolean res = false;
		lock.readLock().lock();
		res = users.containsKey(key);
		lock.readLock().unlock();
		return res;
	}
	
	public MutedUser get(String key)
	{		
		MutedUser res = null;
		lock.readLock().lock();
		res = users.get(key);
		lock.readLock().unlock();
		return res;
	}
}
