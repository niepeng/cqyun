package com.baibutao.app.waibao.yun.android.util.cache;

import java.lang.ref.SoftReference;

/**
 * @author lsb
 *
 * @date 2012-5-29 下午11:49:53
 */
public class LRUCache<K, V> {

	private LRUCacheImpl<K, SoftReference<V>> holder = new LRUCacheImpl<K, SoftReference<V>>();
	
	public synchronized void put(K k, V v) {
		holder.put(k, new SoftReference<V>(v));
	}
	
	public synchronized V get(K k) {
		SoftReference<V> v = holder.get(k);
		if (v == null) {
			return null;
		}
		return v.get();
	}
	
	public synchronized void remove(K k) {
		holder.remove(k);
	}
	
}
