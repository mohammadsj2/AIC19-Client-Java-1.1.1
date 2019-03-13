package client.Strategy.PartOfStrategy.BombStrategy;

import client.Exception.NotEnoughApException;
import client.Strategy.PartOfStrategy.PartOfStrategy;
import client.model.AbilityName;
import client.model.Cell;
import client.model.Hero;
import client.model.World;

import java.util.ArrayList;

public class SecondBombStrategy extends PartOfStrategy {
    int[] healths;
    int blasterId;

    public int[] getHealths() {
        return healths;
    }

    public SecondBombStrategy(int maxAp, int blaster, int[] healths) {
        super(maxAp);
        this.blasterId = blaster;
        this.healths = healths;
    }

    @Override
    public void actionTurn(World world) throws NotEnoughApException {
        Hero blaster = world.getHero(blasterId);
        Cell bestCell = getCellWithMostJoneKamShodeForNotLinearAbilities(
                world, blaster.getCurrentCell(), AbilityName.BLASTER_BOMB, healths);
        if (bestCell != null) {
            try {
                bombAttack(world, blaster, bestCell);
                ArrayList<Integer> ids = new ArrayList<>();
                getJoneKamOfOppHeroesInRange(world, bestCell, blaster.getAbility(AbilityName.BLASTER_BOMB).getRange()
                ,AbilityName.BLASTER_BOMB,healths, ids);
                for (Integer id : ids)
                    healths[id] = Math.max(0, healths[id] - blaster.getAbility(AbilityName.BLASTER_BOMB).getPower());
            } catch(Exception ignored) {
            }
        }
    }
}
