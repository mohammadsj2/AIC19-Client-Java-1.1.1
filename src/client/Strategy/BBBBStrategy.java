package client.Strategy;

import client.Exception.NotEnoughApException;
import client.Strategy.PartOfStrategy.AttackStrategy.FirstAttackStrategy;
import client.Strategy.PartOfStrategy.BombStrategy.FirstBombStrategy;
import client.Strategy.PartOfStrategy.DodgeAndMoveStrategy.FirstMoveAndDodgeStrategy;
import client.Strategy.PartOfStrategy.PartOfStrategy;
import client.model.*;

public class BBBBStrategy extends Strategy {
    private int cnt = 0;
    private Boolean partOfStrategiesInited = false;

    public void initStrategy(World world) {
        Hero[] myHeroes = world.getMyHeroes();
        for (Hero hero : myHeroes) {
            partOfStrategies.add(new FirstBombStrategy(world.getMaxAP(), hero));
        }
        for (Hero hero : myHeroes) {
            partOfStrategies.add(new FirstAttackStrategy(world.getMaxAP(), hero));
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
        partOfStrategies.add(new FirstMoveAndDodgeStrategy(world.getMaxAP()));
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
