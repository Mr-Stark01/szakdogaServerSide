package com.szakdogaServer.BusinessLogic;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

public class IdCreator {
    private static Set<Integer> usedIds= new LinkedHashSet<>();
    public static int getNewId(){
        Random random = new Random();
        int number = random.nextInt();
        while(!usedIds.add(number)){
            number = random.nextInt();
        }
        return number;
    }
}
