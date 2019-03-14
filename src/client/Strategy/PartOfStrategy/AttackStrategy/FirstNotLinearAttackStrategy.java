package client.Strategy.PartOfStrategy.AttackStrategy;

import client.Exception.NotEnoughApException;
import client.Exception.TwoActionInOneTurnByAHeroException;
import client.Strategy.PartOfStrategy.PartOfStrategy;
import client.model.*;

import java.util.ArrayList;

public class FirstNotLinearAttackStrategy extends PartOfStrategy {
    private int heroId;

    public FirstNotLinearAttackStrategy(int hero) {
        this.heroId = hero;
    }

    @Override
    public void actionTurn(World world) throws NotEnoughApException {
        Hero hero = world.getHero(heroId);
        super.actionTurn(world);
        Ability attackAbility = hero.getOffensiveAbilities()[0];
        Cell targetCell1 = getCellWithMostOppHeroes(world, hero.getCurrentCell(), attackAbility.getName(), true);
        if (targetCell1 != null) {
            try {
                castAbility(world, hero, targetCell1, attackAbility.getName());
            } catch (TwoActionInOneTurnByAHeroException e) {
                e.printStackTrace();
            }
        }
    }
}
