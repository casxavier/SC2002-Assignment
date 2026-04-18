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
        int raw = actor.getAttack();
        int dealt = target.takeDamage(raw);
        int reduced = raw - dealt;
        int defense = target.getEffectiveDefense();
        if (reduced != defense) {
            return ActionResult.ok(String.format(
                    "%s attacks %s for %d damage (reduced by status effects, %s HP: %d).",
                    actor.getName(), target.getName(), dealt, target.getName(), target.getHp()));
        }
        return ActionResult.ok(String.format(
                "%s attacks %s for %d damage (%d - %d def, %s HP: %d).",
                actor.getName(), target.getName(), dealt, raw, defense, target.getName(), target.getHp()));
    }
}
