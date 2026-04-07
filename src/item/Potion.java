package item;

import combatant.Player;

public class Potion extends Item {

    public Potion() {
        super("Potion");
    }

    @Override
    public String use(Player user) {
        int before = user.getHp();
        user.heal(100);
        int after = user.getHp();

        return user.getName() + " healed from " + before + " to " + after;
    }
}
