package com.szakdogaServer.BusinessLogic;

import com.szakdogaServer.DataBase.DB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datatransferobject.DTO;
import org.datatransferobject.PlayerDTO;
import org.datatransferobject.TowerDTO;
import org.datatransferobject.UnitDTO;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class ServerLogic implements Runnable{
    private BlockingQueue<DTO> blockingQueueIn;
    private BlockingQueue<ArrayList<DTO>> blockingQueueOut;
    private PathFinder pathFinder;
    private ArrayList<DTO> DTOList = new ArrayList<>();
    private DB db = new DB();
    private Logger logger;
    private DTOLogic dtoLogic;
    public ServerLogic(BlockingQueue<DTO> blockingQueueIn,BlockingQueue<ArrayList<DTO>> blockingQueueOut){
        this.blockingQueueIn=blockingQueueIn;
        this.blockingQueueOut=blockingQueueOut;
        pathFinder = new PathFinder();
        logger = LogManager.getLogger(ServerLogic.class);
        dtoLogic=new DTOLogic(pathFinder);
    }

    @Override
    public void run() {
        while (true){
            try {
                DTOList.add(blockingQueueIn.poll(10, TimeUnit.SECONDS));
                DTOList.add(blockingQueueIn.poll(10, TimeUnit.SECONDS));
            } catch (InterruptedException e) {
                logger.error("BlockingQueue thrown InteruptedException.Since only server should be capable of starting a shutdown this shouldn't happen");
                logger.trace(e.getMessage());
            }
            for(DTO dto:DTOList){
                dtoLogic.checkIfDTOHasCorrectPossition(dto,db);
                DTO enemyDTO = null;
                if(dto==DTOList.get(0)){
                    enemyDTO = DTOList.get(1);
                }
                else{
                    enemyDTO = DTOList.get(0);
                }
                logger.info("Players stats checked");
                for(UnitDTO unitDTO:dto.getUnitDTOs()) {
                    dtoLogic.setupNewUnits(dto,unitDTO);
                    attackBase(unitDTO, enemyDTO.getPlayerDTO());
                    dtoLogic.step(unitDTO);
                }
                logger.info("Units step has been made");
                for(TowerDTO towerDTO:dto.getTowerDTOs()){
                    dtoLogic.checkIfPlayerCanCreateTower(dto,enemyDTO,towerDTO);
                    dto.getPlayerDTO().setMoney(dto.getPlayerDTO().getMoney()+TowerAttack.attack(enemyDTO.getUnitDTOs(),towerDTO));
                }
                logger.info("Towers attacked");
                if(dto.getPlayerDTO().getHealth()<=0){
                    logger.info("Game finished one player reached 0 health");
                    dto.setId(-3);//-3 loss
                    enemyDTO.setId(-4);//-4 win
                    db.addNameToDb(dto.getName(),dto.getPlayerDTO().getMoney(),0,1);
                    db.addNameToDb(enemyDTO.getName(),enemyDTO.getPlayerDTO().getMoney(),1,0);
                    break;
                }
            }
            synchronized (blockingQueueOut) {
                blockingQueueOut.offer(deepCopy(DTOList));
                blockingQueueOut.offer(deepCopy(DTOList));
            }
            while(!blockingQueueOut.isEmpty()){

            }
            DTOList.clear();
        }

    }

    /**
     * Checks if unit can attack enemy base
     * @param unitDTO
     * @param playerDTO
     */
    private void attackBase(UnitDTO unitDTO, PlayerDTO playerDTO) {
        if(unitDTO.getNextX().size()==1 && unitDTO.getNextX().get(0)==-1 && unitDTO.getNextY().get(0)==-1){
            playerDTO.setHealth(playerDTO.getHealth()-unitDTO.getDamage());
            unitDTO.setId(-1);
        }
    }

    /**
     * Creates a copy of DTOList with new memory.
     * @param DTOList
     * @return
     */
    public ArrayList<DTO> deepCopy(ArrayList<DTO> DTOList){
        ArrayList<DTO> copy= new ArrayList<>();
        for(DTO dto:DTOList){
            ArrayList<UnitDTO> unitCopy = new ArrayList<>();
            for(UnitDTO unit:dto.getUnitDTOs()){
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
            for(TowerDTO tower:dto.getTowerDTOs()) {
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
                            tower.getDeltaSum(),
                            tower.getId(),
                            tower.getTowerClass()));
                }
                else{
                    towerCopy.add(new TowerDTO(
                            tower.getX(),
                            tower.getY(),
                            tower.getDamage(),
                            tower.getPrice(),
                            tower.getRange(),
                            null,
                            tower.getAttackTime(),
                            tower.getDeltaSum(),
                            tower.getId(),
                            tower.getTowerClass()));
                }
            }
            PlayerDTO player= dto.getPlayerDTO();
            PlayerDTO playerCopy =new PlayerDTO(player.getMoney(),
                    player.getPositionX(),
                    player.getPositionY(),
                    player.getHealth());
            copy.add(new DTO(unitCopy,towerCopy,playerCopy,dto.getId()));
        }
        return copy;
    }
}
