import com.richal.learn.MyArrayList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArrayListTest {

    @Test
    public void operateTest() {
        MyArrayList myArrayList = new MyArrayList();
        for (int i = 0; i < 30; i++) {
            myArrayList.add(i);
        }
        assertEquals(30, myArrayList.size());

        myArrayList.remove(0);
        myArrayList.remove(18);
        assertEquals(28, myArrayList.size());
        assertEquals(16, myArrayList.get(15));
        myArrayList.set(18, 17);
        assertEquals(17, myArrayList.get(18));

    }
}
