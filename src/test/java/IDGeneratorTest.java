import com.szakdogaServer.BusinessLogic.IdCreator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;

import java.util.HashSet;
import java.util.Set;

public class IDGeneratorTest {

    @RepeatedTest(5)
    public void testIfItCreateDuplicates(){
        Set<Integer> set = new HashSet<>();
        for(int i=0;i<1000000;i++){
            int id=IdCreator.getNewId();
            Assertions.assertTrue(id>0);
            Assertions.assertTrue(set.add(id));
        }
    }
}
