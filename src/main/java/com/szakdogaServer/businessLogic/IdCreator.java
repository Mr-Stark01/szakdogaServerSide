package com.szakdogaServer.businessLogic;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class IdCreator {
    private static final int LOWERBOUND = 1;

    private static Set<Integer> usedIds = new LinkedHashSet<>();

    public synchronized static int getNewId() {
        int number = ThreadLocalRandom.current().nextInt(LOWERBOUND, Integer.MAX_VALUE);
        while (!usedIds.add(number)) {
            number = ThreadLocalRandom.current().nextInt(LOWERBOUND, Integer.MAX_VALUE);
        }
        return number;
    }
}
