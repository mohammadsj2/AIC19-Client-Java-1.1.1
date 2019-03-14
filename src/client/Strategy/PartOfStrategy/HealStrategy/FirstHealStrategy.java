package client.Strategy.PartOfStrategy.HealStrategy;


import client.Exception.NotEnoughApException;
import client.Exception.TwoActionInOneTurnByAHeroException;
import client.Strategy.PartOfStrategy.PartOfStrategy;
import client.model.AbilityConstants;
import client.model.AbilityName;
import client.model.Hero;
import client.model.World;

import java.util.ArrayList;
import java.util.Comparator;

/*
    ooni ke kamtarin joono dare va too range hast!
 */
public class FirstHealStrategy extends PartOfStrategy {
    private int healerId;

    public FirstHealStrategy(int healer) {
        this.healerId = healer;
    }

    @Override
    public void actionTurn(World world) throws NotEnoughApException {
        Hero healer = world.getHero(healerId);
        AbilityConstants abilityHealer = world.getAbilityConstants(AbilityName.HEALER_HEAL);
        int range = abilityHealer.getRange(),
                power = abilityHealer.getPower();
        ArrayList<Hero> inArea = new ArrayList<>();
        for (Hero hero : world.getMyHeroes()) {
            int distance = world.manhattanDistance(hero.getCurrentCell(), healer.getCurrentCell());
            if (distance <= range)
                inArea.add(hero);
        }
        inArea.sort(Comparator.comparingInt(Hero::getCurrentHP));
        for (Hero hero : inArea) {
            if (hero.getCurrentHP() + power <= hero.getMaxHP()) {
                try {
                    heal(world, healer, hero.getCurrentCell());
                } catch (TwoActionInOneTurnByAHeroException ignored) {

                }
                return;
            }
        }
        if (inArea.size() != 0) {
            try {
                heal(world, healer, inArea.get(0).getCurrentCell());
            } catch (TwoActionInOneTurnByAHeroException ignored) {

            }
        }
    }
}
