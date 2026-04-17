package action;

import combatant.Combatant;
import combatant.Player;
import status.Stun;


public final class ShieldBashSkill implements SpecialSkill {

    public static final ShieldBashSkill INSTANCE = new ShieldBashSkill();

    private ShieldBashSkill() {}

    @Override
    public boolean canUse(Player player) {
        return player.canUseSpecialSkill();
    }

    @Override
    public boolean requiresTarget() {
        return true;
    }

    @Override
    public ActionResult execute(Player player, BattleContext ctx, Combatant singleTarget) {
        if (!canUse(player)) {
            return ActionResult.fail(player.getName() + " cannot use Shield Bash right now.");
        }
        if (singleTarget == null || !singleTarget.isAlive()) {
            return ActionResult.fail("Invalid target for Shield Bash.");
        }
        int dealt = singleTarget.takeDamage(player.getAttack());
        singleTarget.addStatusEffect(new Stun());
        player.consumeSpecialSkillUse();
        return ActionResult.ok(String.format(
                "%s used Shield Bash on %s for %d damage (%d - %d defense, target stunned).",
                player.getName(), singleTarget.getName(), dealt, player.getAttack(), player.getAttack() - dealt));
    }
}
