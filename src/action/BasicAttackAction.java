package action;

import combatant.Combatant;

public class BasicAttackAction extends Action {

    private final Combatant target;

    public BasicAttackAction(Combatant actor, Combatant target) {
        super(actor);
        this.target = target;
    }

    @Override
    public boolean canExecute() {
        return super.canExecute() && target != null && target.isAlive();
    }

    @Override
    public String blockedReason() {
        String base = super.blockedReason();
        if (!base.isEmpty()) {
            return base;
        }
        if (target == null || !target.isAlive()) {
            return "No valid target for basic attack.";
        }
        return "";
    }

    @Override
    public ActionResult execute(BattleContext ctx) {
        int dealt = target.takeDamage(actor.getAttack());
        return ActionResult.ok(String.format(
                "%s attacks %s for %d damage (%s HP: %d).",
                actor.getName(), target.getName(), dealt, target.getName(), target.getHp()));
    }
}
