import com.richal.learn.MyHashMap;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MyHashMapTest {

    @Test
    public void testHashMapApi() {

        MyHashMap<String, String> myHashMap = new MyHashMap<>();
        int count = 10000;
        for (int i = 0; i < count; i++) {
            myHashMap.put("key: " + i, "value: " + i);
        }
        assertEquals(count, myHashMap.size());

        for (int i = 0; i < count; i++) {
            assertEquals("value: " + i, myHashMap.get("key: " + i));
        }

        myHashMap.remove("key: 8");
        assertNull( myHashMap.get("key: 8"));

        assertEquals(count - 1, myHashMap.size());

    }
}
