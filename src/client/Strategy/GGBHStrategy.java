
package client.Strategy;

import client.Exception.NotEnoughApException;
import client.Strategy.PartOfStrategy.AttackStrategy.FirstNotLinearAttackStrategy;
import client.Strategy.PartOfStrategy.DodgeAndMoveStrategy.GGBHMoveAndDodgeStrategy;
import client.Strategy.PartOfStrategy.GuardStrategy.FirstGuardStrategy;
import client.Strategy.PartOfStrategy.HealStrategy.FirstHealStrategy;
import client.Strategy.PartOfStrategy.PartOfStrategy;
import client.model.*;

public class GGBHStrategy extends Strategy {
    private GGBHMoveAndDodgeStrategy ggbhMoveAndDodgeStrategy = new GGBHMoveAndDodgeStrategy();
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
        firstGuardStrategy = new FirstGuardStrategy(myHeroes[0].getId(), myHeroes[1].getId());
        firstHealStrategy = new FirstHealStrategy(myHeroes[3].getId());
        for (int i = 0; i < 4; i++) {
            firstNotLinearAttackStrategies[i] = new FirstNotLinearAttackStrategy(myHeroes[i].getId());
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
