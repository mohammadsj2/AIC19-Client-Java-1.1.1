package client.Strategy.PartOfStrategy.BombStrategy;


import client.Exception.NotEnoughApException;
import client.Exception.TwoActionInOneTurnByAHeroException;
import client.Strategy.PartOfStrategy.PartOfStrategy;
import client.model.AbilityName;
import client.model.Cell;
import client.model.Hero;
import client.model.World;

/*
    jayi ro peyda mikone ke be tedad bishtari sadame bezane

 */
public class FirstBombStrategy extends PartOfStrategy {
    int blasterId;

    public FirstBombStrategy(int blaster) {
        this.blasterId = blaster;
    }

    @Override
    public void actionTurn(World world) throws NotEnoughApException {
        Hero blaster = world.getHero(blasterId);
        Cell bestCell = getCellWithMostOppHeroes(world, blaster.getCurrentCell(), AbilityName.BLASTER_BOMB, false);
        if (bestCell != null) {
            try {
                bombAttack(world, blaster, bestCell);
            } catch (TwoActionInOneTurnByAHeroException ignored) {

            }
        }
    }


}
