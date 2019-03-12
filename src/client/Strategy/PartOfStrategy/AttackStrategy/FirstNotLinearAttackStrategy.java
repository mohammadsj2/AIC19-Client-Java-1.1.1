package client.Strategy.PartOfStrategy.AttackStrategy;

import client.Exception.NotEnoughApException;
import client.Strategy.PartOfStrategy.PartOfStrategy;
import client.model.*;

import java.util.ArrayList;

public class FirstNotLinearAttackStrategy extends PartOfStrategy {
    private Hero hero;

    public FirstNotLinearAttackStrategy(int maxAp, Hero hero) {
        super(maxAp);
        this.hero = hero;
    }

    @Override
    public void actionTurn(World world) throws NotEnoughApException {
        super.actionTurn(world);
        Ability attackAbility = hero.getOffensiveAbilities()[0];
        Cell targetCell1 = getCellWithMostOppHeroesForNotLinearAbilities(world, hero.getCurrentCell(), attackAbility.getName());
        castAbility(world, hero, targetCell1, attackAbility.getName());
    }
}
