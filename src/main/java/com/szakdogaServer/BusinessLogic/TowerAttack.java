package com.szakdogaServer.BusinessLogic;

import net.bytebuddy.dynamic.DynamicType;
import org.datatransferobject.TowerDTO;
import org.datatransferobject.UnitDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TowerAttack {

    public static void checkIfEnemyStillInRangeAndAllive(List<UnitDTO> units,TowerDTO towerDTO){
        UnitDTO target = getTarget(units,towerDTO);
        if (target != null) {
            towerDTO.setTarget(deepCopyUnit(target));
            if(target.getHealth() <=0){
                units.remove(towerDTO.getTarget());
                towerDTO.setTarget(null);
                findTarget(units,towerDTO);
            }
            else if(!isInRange(towerDTO,target)){
                towerDTO.setTarget(null);
                findTarget(units,towerDTO);
            }
        } else {
            findTarget(units, towerDTO);
        }

    }
    public static int attack(List<UnitDTO> units,TowerDTO towerDTO){
        if(towerDTO.getLastTimeOfAttack()+(long)(towerDTO.getAttackTime()*1000f) <= new Date().getTime() && towerDTO.getId() != Flags.FOR_REMOVAL_ID){
            checkIfEnemyStillInRangeAndAllive(units,towerDTO);
            if(towerDTO.getTarget()!=null){
                UnitDTO tmpTarget=getTarget(units,towerDTO);
                tmpTarget.setHealth(towerDTO.getTarget().getHealth()-towerDTO.getDamage());
                towerDTO.setLastTimeOfAttack(new Date().getTime());
                tmpTarget.setId(tmpTarget.getHealth()<0?Flags.FOR_REMOVAL_ID:tmpTarget.getId());
                towerDTO.getTarget().setHealth(tmpTarget.getHealth());
                return tmpTarget.getPrice()/2;
            }
        }
        return 0;
    }
    public static void findTarget(List<UnitDTO> units, TowerDTO towerDTO){
        for(UnitDTO unit:units) {
            if(isInRange(towerDTO,unit)){
                towerDTO.setTarget(deepCopyUnit(unit));
            }
            else{
                towerDTO.setTarget(null);
            }
        }
    }

    /**
     * Find the target in the UnitList and returns it
     * @param units
     * @param towerDTO
     * @return
     */
    private static UnitDTO getTarget(List<UnitDTO> units,TowerDTO towerDTO){
        if(towerDTO.getTarget()==null){
            return null;
        }
        for(UnitDTO unit:units){
            if(unit.getId()==towerDTO.getTarget().getId()){
                return unit;
            }
        }
        return null;
    }
    private static ArrayList<Integer> deepcopy(ArrayList<Integer> nextList) {
        ArrayList<Integer> copy=new ArrayList<>();
        for(int elem:nextList){
            copy.add(elem);
        }
        return copy;
    }
    private static UnitDTO deepCopyUnit(UnitDTO target){
        return new UnitDTO(
                target.getSpeed(),
                target.getHealth(),
                target.getDamage(),
                target.getPrice(),
                target.getPreviousX(),
                target.getPreviousY(),
                target.getDeltaX(),
                target.getDeltaY(),
                target.getDistance(),
                target.getX(),
                target.getY(),
                new String(target.getUnitClass()),
                target.getId(),
                deepcopy((ArrayList<Integer>) target.getNextX()),
                deepcopy((ArrayList<Integer>) target.getNextY()),
                target.getLastStep());
    }
    //(Math.sqrt(Math.pow(target.getX() - towerDTO.getX(), 2) +
    //                    Math.pow(target.getY() - towerDTO.getY(), 2)) > towerDTO.getRange()){
    private static boolean isInRange(TowerDTO towerDTO, UnitDTO target){
        return Math.sqrt(Math.pow(target.getX() - towerDTO.getX(), 2) +
                Math.pow(target.getY() - towerDTO.getY(), 2)) <= towerDTO.getRange();
    }
}
