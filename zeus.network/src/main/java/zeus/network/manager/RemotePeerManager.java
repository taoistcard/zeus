package zeus.network.manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import io.netty.channel.ChannelHandlerContext;

/**
 * Զ�̴�����������
 * 
 * @author frank
 *
 */
public class RemotePeerManager {

	private static Map<String, IRemotPeer> peerMap = new HashMap<String, IRemotPeer>();
	private static ReadWriteLock peerMapLock = new ReentrantReadWriteLock(true);

	private static Map<ChannelHandlerContext, HashSet<IRemotPeer>> ctxVirtualMap = new HashMap<ChannelHandlerContext, HashSet<IRemotPeer>>();
	private static ReadWriteLock ctxVirtualMapLock = new ReentrantReadWriteLock(true);

	public static void put(String sessionId, IRemotPeer peer) {
		peerMapLock.writeLock().lock();
		peerMap.put(sessionId, peer);
		System.out.println("peermaganer:on put, count:" + peerMap.size());
		peerMapLock.writeLock().unlock();
	}

	public static IRemotPeer getClient(String sessionId) {
		IRemotPeer peer = null;
		try {
			peerMapLock.readLock().lock();
			peer = peerMap.get(sessionId);
		} catch (Exception e) {

		} finally {
			peerMapLock.readLock().unlock();
		}
		return peer;
	}

	public static IRemotPeer removeClient(String sessionId) {
		IRemotPeer peer = null;
		try {
			peerMapLock.writeLock().lock();
			peer = peerMap.remove(sessionId);
		} catch (Exception e) {

		} finally {
			peerMapLock.writeLock().unlock();
		}

		if (peer != null) {
			removeVirtual(peer.getContext(), peer);
		}
		return peer;
	}

	public static boolean containsKey(String sessionId) {
		boolean contained = false;
		try {
			peerMapLock.readLock().lock();
			contained = peerMap.containsKey(sessionId);
		} catch (Exception e) {

		} finally {
			peerMapLock.readLock().unlock();
		}
		return contained;
	}

	public static int getSize() {
		int size = 0;
		try {
			peerMapLock.readLock().lock();
			size = peerMap.size();
		} catch (Exception e) {

		} finally {
			peerMapLock.readLock().unlock();
		}
		return size;
	}

	public static String[] getAllSessionIds() {
		String[] sessionIds = null;
		try {
			peerMapLock.readLock().lock();
			sessionIds = peerMap.keySet().toArray(new String[0]);
		} catch (Exception e) {

		} finally {
			peerMapLock.readLock().unlock();
		}
		return sessionIds;
	}

	public static IRemotPeer[] getAllPeer() {
		IRemotPeer[] peers = null;
		try {
			peerMapLock.readLock().lock();
			peers = peerMap.values().toArray(new IRemotPeer[0]);
		} catch (Exception e) {

		} finally {
			peerMapLock.readLock().unlock();
		}
		return peers;
	}

	public static void addVirtual(ChannelHandlerContext ctx, IRemotPeer peer) {
		if (ctx == null || peer == null)
			return;
		try {
			ctxVirtualMapLock.writeLock().lock();
			HashSet<IRemotPeer> set = ctxVirtualMap.get(ctx);
			if (set == null) {
				set = new HashSet<IRemotPeer>();
			}
			set.add(peer);
		} catch (Exception e) {

		} finally {
			ctxVirtualMapLock.writeLock().unlock();
		}
	}

	public static void removeVirtual(ChannelHandlerContext ctx, IRemotPeer peer) {
		if (ctx == null || peer == null)
			return;
		try {
			ctxVirtualMapLock.writeLock().lock();
			HashSet<IRemotPeer> set = ctxVirtualMap.get(ctx);
			if (set != null) {
				set.remove(peer);
			}
		} catch (Exception e) {

		} finally {
			ctxVirtualMapLock.writeLock().unlock();
		}
	}

	public static void removeVirtual(ChannelHandlerContext ctx) {
		if (ctx == null)
			return;
		try {
			ctxVirtualMapLock.writeLock().lock();
			HashSet<IRemotPeer> set = ctxVirtualMap.get(ctx);
			if (set != null) {
				set.clear();
			}
		} catch (Exception e) {

		} finally {
			ctxVirtualMapLock.writeLock().unlock();
		}
	}

	public static HashSet<IRemotPeer> getAllVirtual(ChannelHandlerContext ctx) {
		if (ctx == null)
			return null;
		HashSet<IRemotPeer> set = null;
		try {
			ctxVirtualMapLock.readLock().lock();
			set = ctxVirtualMap.get(ctx);
		} catch (Exception e) {

		} finally {
			ctxVirtualMapLock.readLock().unlock();
		}
		return set;
	}
}
