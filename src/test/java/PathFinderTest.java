import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.graphics.GL20;
import com.szakdogaServer.BusinessLogic.PathFinder;
import com.szakdogaServer.DataBase.DB;
import org.datatransferobject.UnitDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

import java.util.ArrayList;

public class PathFinderTest {
    static PathFinder pathFinder;
    static DB db;
    @BeforeAll
    public static void setupPathFinder(){
        new HeadlessApplication(new ApplicationAdapter() {
        });
        Gdx.gl20 = Mockito.mock(GL20.class);
        Gdx.gl = Gdx.gl20;

        pathFinder = new PathFinder();
        db = DB.getInstance();
    }
    @Test
    public void CorrectStartingTileInDB(){
        UnitDTO unitDTO = new UnitDTO(0,0,0,0,db.getPlayerPositionX(1),db.getPlayerPositionY(1),0,0,0,db.getPlayerPositionX(1),db.getPlayerPositionY(1),"asd",0,new ArrayList<Integer>(),new ArrayList<Integer>(),0l);
        pathFinder.setupNextTiles(unitDTO);
        Assertions.assertEquals(6,unitDTO.getNextX().size());
        Assertions.assertEquals(6,unitDTO.getNextY().size());
    }
    @Test
    public void CorrectCoordinatesWithNoMoreDistanceThan2(){
        UnitDTO unitDTO = new UnitDTO(0,0,0,0,db.getPlayerPositionX(1),db.getPlayerPositionY(1),0,0,0,db.getPlayerPositionX(1),db.getPlayerPositionY(1),"asd",0,new ArrayList<Integer>(),new ArrayList<Integer>(),0l);
        pathFinder.setupNextTiles(unitDTO);
        Assertions.assertTrue(Math.sqrt((Math.pow(unitDTO.getX() - unitDTO.getNextX().get(0), 2)) + (Math.pow(unitDTO.getY() - unitDTO.getNextY().get(0), 2))) < 1.51f);
        unitDTO.setX(unitDTO.getNextX().remove(0));
        unitDTO.setY(unitDTO.getNextY().remove(0));
        Assertions.assertTrue(Math.sqrt((Math.pow(unitDTO.getX() - unitDTO.getNextX().get(0), 2)) + (Math.pow(unitDTO.getY() - unitDTO.getNextY().get(0), 2))) < 1.51f);
        unitDTO.setX(unitDTO.getNextX().remove(0));
        unitDTO.setY(unitDTO.getNextY().remove(0));
        Assertions.assertTrue(Math.sqrt((Math.pow(unitDTO.getX() - unitDTO.getNextX().get(0), 2)) + (Math.pow(unitDTO.getY() - unitDTO.getNextY().get(0), 2))) < 1.51f);
        unitDTO.setX(unitDTO.getNextX().remove(0));
        unitDTO.setY(unitDTO.getNextY().remove(0));
        Assertions.assertTrue(Math.sqrt((Math.pow(unitDTO.getX() - unitDTO.getNextX().get(0), 2)) + (Math.pow(unitDTO.getY() - unitDTO.getNextY().get(0), 2))) < 1.51f);
        unitDTO.setX(unitDTO.getNextX().remove(0));
        unitDTO.setY(unitDTO.getNextY().remove(0));
        Assertions.assertTrue(Math.sqrt((Math.pow(unitDTO.getX() - unitDTO.getNextX().get(0), 2)) + (Math.pow(unitDTO.getY() - unitDTO.getNextY().get(0), 2))) < 1.51f);
        unitDTO.setX(unitDTO.getNextX().remove(0));
        unitDTO.setY(unitDTO.getNextY().remove(0));
        Assertions.assertTrue(Math.sqrt((Math.pow(unitDTO.getX() - unitDTO.getNextX().get(0), 2)) + (Math.pow(unitDTO.getY() - unitDTO.getNextY().get(0), 2))) < 1.51f);
        unitDTO.setX(unitDTO.getNextX().remove(0));
        unitDTO.setY(unitDTO.getNextY().remove(0));
        Assertions.assertEquals(unitDTO.getNextX().size(),0);
        Assertions.assertEquals(unitDTO.getNextY().size(),0);
    }
    @Test
    public void calculatingCorrectAngelBetweenTwoPoints(){
        UnitDTO unitDTO = new UnitDTO(0,0,0,0,db.getPlayerPositionX(1),db.getPlayerPositionY(1),0,0,0,db.getPlayerPositionX(1),db.getPlayerPositionY(1),"asd",0,new ArrayList<Integer>(),new ArrayList<Integer>(),0l);
        pathFinder.setupNextTiles(unitDTO);
        pathFinder.calculateAngle(unitDTO);
        Assertions.assertEquals(0.707,unitDTO.getDeltaX(),2);
        Assertions.assertEquals(-0.707,unitDTO.getDeltaY(),2);
        unitDTO.setX(unitDTO.getNextX().remove(0));
        unitDTO.setY(unitDTO.getNextY().remove(0));
        Assertions.assertEquals(0.707,unitDTO.getDeltaX(),2);
        Assertions.assertEquals(-0.707,unitDTO.getDeltaY(),2);
    }
}
