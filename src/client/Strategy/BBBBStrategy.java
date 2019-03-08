package client.Strategy;

import client.Exception.NotEnoughApException;
import client.Strategy.PartOfStrategy.AttackStrategy.FirstNotLinearAttackStrategy;
import client.Strategy.PartOfStrategy.BombStrategy.FirstBombStrategy;
import client.Strategy.PartOfStrategy.DodgeAndMoveStrategy.FirstMoveAndDodgeStrategy;
import client.Strategy.PartOfStrategy.PartOfStrategy;
import client.model.*;

import java.util.ArrayList;

public class BBBBStrategy extends Strategy {
    private int cnt = 0;
    private ArrayList<PartOfStrategy> partOfStrategies = new ArrayList<>();
    private Boolean partOfStrategiesInited = false;

    private void initStrategy(World world) {
        Hero[] myHeroes = world.getMyHeroes();
        for (Hero hero : myHeroes) {
            partOfStrategies.add(new FirstBombStrategy(PartOfStrategy.INFINIT_AP, hero));
        }
        for (Hero hero : myHeroes) {
            partOfStrategies.add(new FirstNotLinearAttackStrategy(PartOfStrategy.INFINIT_AP, hero));
        }
        partOfStrategiesInited = true;
    }

    @Override
    public void pickTurn(World world) {
        System.out.println(world.getCurrentTurn());
        switch (cnt) {
            case 0:
                world.pickHero(HeroName.BLASTER);
                break;
            case 1:
                world.pickHero(HeroName.BLASTER);
                break;
            case 2:
                world.pickHero(HeroName.BLASTER);
                break;
            case 3:
                world.pickHero(HeroName.BLASTER);
                break;
        }
        cnt++;
    }

    @Override
    public void preProcess(World world) {
        partOfStrategies.add(new FirstMoveAndDodgeStrategy(PartOfStrategy.INFINIT_AP));
        for (PartOfStrategy partOfStrategy : partOfStrategies) {
            partOfStrategy.preProcess(world);
        }
    }

    @Override
    public void moveTurn(World world) {
        if (!partOfStrategiesInited)
            initStrategy(world);
        for (PartOfStrategy partOfStrategy : partOfStrategies) {
            try {
                partOfStrategy.moveTurn(world);
            } catch (NotEnoughApException ignored) {

            }
        }
    }


    @Override
    public void actionTurn(World world) {
        for (PartOfStrategy partOfStrategy : partOfStrategies) {
            try {
                partOfStrategy.actionTurn(world);
            } catch (NotEnoughApException ignored) {

            }
        }
    }

}
