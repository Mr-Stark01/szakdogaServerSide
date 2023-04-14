package com.szakdogaServer.BusinessLogic;

import org.datatransferobject.TowerDTO;
import org.datatransferobject.UnitDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TowerAttack {

    public static void checkIfEnemyStillInRangeAndAllive(List<UnitDTO> units,TowerDTO towerDTO){//What a beauty
        System.out.println("asd1");
        UnitDTO target =towerDTO.getTarget();
        if(target==null){
            findTarget(units,towerDTO);
        }
        else if(target != null && target.getHealth() <=0){
            units.remove(towerDTO.getTarget());
            findTarget(units,towerDTO);
        }
        else if(target != null &&
                Math.sqrt((Math.pow(target.getX() - towerDTO.getX(), 2)) +
                        (Math.pow(target.getY() - towerDTO.getY(), 2))) > towerDTO.getRange()){
            findTarget(units,towerDTO);
        }
        System.out.println("asd2");
    }
    public static void attack(List<UnitDTO> units,TowerDTO towerDTO){
        if(new Date().getTime()-towerDTO.getLastTimeOfAttack() > towerDTO.getAttackTime() && towerDTO.getId() != -1){
            checkIfEnemyStillInRangeAndAllive(units,towerDTO);
            if(towerDTO.getTarget()!=null){
                getTarget(units,towerDTO).setHealth(towerDTO.getTarget().getHealth()-towerDTO.getDamage());
                towerDTO.setLastTimeOfAttack(new Date().getTime());
            }
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
                towerDTO.setTarget(new UnitDTO(
                        unit.getSpeed(),
                        unit.getHealth(),
                        unit.getDamage(),
                        unit.getPrice(),
                        unit.getPreviousX(),
                        unit.getPreviousY(),
                        unit.getDeltaX(),
                        unit.getDeltaY(),
                        unit.getDistance(),
                        unit.getX(),
                        unit.getY(),
                        new String(unit.getUnitClass()),
                        unit.getId(),
                        deepcopy((ArrayList<Integer>) unit.getNextX()),
                        deepcopy((ArrayList<Integer>) unit.getNextY())));
                return;
            }
        }
    }
    private static UnitDTO getTarget(List<UnitDTO> units,TowerDTO towerDTO){
        for(UnitDTO unit:units){
            if(unit.getId()==towerDTO.getTarget().getId()){
                return unit;
            }
        }
        return null;
    }
    private static ArrayList<Integer> deepcopy(ArrayList<Integer> nextList) {
        ArrayList<Integer> copy=new ArrayList<Integer>();
        for(int elem:nextList){
            copy.add(elem);
        }
        return copy;
    }
}
