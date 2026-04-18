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

    public abstract String use(Player user, BattleContext ctx);

    public String useWithTarget(Player user, BattleContext ctx, Combatant target) {
        return "This item does not support targeting.";
    }

    public boolean requiresTarget() {
        return false;
    }

    // For checking if the item requires a target based on the user's class
    public boolean requiresTarget(Player user) {
        return requiresTarget();
    }

    // For duplicating items
    public abstract Item duplicate();
}
