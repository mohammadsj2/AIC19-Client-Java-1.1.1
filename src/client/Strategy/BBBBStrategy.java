package client.Strategy;

import client.Exception.NotEnoughApException;
import client.Strategy.PartOfStrategy.AttackStrategy.FirstLinearAttackStrategy;
import client.Strategy.PartOfStrategy.AttackStrategy.FirstNotLinearAttackStrategy;
import client.Strategy.PartOfStrategy.AttackStrategy.SecondLinearAttackStrategy;
import client.Strategy.PartOfStrategy.AttackStrategy.ThirdLinearAttackStrategy;
import client.Strategy.PartOfStrategy.BombStrategy.FirstBombStrategy;
import client.Strategy.PartOfStrategy.BombStrategy.SecondBombStrategy;
import client.Strategy.PartOfStrategy.BombStrategy.ThirdBombStrategy;
import client.Strategy.PartOfStrategy.DodgeAndMoveStrategy.FirstMoveAndDodgeStrategy;
import client.Strategy.PartOfStrategy.DodgeAndMoveStrategy.SecondMoveAndDodgeStrategy;
import client.Strategy.PartOfStrategy.DodgeAndMoveStrategy.ThirdMoveAndDodgeStrategy;
import client.Strategy.PartOfStrategy.PartOfStrategy;
import client.Strategy.Tools.BFS;
import client.model.*;

import java.util.ArrayList;

public class BBBBStrategy extends Strategy {
    public static final int NUMBER_OF_HEROES = 8;
    private int cnt = 0;
    private ArrayList<PartOfStrategy> partOfStrategies = new ArrayList<>();
    private Boolean partOfStrategiesInited = false;

    int[] healths = new int[NUMBER_OF_HEROES];

    private void initStrategy(World world) {
        Hero[] myHeroes = world.getMyHeroes();
        for (Hero hero : myHeroes) {
            //partOfStrategies.add(new ThirdBombStrategy(PartOfStrategy.INFINIT_AP, hero.getId(), null));
            partOfStrategies.add(new FirstBombStrategy(PartOfStrategy.INFINIT_AP, hero.getId()));
        }
        for (Hero hero : myHeroes) {
            //partOfStrategies.add(new ThirdLinearAttackStrategy(PartOfStrategy.INFINIT_AP, hero.getId(), null));
            partOfStrategies.add(new FirstLinearAttackStrategy(PartOfStrategy.INFINIT_AP, hero.getId()));
        }
        partOfStrategiesInited = true;
    }

    @Override
    public void pickTurn(World world) {
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
        BFS bfs = new BFS(world.getMap());
        partOfStrategies.add(new ThirdMoveAndDodgeStrategy(PartOfStrategy.INFINIT_AP, bfs));
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

        for (int i = 0; i < NUMBER_OF_HEROES; i++)
            if (world.getHero(i).getCurrentCell() == null)
                healths[i] = 0;
            else healths[i] = world.getHero(i).getCurrentHP();

        for (PartOfStrategy partOfStrategy : partOfStrategies) {
            try {
                partOfStrategy.actionTurn(world);
            } catch (NotEnoughApException e) {
                e.printStackTrace();
            }
        }
    }

}
