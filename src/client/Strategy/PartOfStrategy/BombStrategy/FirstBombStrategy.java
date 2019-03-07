package client.Strategy.PartOfStrategy.BombStrategy;


import client.Exception.NotEnoughApException;
import client.model.AbilityName;
import client.model.Cell;
import client.model.Hero;
import client.model.World;

/*
    jayi ro peyda mikone ke be tedad bishtari sadame bezane

 */
public class FirstBombStrategy extends BombStrategy{
    Hero blaster;
    FirstBombStrategy(int maxAp, Hero blaster) {
        super(maxAp);
        this.blaster=blaster;
    }

    @Override
    public void run(World world) throws NotEnoughApException {
        super.run(world);
        Cell bestCell = getBestCellForNotLinearStrategies(world, blaster.getCurrentCell() , AbilityName.BLASTER_BOMB);
        if (bestCell != null) {
            bombAttack(world,blaster,bestCell);
        }
    }
}
