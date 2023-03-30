package com.szakdogaServer.BusinessLogic;

import org.datatransferobject.TowerDTO;
import org.datatransferobject.UnitDTO;

import java.util.Date;
import java.util.List;

public class TowerAttack {

    public static void checkIfEnemyStillInRangeAndAllive(List<UnitDTO> units,TowerDTO towerDTO){//What a beauty
        UnitDTO target =towerDTO.getTarget();
        if(target==null){
            findTarget(units,towerDTO);
        }

        else if(target.getHealth() <=0){
            units.remove(towerDTO.getTarget());
            findTarget(units,towerDTO);
        }
        else if(
                Math.sqrt((Math.pow(target.getX() - towerDTO.getX(), 2)) +
                        (Math.pow(target.getY() - towerDTO.getY(), 2))) > towerDTO.getRange()){
            findTarget(units,towerDTO);
        }
    }
    public static void attack(List<UnitDTO> units,TowerDTO towerDTO){
        if(new Date().getTime()-towerDTO.getLastTimeOfAttack() > towerDTO.getAttackTime()){
            checkIfEnemyStillInRangeAndAllive(units,towerDTO);
            if(towerDTO.getTarget()!=null){
                towerDTO.getTarget().setHealth(towerDTO.getTarget().getHealth()-towerDTO.getDamage());// TODO
            }
            towerDTO.setLastTimeOfAttack(new Date().getTime());
        }
    }
    public static void findTarget(List<UnitDTO> units, TowerDTO towerDTO){
        if(towerDTO.getTarget() != null){
            return;
        }
        for(UnitDTO unit:units) {
            if(
                    Math.sqrt((Math.pow(unit.getX() - towerDTO.getX(), 2)) +
                            (Math.pow(unit.getY() - towerDTO.getY(), 2)))< towerDTO.getRange()){
                towerDTO.setTarget(unit);
                return;
            }
        }
    }
}
