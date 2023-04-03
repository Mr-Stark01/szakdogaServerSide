package com.szakdogaServer.BusinessLogic;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.MathUtils;
import org.datatransferobject.UnitDTO;

import java.util.Date;


public class PathFinder {
    private TiledMapTileLayer tiledMapTileLayer;

    public PathFinder() {

        TmxMapLoader loader = new TmxMapLoader();
        TiledMap map = loader.load("src/main/resources/maps/defmap.tmx");
        tiledMapTileLayer = (TiledMapTileLayer) map.getLayers().get(0);
    }
    public void setupNextTiles(UnitDTO unit){
        calculateNextStep(unit,Math.round(unit.getX()),Math.round(unit.getY()),unit.getPreviousX(), unit.getPreviousY());
        calculateNextStep(unit,unit.getNextX().get(unit.getNextX().size()),unit.getNextY().get(unit.getNextY().size()),unit.getPreviousX(), unit.getPreviousY());
        for(int i=0;i<4;i++){
            calculateNextStep(unit,unit.getNextX().get(unit.getNextX().size()),unit.getNextY().get(unit.getNextY().size()),unit.getNextX().get(unit.getNextX().size()-1), unit.getNextY().get(unit.getNextY().size()-1));
        }
    }

    public void checkNextStep(UnitDTO unit) {
        if(unit.getLastStep() +((1000/unit.getSpeed())) > new Date().getTime()){ //TODO ha túll gyors vagy lassan fút a játék átugorhat pontot
            unit.getNextX().remove(0);
            unit.getNextY().remove(0);
            calculateNextStep(unit,unit.getNextX().get(unit.getNextX().size()),unit.getNextY().get(unit.getNextY().size()),unit.getNextX().get(unit.getNextX().size()-1), unit.getNextY().get(unit.getNextY().size()-1));
            unit.setLastStep(new Date().getTime());
        }
        else{
            if(Math.sqrt((Math.pow(unit.getX()-unit.getNextX().get(0),2))+(Math.pow(unit.getY()-unit.getNextY().get(0),2))) > 1.51f){
                unit.setX(unit.getNextX().get(0));
                unit.setY(unit.getNextY().get(0));
            }
        }
    }

    public void calculateAngle(UnitDTO unit) {
        float angle = MathUtils.atan2( unit.getNextY().get(0) - unit.getY(), unit.getNextX().get(0) - unit.getX());
        unit.setDeltaX(MathUtils.cos(angle));
        unit.setDeltaY(MathUtils.sin(angle));
    }

    public void calculateNextStep(UnitDTO unit,int X,int Y,int previousX,int previousY){
        if (tiledMapTileLayer.getCell(X, Y + 1).getTile().getProperties().containsKey("road") &&
                !(previousX == X && Y + 1 == previousY)) {
            unit.getNextX().add(X);
            unit.getNextY().add(Y + 1);
            setPreviousCoordinates(X,Y,unit);
            return;
        }
        if (tiledMapTileLayer.getCell(X, Y - 1).getTile().getProperties().containsKey("road") &&
                !(previousX == X && Y - 1 == previousY)) {
            unit.getNextX().add(X);
            unit.getNextY().add(Y - 1);
            setPreviousCoordinates(X,Y,unit);
            return;
        }
        if (tiledMapTileLayer.getCell(X - 1, Y).getTile().getProperties().containsKey("road") &&
                !(previousX == X - 1 && Y == previousY)) {
            unit.getNextX().add(X - 1);
            unit.getNextY().add(Y);
            setPreviousCoordinates(X,Y,unit);
            return;
        }
        if (tiledMapTileLayer.getCell(X + 1, Y).getTile().getProperties().containsKey("road") &&
                !(previousX == X + 1 && Y == previousY)) {
            unit.getNextX().add(X + 1);
            unit.getNextY().add(Y);
            setPreviousCoordinates(X,Y,unit);
            return;
        }
        if (tiledMapTileLayer.getCell(X + 1, Y + 1).getTile().getProperties().containsKey("road") &&
                !(previousX == X + 1 && Y + 1 == previousY)) {
            unit.getNextX().add(X + 1);
            unit.getNextY().add(Y + 1);
            setPreviousCoordinates(X,Y,unit);
            return;
        }
        if (tiledMapTileLayer.getCell(X + 1, Y - 1).getTile().getProperties().containsKey("road") &&
                !(previousX == X + 1 && Y - 1 == previousY)) {
            unit.getNextX().add(X + 1);
            unit.getNextY().add(Y - 1);
            setPreviousCoordinates(X,Y,unit);
            return;
        }

        if (tiledMapTileLayer.getCell(X - 1, Y + 1).getTile().getProperties().containsKey("road") &&
                !(previousX == X - 1 && Y + 1 == previousY)) {
            unit.getNextX().add(X - 1);
            unit.getNextY().add(Y + 1);
            setPreviousCoordinates(X,Y,unit);
            return;
        }
        if (tiledMapTileLayer.getCell(X - 1, Y - 1).getTile().getProperties().containsKey("road") &&
                !(previousX == X - 1 && Y - 1 == previousY)) {
            unit.getNextX().add(X - 1);
            unit.getNextY().add(Y - 1);
            setPreviousCoordinates(X,Y,unit);
        }
    }
    private void setPreviousCoordinates(int X,int Y,UnitDTO unit){
        unit.setPreviousX(X);
        unit.setPreviousY(Y);
    }

}
