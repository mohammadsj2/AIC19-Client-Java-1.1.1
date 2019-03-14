package client.Strategy.PartOfStrategy;

import client.Exception.NotEnoughApException;
import client.model.*;

import java.util.ArrayList;

public abstract class PartOfStrategy {
    public static final int INFINIT_AP = 1000000000;
    protected int maxAp = INFINIT_AP;
    protected int remainAp = INFINIT_AP;

    protected PartOfStrategy(int maxAp) {
        this.maxAp = maxAp;
        remainAp = maxAp;
    }

    public void decreaseAp(int x) throws NotEnoughApException {
        if (x > remainAp) {
            throw new NotEnoughApException();
        }
        remainAp -= x;
    }

    public void actionTurn(World world) throws NotEnoughApException {

    }

    public void moveTurn(World world) throws NotEnoughApException {

    }

    public void preProcess(World world) {

    }

    public void setMaxAp(int maxAp) {
        this.maxAp = maxAp;
    }

    public int getMaxAp() {
        return maxAp;
    }

    public void setRemainAp(int remainAp) {
        this.remainAp = remainAp;
    }

    public int getRemainAp() {
        return remainAp;
    }

    protected ArrayList<Cell> getARangeOfCellsThatIsNotWall(World world, Cell cell, int range) {
        ArrayList<Cell> tmp = getARangeOfCells(world, cell, range);
        ArrayList<Cell> answer = new ArrayList<>();
        for (Cell cell1 : tmp) {
            if (!cell1.isWall()) {
                answer.add(cell1);
            }
        }
        return answer;
    }

    protected ArrayList<Cell> getARangeOfCells(World world, Cell cell, int range) {
        ArrayList<Cell> answer = new ArrayList<>();
        Cell[][] cells = world.getMap().getCells();
        for (int r = Math.max(0, cell.getRow() - range - 2); r < Math.min(cells.length, cell.getRow() + range + 2); r++) {
            for (int c = Math.max(0, cell.getColumn() - range - 2); c < Math.min(cells.length, cell.getColumn() + range + 2); c++) {
                Cell secondCell = world.getMap().getCell(r, c);
                if (world.manhattanDistance(cell, secondCell) <= range) {
                    answer.add(secondCell);
                }
            }
        }
        return answer;
    }

    protected Cell getNextCellByDirection(World world, Cell cell, Direction direction) {
        int r = cell.getRow();
        int c = cell.getColumn();
        if (direction.equals(Direction.LEFT)) {
            c--;
        } else if (direction.equals(Direction.RIGHT)) {
            c++;
        } else if (direction.equals(Direction.UP)) {
            r--;
        } else {
            r++;
        }
        return world.getMap().getCell(r, c);
    }

    // TODO check other files for Boolean Linear

    protected Cell getCellWithMostJoneKamShode(World world, Cell currentCell,
                                               AbilityName abilityName, int[] healths, boolean Linear) {
        Cell bestCell = currentCell;
        int best = 0;

        int range = world.getAbilityConstants(abilityName).getRange(),
                areaOfEffect = world.getAbilityConstants(abilityName).getAreaOfEffect();
        for (Cell targetCell : getARangeOfCells(world, currentCell, range)) {
            if (!world.isInVision(targetCell, currentCell) && Linear)
                continue;
            int ans;
            ans = getJoneKamOfOppHeroesInRange(world, targetCell, areaOfEffect, abilityName, healths, null);
            if (ans > best) {
                best = ans;
                bestCell = targetCell;
            }
        }
        if (best == 0) {
            return null;
        }
        return bestCell;
    }


    protected Cell getCellWithMostKills(World world, Cell currentCell,
                                        AbilityName abilityName, int[] healths, boolean Linear) {
        Cell bestCell = currentCell;
        Pair<Integer, Integer> best = new Pair<>(0, 0);

        int range = world.getAbilityConstants(abilityName).getRange(),
                areaOfEffect = world.getAbilityConstants(abilityName).getAreaOfEffect();
        for (Cell targetCell : getARangeOfCells(world, currentCell, range)) {
            if (!world.isInVision(targetCell, currentCell) && Linear)
                continue;
            Pair<Integer, Integer> thisOne;
            thisOne = getKillsOfOppHeroesInRange(world, targetCell, areaOfEffect, abilityName, healths, null);
            if (thisOne.getFirst() > best.getFirst() ||
                    (thisOne.getFirst().equals(best.getFirst()) && thisOne.getSecond() > best.getSecond())) {
                best = thisOne;
                bestCell = targetCell;
            }
        }
        if (best.getSecond() == 0) {
            return null;
        }
        return bestCell;
    }


    protected Cell getCellWithMostOppHeroes(World world, Cell currentCell, AbilityName abilityName,
                                            boolean Linear) {
        Cell bestCell = currentCell;
        int best = 0;

        int range = world.getAbilityConstants(abilityName).getRange();
        for (Cell cell : getARangeOfCells(world, currentCell, range)) {
            if (!world.isInVision(cell, currentCell) && Linear)
                continue;
            int areaOfEffect = world.getAbilityConstants(abilityName).getAreaOfEffect();
            int ans = getNumberOfOppHeroesInRange(world, cell, areaOfEffect);
            if (ans > best) {
                bestCell = cell;
                best = ans;
            }
        }
        if (best == 0) {
            return null;
        }
        return bestCell;
    }

