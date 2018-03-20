
import java.util.ArrayList;
import java.util.List;

public class MyHashMap<K,V>  implements MyMap<K, V>{

	//数组的初始化默认长度  必须是2的幂数
	private static final int  DEFAULT_INITIAL_CAPACITY = 1 << 4;
	//阀值比例
	private static final float DEFAULT_LOAD_FACTOR = 0.75f;
	
	private int defaultInitSize;
	
	private float defaultLoaderFactor;

	//MAP中entry的数量
	private int  entryUseSize;
	
	//数组
	private Entry<K, V>[] table = null;
	
	//此处使用到了“门面模式”。这里的2个构造方法其实指向的是同一个，但是对外却暴露了2个“门面”！
	
	public  MyHashMap() {
		this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
	}
	
	@SuppressWarnings("unchecked")
	public MyHashMap(int defaultInitialCapacity , float defaultLoadFactor){
		
		if(defaultInitialCapacity < 0){
			throw new IllegalArgumentException("Illegal initial capacity: "+ defaultInitialCapacity );
		}
		
		if(defaultLoadFactor <= 0 || Float.isNaN(defaultLoadFactor)){
			throw new IllegalArgumentException("Illegal load factor: "+ defaultLoadFactor );
		}
		
		this.defaultInitSize = defaultInitialCapacity;
		this.defaultLoaderFactor = defaultLoadFactor;
		
		table = new Entry[this.defaultInitSize];
		
	}
	
	
	@SuppressWarnings("hiding")
	class Entry<K, V> implements MyMap.Entry<K,V>{

		private K key;
		private V value;
		private Entry<K,V> next;
	    public Entry() {

		}
	    
	    public Entry(K key,V value,Entry<K, V> next){
	    	this.key = key;
	    	this.value = value;
	    	this.next = next;
	    }
		
		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}
	}
	
	/*
	 * put的实现
	 
	 第一，要考虑是否扩容？

	HashMap中的Entry的数量（数组以及单链表中的所有Entry）是否达到阀值？

	第二，如果扩容，意味着新生成一个Entry[]，不仅如此还得重新散列。

	第三，要根据Key计算出在Entry[]中的位置，定位后，如果Entry[]中的元素为null，那么可以放入其中，如果不为空，那么得遍历单链表，要么更新value，要么形成一个新的Entry“挤压”单链表！

	*/
	
	@Override
	public V put(K k, V v) {
		
		V oldValue = null;
		//是否需要扩容？
		//扩容完毕，肯定需要重新散列，所谓重新HASH，就是重新PUT ENTRY 到HASHMAP
		if(entryUseSize >= defaultInitSize * defaultLoaderFactor) {
			resize(2 * defaultInitSize);
		}
		
		//得到HASH值，计算出数组中的位置
		int index = hash(k) & (defaultInitSize - 1);
		if(table[index] == null){
			table[index] = new Entry<K,V>(k, v, null);
			++ entryUseSize;
		}else{//需要遍历单链表
			Entry<K, V> entry = table[index];
			Entry<K, V> e = entry;
			while(e != null){
				if(k == e.getKey() || k.equals(e.getKey())){
					oldValue = e.value;
					e.value = v;
					return oldValue;
				}
				e = e.next;
			}
			table[index] = new Entry<K, V>(k, v, entry);
			++ entryUseSize;
		}
		return oldValue;
	}
	


	/* 
	 * get很简单，只需要注意在遍历单链表的过程中使用== or equals来判断下即可。
	 */
	@Override
	public V get(K k) {

		int index = hash(k) & (defaultInitSize - 1);
		if(table[index] == null){
			return  null;
		}else{
			Entry<K, V> entry = table[index];
			do {
				if(k == entry.getKey() || k.equals(entry.getKey())){
					return entry.value;
				}
				entry = entry.next;
			} while (entry != null);
		}
		return null;
	}

	@Override
	public V remove(K k) {
		
		int index = hash(k) & (defaultInitSize - 1);
		Entry<K,V> pre = null;  //存放上一个Entry   
		Entry<K,V> e = table[index];  
          
        for(;e != null;e = e.next){  
            if(k.equals(e.key)){  
                //删除  
                if(pre == null){//删除header  
                    table[index] = e.next;  
                    return e.getValue();  
                }else{  
                    pre.next = e.next;
                    V valueDel = e.getValue();
                    e = null;  
                    return valueDel;  
                }  
            }  
            pre = e;  
        }  
        return null;  
	}
	
	@SuppressWarnings("rawtypes")
	private void resize(int i) {

		Entry[] newTable = new Entry[i];
		//改变了数组的大小
		defaultInitSize = i;
		entryUseSize = 0;
		rehash(newTable);
	}

	private void rehash(Entry[] newTable) {

		//得到原来老的Entry集合 ，注意遍历单链表
		List<Entry<K, V>> entryList = new ArrayList<Entry<K, V>>();
		for(Entry<K, V> entry : table){
			if(entry != null){
				do {
					entryList.add(entry);
					entry = entry.next;
				} while (entry != null);
			}
		}
		
		//覆盖旧的引用
		if(newTable.length > 0){
			table = newTable;
		}
		
		//所谓重新HASH，就是重新PUT ENTRY 到HASHMAP
		for(Entry<K,V> entry : entryList){
			put(entry.getKey(), entry.getValue());
		}
	}
	
	private final int hash(K k) {
		int hashCode = k.hashCode();
		hashCode ^= (hashCode >>> 20) ^ (hashCode >>> 12);
		return  hashCode ^ (hashCode >>> 7 ) ^ (hashCode >>> 4);
	}

	
	// 此处是JDK的HashMap的hash函数的实现，这里也再次说明了：要想散列均匀，就得进行二进制的位运算！
//    static final int hash(Object key) {
//        int h;
//        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
//    }
}
