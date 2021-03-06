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

    public SecondBombStrategy(int blaster, int[] healths) {
        this.blasterId = blaster;
        this.healths = healths;
    }

    @Override
    public void actionTurn(World world) throws NotEnoughApException {
        Hero blaster = world.getHero(blasterId);
        Cell bestCell = getCellWithMostJoneKamShode(
                world, blaster.getCurrentCell(), AbilityName.BLASTER_BOMB, healths, false);
        if (bestCell != null) {
            try {
                bombAttack(world, blaster, bestCell);
                ArrayList<Integer> ids = new ArrayList<>();
                getJoneKamOfOppHeroesInRange(world, bestCell, blaster.getAbility(AbilityName.BLASTER_BOMB).getAreaOfEffect()
                ,AbilityName.BLASTER_BOMB,healths, ids);
                System.out.println("Hero : " + blaster.getId());
                for (Integer id : ids) {
                    healths[id] = Math.max(0, healths[id] - blaster.getAbility(AbilityName.BLASTER_BOMB).getPower());
                    System.out.println("id : " + id);
                }
            } catch(Exception ignored) {
            }
        }
    }
}
