package org.moflon.core.xtext.utils;

import java.util.Map;

public interface BiMap<K, V> extends Map<K, V>{
    V getValue(K key);
    K getKey(V value);
    
    V getValueOrDefault(K key, V defaultValue);
    K getKeyOrDefault(V value, K defaultKey);
    
    V removeKey(K key);
    K removeValue(V value);
}
