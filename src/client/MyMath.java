package client;

import java.util.Random;

public class MyMath {
    private static Random random = new Random();
    public static int getRandomIntegerLessThan(int x) {
        return ((random.nextInt() % x) + x) % x;
    }
}
