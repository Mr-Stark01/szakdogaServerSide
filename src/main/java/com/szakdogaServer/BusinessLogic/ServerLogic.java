package com.szakdogaServer.BusinessLogic;

import org.datatransferobject.DTO;
import org.datatransferobject.PlayerDTO;
import org.datatransferobject.TowerDTO;
import org.datatransferobject.UnitDTO;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import static com.szakdogaServer.BusinessLogic.IdCreator.getNewId;

public class ServerLogic implements Runnable{
    private BlockingQueue<DTO> blockingQueueIn;
    private BlockingQueue<ArrayList<DTO>> blockingQueueOut;
    private PathFinder pathFinder;
    private ArrayList<DTO> DTOList = new ArrayList<>();
    public ServerLogic(BlockingQueue<DTO> blockingQueueIn,BlockingQueue<ArrayList<DTO>> blockingQueueOut){
        this.blockingQueueIn=blockingQueueIn;
        this.blockingQueueOut=blockingQueueOut;
        pathFinder = new PathFinder();
    }

    @Override
    public void run() {
        while (true){
            try {
                DTOList.add(blockingQueueIn.take());
                DTOList.add(blockingQueueIn.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for(DTO dto:DTOList){
                DTO enemyDTO = null;
                if(dto==DTOList.get(0)){
                    enemyDTO = DTOList.get(1);
                }
                else{
                    enemyDTO =DTOList.get(0);
                }
                for(UnitDTO unitDTO:dto.getUnitDTOs()) {
                    if(unitDTO.getId()==0){
                        unitDTO.setId(getNewId());
                        pathFinder.setupNextTiles(unitDTO);
                        pathFinder.calculateAngle(unitDTO);
                    }
                    pathFinder.checkNextStep(unitDTO);
                    //AbstractMap.SimpleEntry<Integer,Integer>simple = new AbstractMap.SimpleEntry<>(1,1);
                }
                for(TowerDTO towerDTO:dto.getTowerDTOs()){
                    if(towerDTO.getId()==0){
                        towerDTO.setId(getNewId());
                    }
                    TowerAttack.attack(enemyDTO.getUnitDTOs(),towerDTO);//TODO currently using the same dto data not enemy data
                }
                System.out.println("asd4");
            }
            synchronized (blockingQueueOut) {
                System.out.println("asd4.5");
                System.out.println(blockingQueueOut.isEmpty());
                blockingQueueOut.offer(deepCopy(DTOList));
                System.out.println("asd4.8");
                blockingQueueOut.offer(deepCopy(DTOList));
            }
            System.out.println("asd5");
            while(!blockingQueueOut.isEmpty()){

            }
            System.out.println("asd6");
            DTOList.clear();
        }

    }

    public void setPlayerDTO(DTO dto) {
        DTOList.add(dto);
    }
    public ArrayList<DTO> deepCopy(ArrayList<DTO> DTOList){
        System.out.println("deepcopy");
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
                        unit.getNextY()));
            }
            ArrayList<TowerDTO> towerCopy = new ArrayList<>();
            for(TowerDTO tower:dto.getTowerDTOs()) {
                if (tower.getTarget() != null) {
                    UnitDTO unit = tower.getTarget();
                    towerCopy.add(new TowerDTO(tower.getDamage(),
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
                                    unit.getNextY()),
                            tower.getAttackTime(),
                            tower.getDeltaSum(),
                            tower.getId(),
                            tower.getTowerClass()));
                }
                else{
                    towerCopy.add(new TowerDTO(tower.getDamage(),
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
        System.out.println("deepcopy2");
        return copy;
    }
}
