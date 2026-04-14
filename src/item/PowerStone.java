package item;

import combatant.Combatant;
import combatant.Player;
import combatant.Warrior;
import action.BattleContext;
import action.PlayerSpecialSkills;
import action.ActionResult;

public class PowerStone extends Item {

    public PowerStone() {
        super("Power Stone");
    }

    @Override
    public String use(Player user) {
        user.grantPowerStoneCharge();
        return null;
    }

    @Override
    public String useWithTarget(Player user, BattleContext ctx, Combatant target) {
        if (user instanceof Warrior) {
            if (target == null || !target.isAlive()) {
                return "Power Stone requires a living enemy target.";
            }
        }

        ActionResult skillResult = PlayerSpecialSkills.forPlayer(user).execute(user, ctx, target);

        if (!skillResult.isSuccess()) {
            return user.getName() + " used Power Stone. " + skillResult.getMessage();
        }

        return user.getName() + " used Power Stone. " + skillResult.getMessage();
    }

    @Override
    public boolean requiresTarget() {
        return true;
    }
}
