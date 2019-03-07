package client.Strategy.PartOfStrategy.DodgeStrategy;

import client.Exception.NotEnoughApException;
import client.Strategy.PartOfStrategy.PartOfStrategy;
import client.model.*;


public class DodgeStrategy extends PartOfStrategy {
    protected DodgeStrategy(int maxAp) {
        super(maxAp);
    }
    void dodge(World world, Hero hero, Cell targetCell) throws NotEnoughApException {
        Ability dodgeAbility=hero.getDodgeAbilities()[0];
        decreaseAp(dodgeAbility.getAPCost());
        world.castAbility(hero, dodgeAbility, targetCell);
    }
}