    public Pair<Integer, Integer> getKillsOfOppHeroesInRange(World world, Cell cellBomb, int range, AbilityName abilityName
            , int[] healths, ArrayList<Integer> ids) {
        int kills = 0,
                hpCost = 0;

        Hero[] oppHeros = world.getOppHeroes();
        for (int i = 0; i < 4; i++) {
            if (oppHeros[i].getCurrentCell() == null) // Dide nashe opp hero
                continue;
            if (world.manhattanDistance(cellBomb, oppHeros[i].getCurrentCell()) <= range) {
                if (healths == null) {
                    if (oppHeros[i].getCurrentHP() <= world.getAbilityConstants(abilityName).getPower() &&
                            oppHeros[i].getCurrentHP() > 0)
                        kills++;
                    hpCost += Math.min(oppHeros[i].getCurrentHP(), world.getAbilityConstants(abilityName).getPower());
                } else {
                    if (healths[oppHeros[i].getId()] <= world.getAbilityConstants(abilityName).getPower() &&
                            healths[oppHeros[i].getId()] > 0)
                        kills++;
                    hpCost += Math.min(healths[oppHeros[i].getId()], world.getAbilityConstants(abilityName).getPower());
                }

                if (ids != null) ids.add(oppHeros[i].getId());

            }
        }
        Pair<Integer, Integer> ans = new Pair<>(kills, hpCost);
        return ans;
    } // aval mibine koja bezane bishtarin koshte ro darim badesh bar hasbe bishtarin jone kam shode sort mikone


    public int getJoneKamOfOppHeroesInRange(World world, Cell cellBomb, int range, AbilityName abilityName
            , int[] healths, ArrayList<Integer> ids) {
        int ans = 0;
        Hero[] oppHeros = world.getOppHeroes();
        for (int i = 0; i < 4; i++) {
            if (oppHeros[i].getCurrentCell() == null) // Dide nashe opp hero
                continue;
            if (world.manhattanDistance(cellBomb, oppHeros[i].getCurrentCell()) <= range) {
                if (healths == null)
                    ans += Math.min(oppHeros[i].getCurrentHP(), world.getAbilityConstants(abilityName).getPower());
                else ans += Math.min(healths[oppHeros[i].getId()], world.getAbilityConstants(abilityName).getPower());

                if (ids != null) ids.add(oppHeros[i].getId());

            }
        }

        return ans;
    }

    private int getNumberOfOppHeroesInRange(World world, Cell cell, int range) {
        int ans = 0;
        for (Cell cell2 : getARangeOfCellsThatIsNotWall(world, cell, range)) {
            if (world.getOppHero(cell2) != null) {
                ans++;
            }
        }
        return ans;
    }

    protected Cell getCellWithMostOwnHeroesForNotLinearAbilities(World world, Cell currentCell, AbilityName abilityName) {
        Cell bestCell = currentCell;
        int best = 0;

        for (Cell cell : getARangeOfCellsThatIsNotWall(world, currentCell,
                world.getAbilityConstants(abilityName).getRange())) {
            int ans = getNumberOfOwnHeroesInRange(world, cell, world.getAbilityConstants(abilityName).getAreaOfEffect());
            if (ans > best) {
                best = ans;
                bestCell = cell;
            }
        }
        if (best == 0) {
            return null;
        }
        return bestCell;
    }

    private int getNumberOfOwnHeroesInRange(World world, Cell cell, int range) {
        int ans = 0;
        for (Cell cell2 : getARangeOfCellsThatIsNotWall(world, cell, range)) {
            if (world.getMyHero(cell2) != null) {
                ans++;
            }
        }
        return ans;
    }

    protected void heal(World world, Hero hero, Cell targetCell) throws NotEnoughApException {
        castAbility(world, hero, targetCell, AbilityName.HEALER_HEAL);
    }

    protected void move(World world, Hero hero, Direction direction) throws NotEnoughApException {
        decreaseAp(hero.getMoveAPCost());
        world.moveHero(hero, direction);
    }

    protected void dodge(World world, Hero hero, Cell targetCell) throws NotEnoughApException {
        Ability dodgeAbility = hero.getDodgeAbilities()[0];
        castAbility(world, hero, targetCell, dodgeAbility.getName());
    }

    protected void dodge(World world, Hero hero, Cell targetCell, Boolean decreaseMoney) throws NotEnoughApException {
        Ability dodgeAbility = hero.getDodgeAbilities()[0];
        if (decreaseMoney.equals(true)) {
            dodge(world, hero, targetCell);
        } else {
            castAbility(world, hero, targetCell, dodgeAbility.getName());

        }
    }

    protected void bombAttack(World world, Hero hero, Cell targetCell) throws NotEnoughApException {
        castAbility(world, hero, targetCell, AbilityName.BLASTER_BOMB);
    }

    protected void guard(World world, Hero hero, Cell targetCell) throws NotEnoughApException {
        castAbility(world, hero, targetCell, AbilityName.GUARDIAN_FORTIFY);
    }

    protected void castAbility(World world, Hero hero, Cell targetCell, AbilityName blasterBomb) throws NotEnoughApException {
        decreaseAp(hero.getAbility(blasterBomb).getAPCost());
        world.castAbility(hero.getId(), blasterBomb, targetCell);
    }

}
