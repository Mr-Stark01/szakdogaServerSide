package com.szakdogaServer.businessLogic;

import com.szakdogaServer.dataBase.DB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datatransferobject.DTO;
import org.datatransferobject.PlayerDTO;
import org.datatransferobject.TowerDTO;
import org.datatransferobject.UnitDTO;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class ServerLogic implements Runnable {
    private static Logger logger;
    private BlockingQueue<DTO> blockingQueueIn;
    private BlockingQueue<ArrayList<DTO>> blockingQueueOut;
    private ArrayList<DTO> DTOList = new ArrayList<>();
    private DB db;

    public ServerLogic(BlockingQueue<DTO> blockingQueueIn, BlockingQueue<ArrayList<DTO>> blockingQueueOut, DB db) {
        this.blockingQueueIn = blockingQueueIn;
        this.blockingQueueOut = blockingQueueOut;
        logger = LogManager.getLogger(ServerLogic.class);
        this.db = db;
    }

    @Override
    public void run() {
        while (true) {
            try {
                DTOList.add(blockingQueueIn.take());
                DTOList.add(blockingQueueIn.take());
                blockingQueueIn.clear();
            } catch (InterruptedException e) {
                logger.error("BlockingQueue thrown InteruptedException.Since only server should be capable of starting a shutdown this shouldn't happen");
                logger.trace(e.getMessage());
            }
            for (DTO dto : DTOList) {
                DTOLogic.checkIfDTOHasCorrectPossition(dto, db);
                DTO enemyDTO = DTOList.get(0) == dto ? DTOList.get(1) : DTOList.get(0);
                logger.info("Players stats checked");
                for (UnitDTO unitDTO : dto.getUnitDTOs()) {
                    DTOLogic.setupNewUnits(dto, unitDTO);
                    attackBase(unitDTO, enemyDTO.getPlayerDTO());
                    DTOLogic.step(unitDTO);
                }
                logger.info("Units step has been made");
                for (TowerDTO towerDTO : dto.getTowerDTOs()) {
                    DTOLogic.checkIfPlayerCanCreateTower(dto, enemyDTO, towerDTO);
                    dto.getPlayerDTO().setMoney(dto.getPlayerDTO().getMoney() + TowerAttack.attack(enemyDTO.getUnitDTOs(), towerDTO));
                }
                logger.info("Towers attacked");
                if (dto.getPlayerDTO().getHealth() <= 0) {
                    logger.info("Game finished one player reached 0 health");
                    db.addNameToDb(dto.getName(), dto.getPlayerDTO().getMoney(), 0, 1);
                    db.addNameToDb(enemyDTO.getName(), enemyDTO.getPlayerDTO().getMoney(), 1, 0);
                    break;
                }
            }
            synchronized (blockingQueueOut) {
                blockingQueueOut.offer(deepCopy(DTOList));
                blockingQueueOut.offer(deepCopy(DTOList));
            }
            while (!blockingQueueOut.isEmpty()) {

            }
            DTOList.clear();
        }

    }

    /**
     * Checks if unit can attack enemy base
     *
     * @param unitDTO
     * @param playerDTO
     */
    private void attackBase(UnitDTO unitDTO, PlayerDTO playerDTO) {
        if (unitDTO.getNextX().size() == 1 && unitDTO.getNextX().get(0) == -1 && unitDTO.getNextY().get(0) == -1) {
            playerDTO.setHealth(playerDTO.getHealth() - unitDTO.getDamage());
            unitDTO.setId(-1);
        }
    }

    /**
     * Creates a copy of DTOList with new memory.
     *
     * @param DTOList
     * @return
     */
    public ArrayList<DTO> deepCopy(ArrayList<DTO> DTOList) {
        ArrayList<DTO> copy = new ArrayList<>();
        for (DTO dto : DTOList) {
            ArrayList<UnitDTO> unitCopy = new ArrayList<>();
            for (UnitDTO unit : dto.getUnitDTOs()) {
                unitCopy.add(new UnitDTO(
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
                        unit.getUnitClass(),
                        unit.getId(),
                        unit.getNextX(),
                        unit.getNextY(),
                        unit.getLastStep()));
            }
            ArrayList<TowerDTO> towerCopy = new ArrayList<>();
            for (TowerDTO tower : dto.getTowerDTOs()) {
                if (tower.getTarget() != null) {
                    UnitDTO unit = tower.getTarget();
                    towerCopy.add(new TowerDTO(
                            tower.getX(),
                            tower.getY(),
                            tower.getDamage(),
                            tower.getPrice(),
                            tower.getRange(),
                            new UnitDTO(
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
                                    unit.getUnitClass(),
                                    unit.getId(),
                                    unit.getNextX(),
                                    unit.getNextY(),
                                    unit.getLastStep()),
                            tower.getAttackTime(),
                            tower.getLastTimeOfAttack(),
                            tower.getId(),
                            tower.getTowerClass()));
                } else {
                    towerCopy.add(new TowerDTO(
                            tower.getX(),
                            tower.getY(),
                            tower.getDamage(),
                            tower.getPrice(),
                            tower.getRange(),
                            null,
                            tower.getAttackTime(),
                            tower.getLastTimeOfAttack(),
                            tower.getId(),
                            tower.getTowerClass()));
                }
            }
            PlayerDTO player = dto.getPlayerDTO();
            PlayerDTO playerCopy = new PlayerDTO(player.getMoney(),
                    player.getPositionX(),
                    player.getPositionY(),
                    player.getHealth());
            if(dto.getMessage()!=null){
                copy.add(new DTO(unitCopy, towerCopy, playerCopy, dto.getId(), dto.getName(),dto.getMessage()));
            }
            else {
                copy.add(new DTO(unitCopy, towerCopy, playerCopy, dto.getId(), dto.getName(),null));
            }
        }
        return copy;
    }
}
