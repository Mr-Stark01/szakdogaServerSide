import com.szakdogaServer.BusinessLogic.TowerAttack;
import org.datatransferobject.TowerDTO;
import org.datatransferobject.UnitDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class TowerAttackTest {
    @Test
    public void TestIfFindEnemy(){
        ArrayList<UnitDTO> units= new ArrayList<>();
        units.add(new UnitDTO(1,100,10,10,19,19,0,0,1,
                20,20,"Test",231,new ArrayList<>(),new ArrayList<>(),0l));
        TowerDTO towerDTO = new TowerDTO(20,20,5,10,
                10,null,1f,1L,23123,"Test");
        TowerAttack.checkIfEnemyStillInRangeAndAllive(units,towerDTO);
        Assertions.assertEquals(towerDTO.getTarget().getId(),units.get(0).getId());
    }
    @Test
    public void OutSideOfRange(){
        ArrayList<UnitDTO> units= new ArrayList<>();
        units.add(new UnitDTO(1,100,10,10,19,19,0,0,1,
                26,26,"Test",231,new ArrayList<>(),new ArrayList<>(),0l));
        TowerDTO towerDTO = new TowerDTO(20,20,5,10,
                5,null,1f,1L,23123,"Test");
        TowerAttack.checkIfEnemyStillInRangeAndAllive(units,towerDTO);
        Assertions.assertThrows(NullPointerException.class,() -> towerDTO.getTarget().getId());
    }
    @Test
    public void CheckIfSystemAlwaysFindTheLattestAddedIfPreviousDiesOrOutOfRange(){
        ArrayList<UnitDTO> units= new ArrayList<>();
        units.add(new UnitDTO(1,100,10,10,19,19,0,0,1,
                26,26,"Test",231,new ArrayList<>(),new ArrayList<>(),0l));
        units.add(new UnitDTO(1,100,10,10,19,19,0,0,1,
                26,26,"Test",232,new ArrayList<>(),new ArrayList<>(),0l));
        units.add(new UnitDTO(1,100,10,10,19,19,0,0,1,
                26,26,"Test",233,new ArrayList<>(),new ArrayList<>(),0l));
        units.add(new UnitDTO(1,100,10,10,19,19,0,0,1,
                26,26,"Test",234,new ArrayList<>(),new ArrayList<>(),0l));
        units.add(new UnitDTO(1,100,10,10,19,19,0,0,1,
                26,26,"Test",235,new ArrayList<>(),new ArrayList<>(),0l));
        TowerDTO towerDTO = new TowerDTO(20,20,5,10,
                10,null,1f,1L,23123,"Test");
        TowerAttack.attack(units,towerDTO);
        Assertions.assertEquals(towerDTO.getTarget().getId(),units.get(0).getId());
        units.get(0).setX(1000);
        TowerAttack.attack(units,towerDTO);
        Assertions.assertEquals(towerDTO.getTarget().getId(),units.get(0).getId());
        units.get(0).setX(1000);
        TowerAttack.attack(units,towerDTO);
        Assertions.assertEquals(towerDTO.getTarget().getId(),units.get(0).getId());
        units.get(0).setX(1000);
        TowerAttack.attack(units,towerDTO);
        Assertions.assertEquals(towerDTO.getTarget().getId(),units.get(0).getId());
        units.get(0).setHealth(0);
        TowerAttack.attack(units,towerDTO);
        Assertions.assertEquals(towerDTO.getTarget().getId(),units.get(0).getId());
    }
    @Test
    public void CheckIfUnitGetRemovedAsTargetIfRequiremenetNotMet(){
        ArrayList<UnitDTO> units= new ArrayList<>();
        units.add(new UnitDTO(1,100,10,10,19,19,0,0,1,
                26,26,"Test",231,new ArrayList<>(),new ArrayList<>(),0l));
        units.add(new UnitDTO(1,100,10,10,19,19,0,0,1,
                26,26,"Test",232,new ArrayList<>(),new ArrayList<>(),0l));
        units.add(new UnitDTO(1,100,10,10,19,19,0,0,1,
                26,26,"Test",233,new ArrayList<>(),new ArrayList<>(),0l));
        units.add(new UnitDTO(1,100,10,10,19,19,0,0,1,
                26,26,"Test",234,new ArrayList<>(),new ArrayList<>(),0l));
        units.add(new UnitDTO(1,100,10,10,19,19,0,0,1,
                26,26,"Test",235,new ArrayList<>(),new ArrayList<>(),0l));
        TowerDTO towerDTO = new TowerDTO(20,20,5,10,
                10,null,1f,1L,23123,"Test");
        TowerAttack.attack(units,towerDTO);
        Assertions.assertEquals(towerDTO.getTarget().getId(),units.get(0).getId());
        units.get(0).setX(1000);
        TowerAttack.attack(units,towerDTO);
        Assertions.assertEquals(towerDTO.getTarget().getId(),units.get(0).getId());
        units.get(0).setY(1000);
        TowerAttack.attack(units,towerDTO);
        Assertions.assertEquals(towerDTO.getTarget().getId(),units.get(0).getId());
        units.get(0).setX(1000);
        TowerAttack.attack(units,towerDTO);
        Assertions.assertEquals(towerDTO.getTarget().getId(),units.get(0).getId());
        units.get(0).setHealth(0);
        TowerAttack.attack(units,towerDTO);
        Assertions.assertEquals(towerDTO.getTarget().getId(),units.get(0).getId());
    }
    @Test
    public void AttackUnit(){
        ArrayList<UnitDTO> units= new ArrayList<>();
        units.add(new UnitDTO(1,100,10,10,19,19,0,0,1,
                26,26,"Test",231,new ArrayList<>(),new ArrayList<>(),0l));
        TowerDTO towerDTO = new TowerDTO(20,20,5,10,
                10,null,1f,1L,23123,"Test");
        for(int i=95;i>0;i-=5) {
            TowerAttack.attack(units, towerDTO);
            Assertions.assertEquals(i, units.get(0).getHealth());
            towerDTO.setLastTimeOfAttack(towerDTO.getLastTimeOfAttack()-10000);
        }
    }
}
