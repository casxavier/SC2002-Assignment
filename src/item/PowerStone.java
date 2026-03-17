package item;

import combatant.Player;

public class PowerStone extends Item {

    public PowerStone() {
        super("PowerStone");
    }

    @Override
    public String use(Player target) {
        target.grantPowerStoneCharge();
        return target.getName() + " gained one free special skill use.";
    }
}
