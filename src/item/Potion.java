package item;

import combatant.Player;

public class Potion extends Item {

    public Potion() {
        super("Potion");
    }

    @Override
    public String use(Player target) {
        int before = target.getHp();
        target.heal(100);
        int after = target.getHp();

        return target.getName() + " healed from " + before + " to " + after;
    }
}
