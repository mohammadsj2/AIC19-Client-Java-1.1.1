package client;

import client.Strategy.GGBHStrategy;
import client.Strategy.RandomStrategy;
import client.Strategy.Strategy;
import client.model.*;

public class AI {
    public static Strategy strategy=new GGBHStrategy();

    public void preProcess(World world) {
        System.out.println("pre process started");
        strategy.preProcess(world);
    }

    public void pickTurn(World world) {
        System.out.println("pick started");
        strategy.pickTurn(world);
    }

    public void moveTurn(World world) {
        System.out.println("move started");
        strategy.moveTurn(world);
    }

    public void actionTurn(World world) {
        System.out.println("action started");
        strategy.actionTurn(world);
    }
}
