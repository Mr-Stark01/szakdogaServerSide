package com.szakdogaServer.BusinessLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.MathUtils;
import org.datatransferobject.UnitDTO;


public class PathFinder {
    private TiledMapTileLayer tiledMapTileLayer;

    public PathFinder() {
        TmxMapLoader loader = new TmxMapLoader();
        TiledMap map = loader.load("maps/defmap.tmx");
        tiledMapTileLayer = (TiledMapTileLayer) map.getLayers().get(0);

    }

    public void checkNextStep(UnitDTO unit) {
        if(Math.sqrt((Math.pow(unit.getX()-unit.getNextX(),2))+(Math.pow(unit.getY()-unit.getNextY(),2))) < 0.1f){ //TODO ha túll gyors vagy lassan fút a játék átugorhat pontot
            calculateNextStep(unit);
            calculateAngle(unit);
        }
        else{
            if(Math.sqrt((Math.pow(unit.getX()-unit.getNextX(),2))+(Math.pow(unit.getY()-unit.getNextY(),2))) > 1.51f){
                unit.setX(unit.getNextX());
                unit.setY(unit.getNextY());
            }
        }
    }

    private void calculateAngle(UnitDTO unit) {
        float angle = MathUtils.atan2( unit.getNextY() - unit.getY(), unit.getNextX() - unit.getX());
        unit.setDeltaX = MathUtils.cos(angle);
        unit.setDeltaY = MathUtils.sin(angle);
    }

    public void calculateNextStep(UnitDTO unit){
        int X = Math.round(unit.getX());
        int Y = Math.round(unit.getY());

        if (tiledMapTileLayer.getCell(X, Y + 1).getTile().getProperties().containsKey("road") &&
                !(unit.getPreviousX() == X && Y + 1 == unit.getPreviousY())) {
            unit.setNextX(X);
            unit.setNextY(Y + 1);
            setPreviousCoordinates(X,Y,unit);
            return;
        }
        if (tiledMapTileLayer.getCell(X, Y - 1).getTile().getProperties().containsKey("road") &&
                !(unit.getPreviousX() == X && Y - 1 == unit.getPreviousY())) {
            unit.setNextX(X);
            unit.setNextY(Y - 1);
            setPreviousCoordinates(X,Y,unit);
            return;
        }
        if (tiledMapTileLayer.getCell(X - 1, Y).getTile().getProperties().containsKey("road") &&
                !(unit.getPreviousX() == X - 1 && Y == unit.getPreviousY())) {
            unit.setNextX(X - 1);
            unit.setNextY(Y);
            setPreviousCoordinates(X,Y,unit);
            return;
        }
        if (tiledMapTileLayer.getCell(X + 1, Y).getTile().getProperties().containsKey("road") &&
                !(unit.getPreviousX() == X + 1 && Y == unit.getPreviousY())) {
            unit.setNextX(X + 1);
            unit.setNextY(Y);
            setPreviousCoordinates(X,Y,unit);
            return;
        }
        if (tiledMapTileLayer.getCell(X + 1, Y + 1).getTile().getProperties().containsKey("road") &&
                !(unit.getPreviousX() == X + 1 && Y + 1 == unit.getPreviousY())) {
            unit.setNextX(X + 1);
            unit.setNextY(Y + 1);
            setPreviousCoordinates(X,Y,unit);
            return;
        }
        if (tiledMapTileLayer.getCell(X + 1, Y - 1).getTile().getProperties().containsKey("road") &&
                !(unit.getPreviousX() == X + 1 && Y - 1 == unit.getPreviousY())) {
            unit.setNextX(X + 1);
            unit.setNextY(Y - 1);
            setPreviousCoordinates(X,Y,unit);
            return;
        }

        if (tiledMapTileLayer.getCell(X - 1, Y + 1).getTile().getProperties().containsKey("road") &&
                !(unit.getPreviousX() == X - 1 && Y + 1 == unit.getPreviousY())) {
            unit.setNextX(X - 1);
            unit.setNextY(Y + 1);
            setPreviousCoordinates(X,Y,unit);
            return;
        }
        if (tiledMapTileLayer.getCell(X - 1, Y - 1).getTile().getProperties().containsKey("road") &&
                !(unit.getPreviousX() == X - 1 && Y - 1 == unit.getPreviousY())) {
            unit.setNextX(X - 1);
            unit.setNextY(Y - 1);
            setPreviousCoordinates(X,Y,unit);
        }
    }
    private void setPreviousCoordinates(int X,int Y,UnitDTO unit){
        unit.setPreviousX(X);
        unit.setPreviousY(Y);
    }

}
