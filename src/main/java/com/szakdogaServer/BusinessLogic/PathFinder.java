package com.szakdogaServer.BusinessLogic;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.MathUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datatransferobject.UnitDTO;

import java.util.Date;


public class PathFinder {
    private TiledMapTileLayer tiledMapTileLayer;
    private Logger logger;

    public PathFinder() {
        logger = LogManager.getLogger(PathFinder.class);
        TmxMapLoader loader = new TmxMapLoader();
        TiledMap map = loader.load("src/main/resources/maps/defmap.tmx");
        tiledMapTileLayer = (TiledMapTileLayer) map.getLayers().get(0);
    }

    /**
     * sets up at least the next 6 steps for lag comp reasons
     * @param unit
     */
    public void setupNextTiles(UnitDTO unit){
        logger.info("setuping up new unit");
        calculateNextStep(unit,Math.round(unit.getX()),Math.round(unit.getY()),unit.getPreviousX(), unit.getPreviousY());
        calculateNextStep(unit,unit.getNextX().get(unit.getNextX().size()-1),unit.getNextY().get(unit.getNextY().size()-1),unit.getPreviousX(), unit.getPreviousY());
        for(int i=0;i<4;i++){
            calculateNextStep(unit,unit.getNextX().get(unit.getNextX().size()-1),unit.getNextY().get(unit.getNextY().size()-1),unit.getNextX().get(unit.getNextX().size()-2), unit.getNextY().get(unit.getNextY().size()-2));
        }
        unit.setLastStep(new Date().getTime());
        logger.info("unit setup with atleast 6 steps");
    }

    /**
     * Moves the unit from a logical side and forces it if necesarry into new position
     * @param unit
     */
    public void checkNextStep(UnitDTO unit) {
        if(unit.getNextX().size()<6 || unit.getNextY().size() < 6) {
            logger.info("Less than 6 steps remaning.");
            calculateNextStep(unit, unit.getNextX().get(unit.getNextX().size()-1), unit.getNextY().get(unit.getNextY().size()-1), unit.getNextX().get(unit.getNextX().size() - 2), unit.getNextY().get(unit.getNextY().size() - 2));
        }
        float distance = (float) Math.sqrt((Math.pow(unit.getPreviousX()-unit.getNextX().get(0),2))+(Math.pow(unit.getPreviousY()-unit.getNextY().get(0),2)));
        if((unit.getLastStep() + (long)(((1000/unit.getSpeed()))*distance)) <= new Date().getTime()){ //The conversion is necesary otherwise it's treated as string which is bad
            logger.info("The unit traveled for the alocated time on this tile");
            int X=unit.getNextX().remove(0);
            int Y=unit.getNextY().remove(0);
            unit.setX(X);
            unit.setY(Y);
            setPreviousCoordinates(X,Y,unit);
            calculateNextStep(unit, unit.getNextX().get(unit.getNextX().size() - 1) , unit.getNextY().get(unit.getNextY().size()- 1 ) ,
                    unit.getNextX().get(unit.getNextX().size() - 2), unit.getNextY().get(unit.getNextY().size() - 2));
            unit.setLastStep(new Date().getTime());
        }
        else{
            if(Math.sqrt((Math.pow(unit.getX()-unit.getNextX().get(0),2))+(Math.pow(unit.getY()-unit.getNextY().get(0),2))) > 1.51f){
                logger.info("The unit strayed too far away from the roda forcefully move it back and steps it forward");
                unit.setX(unit.getNextX().remove(0));
                unit.setY(unit.getNextY().remove(0));
                setPreviousCoordinates((int) unit.getX(), (int) unit.getY(),unit);
                calculateNextStep(unit, unit.getNextX().get(unit.getNextX().size() - 1) , unit.getNextY().get(unit.getNextY().size()- 1 ) ,
                        unit.getNextX().get(unit.getNextX().size() - 2), unit.getNextY().get(unit.getNextY().size() - 2));
                unit.setLastStep(new Date().getTime());
            }
        }
    }

    public void calculateAngle(UnitDTO unit) {
        float angle = MathUtils.atan2( unit.getNextY().get(0) - unit.getY(), unit.getNextX().get(0) - unit.getX());
        unit.setDeltaX(MathUtils.cos(angle));
        unit.setDeltaY(MathUtils.sin(angle));
    }

    public void calculateNextStep(UnitDTO unit,int X,int Y,int previousX,int previousY){
        /*for(int i=-1;i<=1;i++){
            for(int j=-1;j<=1;j++){
                if (tiledMapTileLayer.getCell(X + i, Y + j).getTile().getProperties().containsKey("road") &&
                        !(previousX == X + i && Y + j == previousY)) {
                    unit.getNextX().add(X + i);
                    unit.getNextY().add(Y + j);
                    setPreviousCoordinates(X,Y,unit);
                    return;
                }
            }
        }*/
        if (tiledMapTileLayer.getCell(X, Y + 1).getTile().getProperties().containsKey("road") &&
                !(previousX == X && Y + 1 == previousY)) {
            unit.getNextX().add(X);
            unit.getNextY().add(Y + 1);
           
            return;
        }
        if (tiledMapTileLayer.getCell(X, Y - 1).getTile().getProperties().containsKey("road") &&
                !(previousX == X && Y - 1 == previousY)) {
            unit.getNextX().add(X);
            unit.getNextY().add(Y - 1);
           
            return;
        }
        if (tiledMapTileLayer.getCell(X - 1, Y).getTile().getProperties().containsKey("road") &&
                !(previousX == X - 1 && Y == previousY)) {
            unit.getNextX().add(X - 1);
            unit.getNextY().add(Y);
           
            return;
        }
        if (tiledMapTileLayer.getCell(X + 1, Y).getTile().getProperties().containsKey("road") &&
                !(previousX == X + 1 && Y == previousY)) {
            unit.getNextX().add(X + 1);
            unit.getNextY().add(Y);
           
            return;
        }
        if (tiledMapTileLayer.getCell(X + 1, Y + 1).getTile().getProperties().containsKey("road") &&
                !(previousX == X + 1 && Y + 1 == previousY)) {
            unit.getNextX().add(X + 1);
            unit.getNextY().add(Y + 1);
           
            return;
        }
        if (tiledMapTileLayer.getCell(X + 1, Y - 1).getTile().getProperties().containsKey("road") &&
                !(previousX == X + 1 && Y - 1 == previousY)) {
            unit.getNextX().add(X + 1);
            unit.getNextY().add(Y - 1);
           
            return;
        }

        if (tiledMapTileLayer.getCell(X - 1, Y + 1).getTile().getProperties().containsKey("road") &&
                !(previousX == X - 1 && Y + 1 == previousY)) {
            unit.getNextX().add(X - 1);
            unit.getNextY().add(Y + 1);
           
            return;
        }
        if (tiledMapTileLayer.getCell(X - 1, Y - 1).getTile().getProperties().containsKey("road") &&
                !(previousX == X - 1 && Y - 1 == previousY)) {
            unit.getNextX().add(X - 1);
            unit.getNextY().add(Y - 1);
           
            return;
        }
        unit.getNextX().add(-1);
        unit.getNextY().add(-1);
    }
    private void setPreviousCoordinates(int X,int Y,UnitDTO unit){
        unit.setPreviousX(X);
        unit.setPreviousY(Y);
    }

}
