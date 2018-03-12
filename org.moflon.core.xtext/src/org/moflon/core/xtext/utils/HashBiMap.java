package org.moflon.core.xtext.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HashBiMap<K,V> implements BiMap<K, V>{

    private Map<K,V> map = new HashMap<K, V>();
    private Map<V,K> inversedMap = new HashMap<V, K>();

    public V put(K k, V v) {
        map.put(k, v);
        inversedMap.put(v, k);
        return v;
    }

    @Override
   public  V get(Object k) {
        return map.get(k);
    }

    public K getKey(V v) {
        return inversedMap.get(v);
    }

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return inversedMap.containsKey(value);
	}
	
	@Override
	public V remove(Object key) {
		V value = map.remove(key);
		inversedMap.remove(value);
		return value;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> otherMap) {
		map.putAll(otherMap);
		otherMap.entrySet().parallelStream().forEach(entry -> inversedMap.put(entry.getValue(),entry.getKey()));
	}

	@Override
	public void clear() {
		map.clear();
		inversedMap.clear();
	}

	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	@Override
	public Collection<V> values() {
		return inversedMap.keySet();
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return map.entrySet();
	}

	@Override
	public V getValue(K key) {
		return map.get(key);
	}

	@Override
	public V removeKey(K key) {
		return this.remove(key);
	}

	@Override
	public K removeValue(V value) {
		K key = inversedMap.remove(value);
		map.remove(key);
		return key;
	}

	@Override
	public V getValueOrDefault(K key, V defaultValue) {
		return map.getOrDefault(key, defaultValue);
	}

	@Override
	public K getKeyOrDefault(V value, K defaultKey) {
		return inversedMap.getOrDefault(value, defaultKey);
	}

	public String toString() {
		return map.toString();
	}

}
