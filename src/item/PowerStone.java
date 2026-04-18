package item;

import combatant.Combatant;
import combatant.Player;
import action.BattleContext;
import action.SpecialSkill;
import action.ActionResult;

public class PowerStone extends Item {

    public PowerStone() {
        super("Power Stone");
    }

    @Override
    public String use(Player user, BattleContext ctx) {
        user.grantPowerStoneCharge();
        SpecialSkill skill = user.getSpecialSkill();
        ActionResult skillResult = skill.execute(user, ctx, null);

        if (!skillResult.isSuccess()) {
            return user.getName() + " used Power Stone. " + skillResult.getMessage();
        }

        return user.getName() + " used Power Stone. " + skillResult.getMessage();
    }

    @Override
    public String useWithTarget(Player user, BattleContext ctx, Combatant target) {
        user.grantPowerStoneCharge();
        SpecialSkill skill = user.getSpecialSkill();
        ActionResult skillResult = skill.execute(user, ctx, target);

        if (!skillResult.isSuccess()) {
            return user.getName() + " used Power Stone. " + skillResult.getMessage();
        }

        return user.getName() + " used Power Stone. " + skillResult.getMessage();
    }

    @Override
    public boolean requiresTarget() {
        return true;
    }

    @Override
    public boolean requiresTarget(Player user) {
        SpecialSkill skill = user.getSpecialSkill();
        return skill.requiresTarget();
    }

    @Override
    public Item duplicate() {
        return new PowerStone();
    }
}