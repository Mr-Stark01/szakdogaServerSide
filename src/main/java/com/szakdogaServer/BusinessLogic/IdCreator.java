package com.szakdogaServer.BusinessLogic;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class IdCreator {
    static final int UPPERBOUND=Integer.MAX_VALUE;
    static final int LOWERBOUND=1;

    private static Set<Integer> usedIds= new LinkedHashSet<>();
    public synchronized static int getNewId(){
        int number = ThreadLocalRandom.current().nextInt(LOWERBOUND, UPPERBOUND);
        while(!usedIds.add(number)){
            number = ThreadLocalRandom.current().nextInt(LOWERBOUND, UPPERBOUND);
        }
        return number;
    }
}
