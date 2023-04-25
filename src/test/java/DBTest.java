import com.szakdogaServer.DataBase.DB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DBTest {
    @Test
    public void TestingIfDBexistsAndProperlyConnects(){
        DB db = new DB();
        Assertions.assertDoesNotThrow(() -> db.getPlayerPositionY(1));
    }
    @Test
    public void TestIfDBReturnsResultsProperly(){
        DB db = new DB();
        Assertions.assertTrue(db.getPlayerPositionX(1)>0);
    }
}
