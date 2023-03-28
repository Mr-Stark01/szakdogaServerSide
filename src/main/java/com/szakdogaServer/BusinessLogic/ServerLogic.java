package com.szakdogaServer.BusinessLogic;

import org.datatransferobject.DTO;
import org.datatransferobject.TowerDTO;
import org.datatransferobject.UnitDTO;

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
                throw new RuntimeException(e);
            }

            for(DTO dto:DTOList){
                for(UnitDTO unitDTO:dto.getUnitDTOs()) {
                    if(unitDTO.getId()==0){
                        unitDTO.setId(getNewId());
                    }
                    pathFinder.checkNextStep(unitDTO);
                }
                for(TowerDTO towerDTO:dto.getTowerDTOs()){
                    TowerAttack.attack(dto.getUnitDTOs(),towerDTO);//TODO currently using the same dto data not enemy data
                }
            }
            blockingQueueOut.add(DTOList);
            blockingQueueOut.add(DTOList);
            while(!blockingQueueOut.isEmpty()){

            }
            DTOList.clear();
        }

    }

    public void setPlayerDTO(DTO dto) {
        DTOList.add(dto);
    }
}
