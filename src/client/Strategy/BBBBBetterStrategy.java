package client.Strategy;

import client.model.Cell;
import client.model.Hero;
import client.model.World;

public class BBBBBetterStrategy extends BBBBStrategy{

    public static final int NUMBER_OF_ACTION_PHASES = 6;

    @Override
    boolean betterToWait(World world, Hero hero, Cell targetCell) {
        return hero.getDodgeAbilities()[0].isReady() &&
                dodgeAHero(world,hero,targetCell,false,true)> NUMBER_OF_ACTION_PHASES;
    }

    @Override
    public void actionTurn(World world) {
        dodge(world);
        blastersBombAttacks(world);
        blasterAttacks(world);
    }
}
