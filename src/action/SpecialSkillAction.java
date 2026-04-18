package action;

import combatant.Combatant;
import combatant.Player;

public class SpecialSkillAction extends Action {

    private final Combatant target;

    
    public SpecialSkillAction(Player actor, Combatant target) {
        super(actor);
        this.target = target;
    }

    @Override
    public boolean canExecute() {
        if (!(actor instanceof Player)) {
            return false;
        }
        Player player = (Player) actor;
        if (!super.canExecute() || !player.getSpecialSkill().canUse(player)) {
            return false;
        }
        SpecialSkill skill = player.getSpecialSkill();
        if (skill.requiresTarget()) {
            return target != null && target.isAlive();
        }
        return true;
    }

    @Override
    public String blockedReason() {
        if (!(actor instanceof Player)) {
            return "Only players have special skills.";
        }
        Player player = (Player) actor;
        String base = super.blockedReason();
        if (!base.isEmpty()) {
            return base;
        }
        if (!player.getSpecialSkill().canUse(player)) {
            return player.getName() + "'s special skill is on cooldown.";
        }
        SpecialSkill skill = player.getSpecialSkill();
        if (skill.requiresTarget() && (target == null || !target.isAlive())) {
            return "This skill requires a living enemy.";
        }
        return "";
    }

    @Override
    public ActionResult execute(BattleContext ctx) {
        Player player = (Player) actor;
        SpecialSkill skill = player.getSpecialSkill();
        return skill.execute(player, ctx, target);
    }
}