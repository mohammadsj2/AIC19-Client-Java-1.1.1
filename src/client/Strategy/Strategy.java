package client.Strategy;

import client.Strategy.PartOfStrategy.PartOfStrategy;
import client.model.*;

import java.util.ArrayList;

public abstract class Strategy {
    public static final int INF_DISTANCE = 1000;

    protected ArrayList<PartOfStrategy> strategies=new ArrayList<>();
    public abstract void preProcess(World world);

    public abstract void pickTurn(World world);

    public abstract void moveTurn(World world);

    public abstract void actionTurn(World world);


}
