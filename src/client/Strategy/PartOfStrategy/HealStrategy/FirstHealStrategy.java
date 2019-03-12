package client.Strategy.PartOfStrategy.HealStrategy;


import client.Exception.NotEnoughApException;
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
    private Hero healer;

    public FirstHealStrategy(int maxAp, Hero healer) {
        super(maxAp);
        this.healer = healer;
    }

    @Override
    public void actionTurn(World world) throws NotEnoughApException {
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
                heal(world, healer, hero.getCurrentCell());
                return;
            }
        }
        if (inArea.size() != 0)
            heal(world, healer, inArea.get(0).getCurrentCell());
    }
}
