package client;


import client.Strategy.*;
import client.Strategy.PartOfStrategy.NullStrategy;
import client.model.*;

public class AI {

    public static Strategy strategy= new BBBBStrategy();


    public void preProcess(World world) {
        //System.out.println("pre process started");
        strategy.preProcess(world);
    }

    public void pickTurn(World world) {
        //System.out.println("pick started");
        strategy.pickTurn(world);
    }

    public void moveTurn(World world) {
        //System.out.println("move started");
        strategy.moveTurn(world);
    }

    public void actionTurn(World world) {
        //System.out.println("actionTurn started");
        strategy.actionTurn(world);
    }
}
