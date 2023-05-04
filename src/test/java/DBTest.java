import com.szakdogaServer.dataBase.DB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DBTest {
    @Test
    public void TestingIfDBexistsAndProperlyConnects(){
        DB db = DB.getInstance();
        Assertions.assertDoesNotThrow(() -> db.getPlayerPositionY(1));
    }
    @Test
    public void TestIfDBReturnsResultsProperly(){
        DB db = DB.getInstance();
        Assertions.assertTrue(db.getPlayerPositionX(1)>0);
    }
}
