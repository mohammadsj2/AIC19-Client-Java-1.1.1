package client.Strategy;

import client.model.World;

public class BBBBBetterStrategy extends BBBBStrategy{
    @Override
    public void actionTurn(World world) {
        dodge(world);
        blastersBombAttacks(world);
        blastersAttacks(world);
    }
}
