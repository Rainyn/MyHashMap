



/**
 * 定义一个接口，对外暴露快速存取的方法。  注意MyMap接口内部定义了一个内部接口Entry。
 * @param <K>
 * @param <V>
 */
public interface MyMap<K,V> {

	public V put(K K,V v);
	
	public V get(K k);
	
	public V remove(K k);
	
	interface  Entry<K,V>{
		public K getKey();
		public V getValue();
	}
	
}
