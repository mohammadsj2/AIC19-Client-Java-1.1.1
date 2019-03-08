package client.Strategy.PartOfStrategy.GuardStrategy;

import client.Exception.NotEnoughApException;
import client.Strategy.PartOfStrategy.PartOfStrategy;
import client.model.AbilityName;
import client.model.Cell;
import client.model.Hero;
import client.model.World;

public class FirstGuardStrategy extends PartOfStrategy {
    private Hero[] guardians = new Hero[2];

    public FirstGuardStrategy(int maxAp, Hero firstGuardian, Hero secondGuardian) {
        super(maxAp);
        guardians[0] = firstGuardian;
        guardians[1] = secondGuardian;
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
        Hero guardian = guardians[turn];
        Cell targetCell = getCellWithMostOwnHeroesForNotLinearAbilities(world, guardian.getCurrentCell(), fortify);
        guard(world, guardian, targetCell);
    }
}
