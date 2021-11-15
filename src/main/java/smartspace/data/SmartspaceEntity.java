package smartspace.data;

public interface SmartspaceEntity<K> {
	K getKey();
	void setKey(K key);
}
