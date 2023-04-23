package com.szakdogaServer.BusinessLogic;

import com.szakdogaServer.DataBase.DB;
import org.datatransferobject.DTO;
import org.datatransferobject.TowerDTO;
import org.datatransferobject.UnitDTO;

import java.util.Date;

import static com.szakdogaServer.BusinessLogic.IdCreator.getNewId;

/**
 * This class facilitates everything that only requires 1 dto and not both.
 */
public class DTOLogic {
    int playerCount=1;
    PathFinder pathFinder;
    public DTOLogic(PathFinder pathFinder){
        this.pathFinder=pathFinder;

    }
    public void checkIfDTOHasCorrectPossition(DTO dto, DB db){
        if(dto.getPlayerDTO().getPositionX()==-1 || dto.getPlayerDTO().getPositionY()==-1) {
            dto.getPlayerDTO().setPositionX(db.getPlayerPositionX(playerCount));
            dto.getPlayerDTO().setPositionY(db.getPlayerPositionY(playerCount));
            playerCount++;
        }
        playerCount=playerCount>2?1:2;
    }
    public void setupNewUnits(DTO dto,UnitDTO unitDTO){
        if(unitDTO.getId()==0){
            if(dto.getPlayerDTO().getMoney()-unitDTO.getPrice()<0){
                unitDTO.setId(-1);
            }
            else {
                dto.getPlayerDTO().setMoney(dto.getPlayerDTO().getMoney()-unitDTO.getPrice());
                unitDTO.setId(getNewId());
                pathFinder.setupNextTiles(unitDTO);
                pathFinder.calculateAngle(unitDTO);
            }
        }
    }
    public void step(UnitDTO unitDTO){
        if(unitDTO.getId()!=-1 && !unitDTO.getNextX().contains(-1)) {
            pathFinder.checkNextStep(unitDTO);
        }
        else if(unitDTO.getNextX().contains(-1) && unitDTO.getNextX().size()>1 && unitDTO.getLastStep() + ((int)(1000/unitDTO.getSpeed())) <= new Date().getTime()){
            unitDTO.getNextX().remove(0);
            unitDTO.getNextY().remove(0);
            unitDTO.setLastStep(new Date().getTime());
        }
    }
    public void checkIfPlayerCanCreateTower(DTO dto,DTO enemyDTO,TowerDTO towerDTO){
        if(towerDTO.getId()==0){
            if(dto.getPlayerDTO().getMoney()-towerDTO.getPrice()<0){
                towerDTO.setId(-1);
                return;
            }
            for(TowerDTO towerDTO1:dto.getTowerDTOs()){
                if(towerDTO != towerDTO1 && towerDTO.getX()==towerDTO1.getX() && towerDTO.getY()==towerDTO1.getY()){
                    towerDTO.setId(-1);
                    return;
                }
            }
            for(TowerDTO towerDTO1:enemyDTO.getTowerDTOs()){
                if( towerDTO != towerDTO1 && towerDTO.getX()==towerDTO1.getX() && towerDTO.getY()==towerDTO1.getY()){
                    towerDTO.setId(-1);
                    return;
                }
            }
            dto.getPlayerDTO().setMoney(dto.getPlayerDTO().getMoney()-towerDTO.getPrice());
            towerDTO.setId(getNewId());
        }
    }
}
