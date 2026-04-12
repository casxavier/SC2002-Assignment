package item;

import combatant.Player;
import combatant.Combatant;
import action.BattleContext;

public abstract class Item {
    protected String name;

    public Item(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract String use(Player user);

    public String useWithTarget(Player user, BattleContext ctx, Combatant target) {
        return "This item does not support targeting.";
    }

    public boolean requiresTarget() {
        return false;
    }
}
