

public class MyMapTest {

	public static void main(String[] args) {

		MyMap<String, String> myMap = new MyHashMap<String, String>();
		for(int i = 0; i < 10;i ++){
			myMap.put("key"+i, "value"+i);
		}
		for(int i = 0; i < 10;i ++){
			String value = myMap.get("key"+i);
			System.out.println("key"+i+",value is : "+value);
		}
		String value = myMap.remove("key3");
		System.out.println("the deleted value is :"+ value);
		System.out.println(myMap.get("key3"));
	}
}
