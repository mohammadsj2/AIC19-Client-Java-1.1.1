package client.Strategy.PartOfStrategy.HealStrategy;

import client.Exception.NotEnoughApException;
import client.Strategy.PartOfStrategy.PartOfStrategy;
import client.model.AbilityName;
import client.model.Cell;
import client.model.Hero;
import client.model.World;

public class HealStrategy extends PartOfStrategy {
    protected HealStrategy(int maxAp) {
        super(maxAp);
    }
    void heal(World world, Hero hero, Cell targetCell) throws NotEnoughApException {
        decreaseAp(hero.getAbility(AbilityName.HEALER_HEAL).getAPCost());
        world.castAbility(hero, AbilityName.HEALER_HEAL, targetCell);
    }
}
