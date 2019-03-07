package client.Strategy.PartOfStrategy.BombStrategy;

import client.Exception.NotEnoughApException;
import client.Strategy.PartOfStrategy.PartOfStrategy;
import client.model.AbilityName;
import client.model.Cell;
import client.model.Hero;
import client.model.World;

public abstract class BombStrategy extends PartOfStrategy {

    BombStrategy(int maxAp) {
        super(maxAp);
    }
    void bombAttack(World world,Hero hero,Cell cell) throws NotEnoughApException {
        decreaseAp(hero.getAbility(AbilityName.BLASTER_BOMB).getAPCost());
        world.castAbility(hero, AbilityName.BLASTER_BOMB, cell);
    }
}
