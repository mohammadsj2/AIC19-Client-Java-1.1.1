package client.Strategy.PartOfStrategy.AttackStrategy;

import client.Exception.NotEnoughApException;
import client.Strategy.PartOfStrategy.PartOfStrategy;
import client.model.*;

import java.util.ArrayList;

public class FirstNotLinearAttackStrategy extends PartOfStrategy {
    private int heroId;

    public FirstNotLinearAttackStrategy(int maxAp, int hero) {
        super(maxAp);
        this.heroId = hero;
    }

    @Override
    public void actionTurn(World world) throws NotEnoughApException {
        Hero hero = world.getHero(heroId);
        super.actionTurn(world);
        Ability attackAbility = hero.getOffensiveAbilities()[0];
        Cell targetCell1 = getCellWithMostOppHeroesForNotLinearAbilities(world, hero.getCurrentCell(), attackAbility.getName());
        if (targetCell1 != null)
            castAbility(world, hero, targetCell1, attackAbility.getName());
    }
}
