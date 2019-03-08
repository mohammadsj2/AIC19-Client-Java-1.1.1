
package client.Strategy;

import client.Exception.NotEnoughApException;
import client.Strategy.PartOfStrategy.AttackStrategy.FirstNotLinearAttackStrategy;
import client.Strategy.PartOfStrategy.DodgeAndMoveStrategy.GGBHMoveAndDodgeStrategy;
import client.Strategy.PartOfStrategy.GuardStrategy.FirstGuardStrategy;
import client.Strategy.PartOfStrategy.HealStrategy.FirstHealStrategy;
import client.Strategy.PartOfStrategy.PartOfStrategy;
import client.model.*;

public class GGBHStrategy extends Strategy {
    private GGBHMoveAndDodgeStrategy ggbhMoveAndDodgeStrategy = new GGBHMoveAndDodgeStrategy(PartOfStrategy.INFINIT_AP);
    private FirstGuardStrategy firstGuardStrategy;
    private FirstHealStrategy firstHealStrategy;
    private FirstNotLinearAttackStrategy[] firstNotLinearAttackStrategies = new FirstNotLinearAttackStrategy[4];

    @Override
    public void preProcess(World world) {
        ggbhMoveAndDodgeStrategy.preProcess(world);
    }

    private Boolean partOfStrategiesInited = false;

    public void initStrategy(World world) {
        Hero[] myHeroes = world.getMyHeroes();
        firstGuardStrategy = new FirstGuardStrategy(PartOfStrategy.INFINIT_AP, myHeroes[0], myHeroes[1]);
        firstHealStrategy = new FirstHealStrategy(PartOfStrategy.INFINIT_AP, myHeroes[3]);
        for (int i = 0; i < 4; i++) {
            firstNotLinearAttackStrategies[i] = new FirstNotLinearAttackStrategy(PartOfStrategy.INFINIT_AP, myHeroes[i]);
        }
        partOfStrategiesInited = true;
    }

    private int cnt = 0;

    @Override
    public void pickTurn(World world) {
        switch (cnt) {
            case 0:
                world.pickHero(HeroName.GUARDIAN);
                break;
            case 1:
                world.pickHero(HeroName.GUARDIAN);
                break;
            case 2:
                world.pickHero(HeroName.BLASTER);
                break;
            case 3:
                world.pickHero(HeroName.HEALER);
                break;
        }
        cnt++;
    }

    @Override
    public void moveTurn(World world) {
        if (!partOfStrategiesInited)
            initStrategy(world);
        try {
            ggbhMoveAndDodgeStrategy.moveTurn(world);
        } catch (NotEnoughApException ignored) {

        }
    }

    @Override
    public void actionTurn(World world) {
        try {
            firstGuardStrategy.actionTurn(world);
            firstHealStrategy.actionTurn(world);
            for (int i = 0; i < 4; i++) {
                firstNotLinearAttackStrategies[i].actionTurn(world);
            }
            ggbhMoveAndDodgeStrategy.actionTurn(world);
        } catch (NotEnoughApException ignored) {

        }
    }


}
