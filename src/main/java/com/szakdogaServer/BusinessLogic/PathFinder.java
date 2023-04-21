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
        calculateNextStep(unit,unit.getNextX().get(unit.getNextX().size()-1),unit.getNextY().get(unit.getNextY().size()-1),unit.getPreviousX(), unit.getPreviousY());
        for(int i=0;i<4;i++){
            calculateNextStep(unit,unit.getNextX().get(unit.getNextX().size()-1),unit.getNextY().get(unit.getNextY().size()-1),unit.getNextX().get(unit.getNextX().size()-2), unit.getNextY().get(unit.getNextY().size()-2));
        }
        unit.setLastStep(new Date().getTime());
    }

    public void checkNextStep(UnitDTO unit) {
        System.out.println("Enters checkNextStep");
        if(unit.getNextX().size()<6 || unit.getNextY().size() < 6) {
            System.out.println("Calculate next step if less than 6");
            calculateNextStep(unit, unit.getNextX().get(unit.getNextX().size()-1), unit.getNextY().get(unit.getNextY().size()-1), unit.getNextX().get(unit.getNextX().size() - 2), unit.getNextY().get(unit.getNextY().size() - 2));
        }
            //1000 + 500 <= 2000
        if(unit.getLastStep() + ((int)(1000/unit.getSpeed())) <= new Date().getTime()){ //TODO ha túll gyors vagy lassan fút a játék átugorhat pontot
            System.out.println("Calculate next step and removes if according to timer already should be past it");
            System.out.println((unit.getLastStep()+(1000/unit.getSpeed())));
            System.out.println((1000/unit.getSpeed()));
            System.out.println(new Date().getTime());
            int X=unit.getNextX().remove(0);
            System.out.println("remvoeX"+X);
            //unit.setX(X);
            System.out.println("setx"+X);
            int Y=unit.getNextY().remove(0);
            System.out.println("remvoeY"+Y);
            //unit.setY(Y);
            System.out.println("setY");


            System.out.println(unit.getNextX().get(unit.getNextX().size()- 1));
            System.out.println(unit.getNextY().get(unit.getNextY().size()- 1));
            System.out.println(unit.getNextX().get(unit.getNextX().size()- 2));
            System.out.println(unit.getNextY().get(unit.getNextY().size()- 2));
            calculateNextStep(unit, unit.getNextX().get(unit.getNextX().size() - 1) , unit.getNextY().get(unit.getNextY().size()- 1 ) ,
                    unit.getNextX().get(unit.getNextX().size() - 2), unit.getNextY().get(unit.getNextY().size() - 2));
            unit.setLastStep(new Date().getTime());
        }
        else{
            if(Math.sqrt((Math.pow(unit.getX()-unit.getNextX().get(0),2))+(Math.pow(unit.getY()-unit.getNextY().get(0),2))) > 2f){
                System.out.println("Force");
                System.out.println(Math.sqrt((Math.pow(unit.getX()-unit.getNextX().get(0),2))+(Math.pow(unit.getY()-unit.getNextY().get(0),2))));
                System.out.println("X:"+unit.getX());
                System.out.println("xNEXT:"+unit.getNextX().get(0));
                System.out.println("Y:"+unit.getY());
                System.out.println("YNEXT:"+unit.getNextY().get(0));


                unit.setX(unit.getNextX().remove(0));
                unit.setY(unit.getNextY().remove(0));
                System.out.println("removes and reset position");
                calculateNextStep(unit, unit.getNextX().get(unit.getNextX().size() - 1) , unit.getNextY().get(unit.getNextY().size()- 1 ) ,
                        unit.getNextX().get(unit.getNextX().size() - 2), unit.getNextY().get(unit.getNextY().size() - 2));
                unit.setLastStep(new Date().getTime());
                System.out.println("leaves force");
            }
        }
        System.out.println("Exit checkNextStep");
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
        System.out.println("cords"+X+"\t"+Y);
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
            return;
        }
        System.out.println("baddddddddddddd");
        unit.getNextX().add(-1);
        unit.getNextY().add(-1);
    }
    private void setPreviousCoordinates(int X,int Y,UnitDTO unit){
        unit.setPreviousX(X);
        unit.setPreviousY(Y);
    }

}
