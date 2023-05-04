import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.graphics.GL20;
import com.szakdogaServer.businessLogic.DTOLogic;
import com.szakdogaServer.dataBase.DB;
import org.datatransferobject.DTO;
import org.datatransferobject.PlayerDTO;
import org.datatransferobject.UnitDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

public class DTOLogicTest {
    private static Application application;
    @BeforeAll
    public static void setupApplication(){
        application = new HeadlessApplication(new ApplicationAdapter() {
        });
        Gdx.gl20 = Mockito.mock(GL20.class);
        Gdx.gl = Gdx.gl20;
    }
    @Test
    public void TestIfReturnCorrectResultsForSpawnPoints(){
        DB db= DB.getInstance();
        DTO dto=new DTO(null,null,new PlayerDTO(100,-1,-1,100),
                0,null);
        DTOLogic.checkIfDTOHasCorrectPossition(dto,db);
        Assertions.assertTrue(dto.getPlayerDTO().getPositionX()>0);
        Assertions.assertTrue(dto.getPlayerDTO().getPositionY()>0);
    }
    @Test
    public void SetupUnits(){
        DB db= DB.getInstance();
        DTO dto=new DTO(null,null,new PlayerDTO(100,-1,-1,100),
                0,null);
        DTOLogic.checkIfDTOHasCorrectPossition(dto,db);
        UnitDTO unitDTO=new UnitDTO(1,1,1,1,dto.getPlayerDTO().getPositionX()
                ,dto.getPlayerDTO().getPositionY()
                ,1,1,1,dto.getPlayerDTO().getPositionX(),dto.getPlayerDTO().getPositionY()
                ,"test",0,new ArrayList<>(),new ArrayList<>(),0l);
        DTOLogic.setupNewUnits(dto,unitDTO);
        Assertions.assertEquals(6,unitDTO.getNextX().size());
        Assertions.assertEquals(6,unitDTO.getNextY().size());
        Assertions.assertNotEquals(0,unitDTO.getId());
    }
    @Test
    public void SetupUnitsNotEnoughMoney(){
        DB db= DB.getInstance();
        DTO dto=new DTO(null,null,new PlayerDTO(100,-1,-1,100),
                0,null);
        DTOLogic.checkIfDTOHasCorrectPossition(dto,db);
        UnitDTO unitDTO=new UnitDTO(1,1,1,120,dto.getPlayerDTO().getPositionX()
                ,dto.getPlayerDTO().getPositionY()
                ,1,1,1,dto.getPlayerDTO().getPositionX(),dto.getPlayerDTO().getPositionY()
                ,"test",0,new ArrayList<>(),new ArrayList<>(),0l);
        DTOLogic.setupNewUnits(dto,unitDTO);
        Assertions.assertEquals(-1,unitDTO.getId());
    }
    @Test
    public void SetupUnitWrongUnitStartPos(){
        DB db= DB.getInstance();
        DTO dto=new DTO(null,null,new PlayerDTO(100,-1,-1,100),
                0,null);
        DTOLogic.checkIfDTOHasCorrectPossition(dto,db);
        UnitDTO unitDTO=new UnitDTO(1,1,1,120,dto.getPlayerDTO().getPositionX()
                ,dto.getPlayerDTO().getPositionY()
                ,1,1,1,0,0
                ,"test",0,new ArrayList<>(),new ArrayList<>(),0l);
        DTOLogic.setupNewUnits(dto,unitDTO);
        Assertions.assertEquals(-1,unitDTO.getId());
    }
}
