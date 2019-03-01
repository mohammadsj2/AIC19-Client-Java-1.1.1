package client.Strategy;

import client.model.Cell;
import client.model.Hero;
import client.model.World;

public class BBBBWithoutMoveStrategy extends BBBBStrategy{

    @Override
    boolean betterToWait(World world, Hero hero, Cell targetCell) {
        return hero.getDodgeAbilities()[0].isReady();
    }

    @Override
    public void actionTurn(World world) {
        dodge(world);
        blastersBombAttacks(world);
        blasterAttacks(world);
    }
}
