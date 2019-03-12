package client.Strategy.PartOfStrategy.GuardStrategy;

import client.Exception.NotEnoughApException;
import client.Strategy.PartOfStrategy.PartOfStrategy;
import client.model.AbilityName;
import client.model.Cell;
import client.model.Hero;
import client.model.World;

public class FirstGuardStrategy extends PartOfStrategy {
    private int[] guardiansId = new int[2];

    public FirstGuardStrategy(int maxAp, int firstGuardian, int secondGuardian) {
        super(maxAp);
        guardiansId[0] = firstGuardian;
        guardiansId[1] = secondGuardian;
    }

    @Override
    public void actionTurn(World world) throws NotEnoughApException {
        AbilityName fortify = AbilityName.GUARDIAN_FORTIFY;
        int cooldownTurns = world.getAbilityConstants(fortify).getCooldown();
        int turn = world.getCurrentTurn();
        turn %= cooldownTurns;
        if (turn > 1) {
            return;
        }
        Hero guardian = world.getHero(guardiansId[turn]);
        Cell targetCell = getCellWithMostOwnHeroesForNotLinearAbilities(world, guardian.getCurrentCell(), fortify);
        guard(world, guardian, targetCell);
    }
}
