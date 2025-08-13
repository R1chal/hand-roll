import com.richal.learn.MyLinkedList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LinkedListTest {

    @Test
    public void operateTest() {
        MyLinkedList myLinkedList = new MyLinkedList();
        for (int i = 0; i < 30; i++) {
            myLinkedList.add(i);
        }
        assertEquals(30, myLinkedList.size());

        myLinkedList.remove(0);
        myLinkedList.remove(18);
        assertEquals(28, myLinkedList.size());
        assertEquals(16, myLinkedList.get(15));
        myLinkedList.set(18, 17);
        assertEquals(17, myLinkedList.get(18));

    }
}
