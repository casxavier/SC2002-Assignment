package action;

import combatant.Combatant;
import combatant.Player;
import combatant.Warrior;

public class SpecialSkillAction extends Action {

    private final Combatant target;

    /**
     * @param target required for Warrior (single target); ignored for Wizard blast.
     */
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
        if (!super.canExecute() || !PlayerSpecialSkills.forPlayer(player).canUse(player)) {
            return false;
        }
        if (player instanceof Warrior) {
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
        if (!PlayerSpecialSkills.forPlayer(player).canUse(player)) {
            return player.getName() + "'s special skill is on cooldown.";
        }
        if (player instanceof Warrior && (target == null || !target.isAlive())) {
            return "Shield Bash requires a living enemy.";
        }
        return "";
    }

    @Override
    public ActionResult execute(BattleContext ctx) {
        Player player = (Player) actor;
        SpecialSkill skill = PlayerSpecialSkills.forPlayer(player);
        return skill.execute(player, ctx, target);
    }
}
