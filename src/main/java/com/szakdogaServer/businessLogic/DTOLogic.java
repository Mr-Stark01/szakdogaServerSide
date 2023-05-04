package com.szakdogaServer.businessLogic;

import com.szakdogaServer.dataBase.DB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datatransferobject.DTO;
import org.datatransferobject.TowerDTO;
import org.datatransferobject.UnitDTO;

import java.util.Date;

import static com.szakdogaServer.businessLogic.IdCreator.getNewId;

/**
 * This class facilitates everything that only requires 1 dto and not both.
 */
public class DTOLogic {
    private static int playerCount = 1;
    private static PathFinder pathFinder = new PathFinder();
    private static Logger logger = LogManager.getLogger(DTOLogic.class);

    public DTOLogic() {

    }

    public static void checkIfDTOHasCorrectPossition(DTO dto, final DB db) {
        if (dto.getPlayerDTO().getPositionX() == Flags.OUT_OF_BOUNDS_INDEX
                || dto.getPlayerDTO().getPositionY() == Flags.OUT_OF_BOUNDS_INDEX) {
            dto.getPlayerDTO().setPositionX(db.getPlayerPositionX(playerCount));
            dto.getPlayerDTO().setPositionY(db.getPlayerPositionY(playerCount));
            playerCount++;
        }
        playerCount = playerCount > 2 ? 1 : 2;
    }

    public static void setupNewUnits(DTO dto, UnitDTO unitDTO) {
        if (unitDTO.getId() == Flags.TO_BE_INITIALIZED_ID) {
            if (!hasEnoughMoney(dto, unitDTO.getPrice())) {
                unitDTO.setId(Flags.FOR_REMOVAL_ID);
            } else {
                //Paying for tower
                dto.getPlayerDTO().setMoney(dto.getPlayerDTO().getMoney() - unitDTO.getPrice());
                unitDTO.setId(getNewId());
                pathFinder.setupNextTiles(unitDTO);
                pathFinder.calculateAngle(unitDTO);

            }
            if (isOutOfBounds(unitDTO)) {
                unitDTO.setId(Flags.FOR_REMOVAL_ID);
            }
        }
    }

    public static void step(UnitDTO unitDTO) {
        if (unitDTO.getId() != Flags.FOR_REMOVAL_ID && !isOutOfBounds(unitDTO)) {
            pathFinder.checkNextStep(unitDTO);
        } else if (isOutOfBounds(unitDTO) &&
                unitDTO.getLastStep() + ((int) (1000 / unitDTO.getSpeed())) <= new Date().getTime()) {
            unitDTO.getNextX().remove(0);
            unitDTO.getNextY().remove(0);
            unitDTO.setLastStep(new Date().getTime());
        }
    }

    public static void checkIfPlayerCanCreateTower(DTO dto, DTO enemyDTO, TowerDTO towerDTO) {
        if (towerDTO.getId() == Flags.TO_BE_INITIALIZED_ID) {
            if (!hasEnoughMoney(dto, towerDTO.getPrice())) {
                towerDTO.setId(Flags.FOR_REMOVAL_ID);
                return;
            }
            if (!pathFinder.canBuildThere(towerDTO.getX(), towerDTO.getY())) {
                towerDTO.setId(Flags.FOR_REMOVAL_ID);
                return;
            }
            //Already has tower placed by me
            for (TowerDTO tower : dto.getTowerDTOs()) {
                if (towerDTO != tower && towerDTO.getX() == tower.getX() && towerDTO.getY() == tower.getY()) {
                    towerDTO.setId(Flags.FOR_REMOVAL_ID);
                    return;
                }
            }
            //Already has tower placed by Enemy
            for (TowerDTO tower : enemyDTO.getTowerDTOs()) {
                if (towerDTO != tower && towerDTO.getX() == tower.getX() && towerDTO.getY() == tower.getY()) {
                    towerDTO.setId(Flags.FOR_REMOVAL_ID);
                    return;
                }
            }
            dto.getPlayerDTO().setMoney(dto.getPlayerDTO().getMoney() - towerDTO.getPrice());
            towerDTO.setId(getNewId());
        }
    }

    private static boolean hasEnoughMoney(DTO dto, int price) {
        return dto.getPlayerDTO().getMoney() - price >= 0;
    }

    private static boolean isOutOfBounds(UnitDTO unitDTO) {
        return unitDTO.getNextX().contains(Flags.OUT_OF_BOUNDS_INDEX) || unitDTO.getNextY().contains(Flags.OUT_OF_BOUNDS_INDEX);
    }
}
