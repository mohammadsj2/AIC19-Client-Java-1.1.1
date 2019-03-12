
package client.Strategy;

import client.Exception.NotEnoughApException;
import client.Strategy.PartOfStrategy.AttackStrategy.FirstNotLinearAttackStrategy;
import client.Strategy.PartOfStrategy.DodgeAndMoveStrategy.FirstMoveAndDodgeStrategy;
import client.Strategy.PartOfStrategy.HealStrategy.FirstHealStrategy;
import client.Strategy.PartOfStrategy.PartOfStrategy;
import client.model.*;

public class HHHHStrategy extends Strategy {
    private FirstHealStrategy[] firstHealStrategies = new FirstHealStrategy[4];
    private FirstNotLinearAttackStrategy[] firstNotLinearAttackStrategies = new FirstNotLinearAttackStrategy[4];
    private FirstMoveAndDodgeStrategy firstMoveAndDodgeStrategy = new FirstMoveAndDodgeStrategy(PartOfStrategy.INFINIT_AP);

    @Override
    public void preProcess(World world) {
        firstMoveAndDodgeStrategy.preProcess(world);
    }

    private Boolean partOfStrategiesInited = false;

    public void initStrategy(World world) {
        Hero[] myHeroes = world.getMyHeroes();
        for (int i = 0; i < 4; i++) {
            firstHealStrategies[i] = new FirstHealStrategy(PartOfStrategy.INFINIT_AP, myHeroes[i]);
            firstNotLinearAttackStrategies[i] = new FirstNotLinearAttackStrategy(PartOfStrategy.INFINIT_AP, myHeroes[i]);
        }
        partOfStrategiesInited = true;
    }

    private int cnt = 0;

    @Override
    public void pickTurn(World world) {
        switch (cnt) {
            case 0:
                world.pickHero(HeroName.HEALER);
                break;
            case 1:
                world.pickHero(HeroName.HEALER);
                break;
            case 2:
                world.pickHero(HeroName.HEALER);
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
            firstMoveAndDodgeStrategy.moveTurn(world);
        } catch (NotEnoughApException ignored) {

        }
    }

    @Override
    public void actionTurn(World world) {
        try {
            for (int i = 0; i < 4; i++)
                firstMoveAndDodgeStrategy.actionTurn(world);
            for (int i = 0; i < 4; i++)
                firstHealStrategies[i].actionTurn(world);
            for (int i = 0; i < 4; i++)
                firstNotLinearAttackStrategies[i].actionTurn(world);
        } catch (NotEnoughApException ignored) {

        }
    }


}
