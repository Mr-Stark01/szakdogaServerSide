package com.szakdogaServer.BusinessLogic;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import org.datatransferobject.UnitDTO;

public class PathFinder {
    private TiledMapTileLayer tiledMapTileLayer;

    public PathFinder() {
        TmxMapLoader loader = new TmxMapLoader();
        TiledMap map = loader.load("maps/defmap.tmx");
        tiledMapTileLayer = (TiledMapTileLayer) map.getLayers().get(0);

    }

    public void checkNextStep(UnitDTO unit) {
        int X = Math.round(unit.getX());
        int Y = Math.round(unit.getY());
        unit.setX(X);
        unit.setY(Y);


        if (tiledMapTileLayer.getCell(X, Y + 1).getTile().getProperties().containsKey("road") &&
                !(unit.getPreviousX() == X && Y + 1 == unit.getPreviousY())) {
            unit.setNextX(X);
            unit.setNextY(Y + 1);
            unit.setPreviousX(X);
            unit.setPreviousY(Y);
            return;//TODO végig irni
        }
        if (tiledMapTileLayer.getCell(X, Y - 1).getTile().getProperties().containsKey("road") &&
                !(unit.getPreviousX() == X && Y - 1 == unit.getPreviousY())) {
            unit.setNextX(X);
            unit.setNextY(Y - 1);
            unit.setPreviousX(X);
            unit.setPreviousY(Y);
            return;
        }
        if (tiledMapTileLayer.getCell(X + 1, Y + 1).getTile().getProperties().containsKey("road") &&
                !(unit.getPreviousX() == X + 1 && Y + 1 == unit.getPreviousY())) {
            unit.setNextX(X + 1);
            unit.setNextY(Y + 1);
            unit.setPreviousX(X);
            unit.setPreviousY(Y);
            return;
        }
        if (tiledMapTileLayer.getCell(X + 1, Y - 1).getTile().getProperties().containsKey("road") &&
                !(unit.getPreviousX() == X + 1 && Y - 1 == unit.getPreviousY())) {
            unit.setNextX(X + 1);
            unit.setNextY(Y - 1);
            unit.setPreviousX(X);
            unit.setPreviousY(Y);
            return;
        }
        if (tiledMapTileLayer.getCell(X + 1, Y).getTile().getProperties().containsKey("road") &&
                !(unit.getPreviousX() == X + 1 && Y == unit.getPreviousY())) {
            unit.setNextX(X + 1);
            unit.setNextY(Y);
            unit.setPreviousX(X);
            unit.setPreviousY(Y);
            return;
        }
        if (tiledMapTileLayer.getCell(X - 1, Y + 1).getTile().getProperties().containsKey("road") &&
                !(unit.getPreviousX() == X - 1 && Y + 1 == unit.getPreviousY())) {
            unit.setNextX(X - 1);
            unit.setNextY(Y + 1);
            unit.setPreviousX(X);
            unit.setPreviousY(Y);
            return;
        }
        if (tiledMapTileLayer.getCell(X - 1, Y - 1).getTile().getProperties().containsKey("road") &&
                !(unit.getPreviousX() == X - 1 && Y - 1 == unit.getPreviousY())) {
            unit.setNextX(X - 1);
            unit.setNextY(Y - 1);
            unit.setPreviousX(X);
            unit.setPreviousY(Y);
            return;
        }
        if (tiledMapTileLayer.getCell(X - 1, Y).getTile().getProperties().containsKey("road") &&
                !(unit.getPreviousX() == X - 1 && Y == unit.getPreviousY())) {
            unit.setNextX(X - 1);
            unit.setNextY(Y);
            unit.setPreviousX(X);
            unit.setPreviousY(Y);
            return;
        }
    }
}
