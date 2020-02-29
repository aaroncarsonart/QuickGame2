package com.aaroncarsonart.quickgame2.menu;

import com.aaroncarsonart.quickgame2.Game;
import com.aaroncarsonart.quickgame2.PlayerAction;
import com.aaroncarsonart.quickgame2.hero.Hero;
import com.aaroncarsonart.quickgame2.hero.Stat;

import java.util.Arrays;
import java.util.List;

public class LevelUpMenu extends Menu {

    private Game game;
    private int statPoints;
    private int strBonus;
    private int agiBonus;
    private int staBonus;
    private int intBonus;
    private int wisBonus;
    private int chaBonus;

    Callback submitStatIncreases = () -> {
        if (statPoints == 0) {
            Hero hero = game.getHero();

            hero.setStrength(hero.getStrength() + strBonus);
            hero.setAgility(hero.getAgility() + agiBonus);
            hero.setStamina(hero.getStamina() + staBonus);
            hero.setIntelligence(hero.getIntelligence() + intBonus);
            hero.setWisdom(hero.getWisdom() + wisBonus);
            hero.setCharisma(hero.getCharisma() + chaBonus);

            game.getMenuCancelCallback().execute();
        }
    };


    public LevelUpMenu(LevelUpMenuView menuView, MenuLayout layout, Game game, int statPoints){
        super(menuView, layout, () -> {});
        this.game = game;
        this.statPoints = statPoints;
        setupMenuItems();
    }


    private void setupMenuItems() {
        List<Stat> stats = Arrays.asList(
                Stat.STRENGTH,
                Stat.AGILITY,
                Stat.STAMINA,
                Stat.INTELLIGENCE,
                Stat.WISDOM,
                Stat.CHARISMA);

        Hero hero = game.getHero();

//        for (Stat stat : stats) {
//            Callback increaseStatCallback = () -> {
//                int currentValue = stat.getAccessor(hero).getAsInt();
//                int newValue = currentValue + 1;
//                stat.getSetter(hero).accept(newValue);
//                game.getMenuCancelCallback().execute();
//            };
//
//            MenuItem menuItem = new MenuItem(stat.name(), increaseStatCallback);
//            this.addMenuItem(menuItem);
//        }

        for (Stat stat : stats) {
            MenuItem menuItem = new MenuItem(stat.name(), () -> {});
            this.addMenuItem(menuItem);
        }
    }

    @Override
    public void respondToPlayerInputs(PlayerAction playerAction) {
        Callback decreaseStat = () -> {
            MenuItem item = menuItems.get(index);
            Stat stat = Stat.valueOf(item.getLabel());
            decreaseStat(stat);
        };
        Callback increaseStat = () -> {
            MenuItem item = menuItems.get(index);
            Stat stat = Stat.valueOf(item.getLabel());
            increaseStat(stat);
        };

        switch (playerAction) {
            case UP:
                previous();
                break;
            case DOWN:
                next();
                break;
            case LEFT:
            case CANCEL:
                decreaseStat.execute();
                break;
            case RIGHT:
                increaseStat.execute();
                break;
            case OK:
                if (statPoints == 0) {
                    submitStatIncreases.execute();
                } else {
                    increaseStat.execute();
                }
                break;
            default:
        }
    }


    private void increaseStat(Stat stat) {
        if (statPoints == 0) {
            return;
        }
        switch (stat) {
            case STRENGTH:
                strBonus++;
                break;
            case AGILITY:
                agiBonus++;
                break;
            case STAMINA:
                staBonus++;
                break;
            case INTELLIGENCE:
                intBonus++;
                break;
            case WISDOM:
                wisBonus++;
                break;
            case CHARISMA:
                chaBonus++;
                break;
        }
        statPoints--;
    }

    private void decreaseStat(Stat stat) {
        switch (stat) {
            case STRENGTH:
                if (strBonus > 0) {
                    strBonus--;
                    statPoints++;
                }
                break;
            case AGILITY:
                if (agiBonus > 0) {
                    agiBonus--;
                    statPoints++;
                }
                break;
            case STAMINA:
                if (staBonus > 0) {
                    staBonus--;
                    statPoints++;
                }
                break;
            case INTELLIGENCE:
                if (intBonus > 0) {
                    intBonus--;
                    statPoints++;
                }
                break;
            case WISDOM:
                if (wisBonus > 0) {
                    wisBonus--;
                    statPoints++;
                }
                break;
            case CHARISMA:
                if (chaBonus > 0) {
                    chaBonus--;
                    statPoints++;
                }
                break;
        }
    }

    public int getStatBonus(Stat stat) {
        switch (stat) {
            case STRENGTH: return strBonus;
            case AGILITY: return agiBonus;
            case STAMINA: return staBonus;
            case INTELLIGENCE: return intBonus;
            case WISDOM: return wisBonus;
            case CHARISMA: return chaBonus;
            default: return 0;
        }
    }

    public int getStatPoints() {
        return statPoints;
    }

}
