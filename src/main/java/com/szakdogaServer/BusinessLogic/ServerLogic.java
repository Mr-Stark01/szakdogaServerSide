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
            System.out.println("here");
            try {
                DTOList.add(blockingQueueIn.take());
                DTOList.add(blockingQueueIn.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("here2");
            for(DTO dto:DTOList){
                System.out.println("loop");
                for(UnitDTO unitDTO:dto.getUnitDTOs()) {
                    if(unitDTO.getId()==0){
                        System.out.println("loop2");
                        unitDTO.setId(getNewId());
                        System.out.println("loop2.5");
                        pathFinder.calculateNextStep(unitDTO);
                        System.out.println("loop2.5");
                        pathFinder.calculateAngle(unitDTO);
                        System.out.println("loop2.5");
                    }
                    System.out.println("loop3");
                    pathFinder.checkNextStep(unitDTO);
                    System.out.println(unitDTO.getId());
                }
                for(TowerDTO towerDTO:dto.getTowerDTOs()){
                    TowerAttack.attack(dto.getUnitDTOs(),towerDTO);//TODO currently using the same dto data not enemy data
                }
            }
            System.out.println("here3");
            synchronized (blockingQueueOut) {
                ArrayList<DTO> tmp  =new ArrayList<DTO>();
                tmp.addAll(DTOList);
                ArrayList<DTO> tmp2 =new ArrayList<DTO>();
                tmp2.addAll(DTOList);
                blockingQueueOut.offer(DTOList);
                blockingQueueOut.offer(DTOList);
            }
            System.out.println("here4");
            while(!blockingQueueOut.isEmpty()){

            }
            DTOList.clear();
        }

    }

    public void setPlayerDTO(DTO dto) {
        DTOList.add(dto);
    }
}
