package client.Strategy.PartOfStrategy.AttackStrategy;

import client.Exception.NotEnoughApException;
import client.Strategy.PartOfStrategy.PartOfStrategy;
import client.model.*;

import java.util.ArrayList;

public class FirstAttackStrategy extends PartOfStrategy {
    Hero hero;

    public FirstAttackStrategy(int maxAp, Hero hero) {
        super(maxAp);
        this.hero = hero;
    }

    @Override
    public void actionTurn(World world) throws NotEnoughApException {
        super.actionTurn(world);
        Ability attackAbility = hero.getOffensiveAbilities()[0];
        ArrayList<Pair<Cell, Integer>> cells = new ArrayList<>();
        ArrayList<Cell> importantCells = new ArrayList<>();
        importantCells.addAll(getARangeOfCellsThatIsNotWall(world, hero.getCurrentCell()
                , world.getAbilityConstants(attackAbility.getName()).getRange()));
        for (Cell cell : importantCells) {
            int score = 0;
            for (Cell cell1 : getARangeOfCellsThatIsNotWall(world, cell, attackAbility.getAreaOfEffect())) {
                if (world.getOppHero(cell1) != null) {
                    score++;
                }
            }
            Pair<Cell, Integer> target = new Pair<>(cell, score);
            cells.add(target);
        }

        cells.sort((t1, t2) -> t2.getSecond() - t1.getSecond());

        int range = world.getAbilityConstants(attackAbility.getName()).getRange();

        for (Pair<Cell, Integer> target : cells) {
            Cell targetCell = target.getFirst();
            if (target.getSecond() > 0 && world.manhattanDistance(hero.getCurrentCell(), targetCell) <= range) {
                if (world.isInVision(hero.getCurrentCell(), targetCell)) {
                    world.castAbility(hero, attackAbility.getName(), targetCell);
                    break;
                }
            }
        }
    }
}
