package action;

import combatant.Combatant;

/**
 * Command executed during a combatant's turn. UI gathers input; battle code calls {@link #execute}.
 */
public abstract class Action {
    protected final Combatant actor;

    public Action(Combatant actor) {
        this.actor = actor;
    }

    public Combatant getActor() {
        return actor;
    }

    /**
     * Checks if the actor is alive and allowed to act this turn (e.g. not Stunned).
     */
    public boolean canExecute() {
        return actor != null && actor.isAlive() && actor.canAct();
    }

    public String blockedReason() {
        if (actor == null) {
            return "No actor.";
        }
        if (!actor.isAlive()) {
            return actor.getName() + " is defeated and cannot act.";
        }
        if (!actor.canAct()) {
            return actor.getName() + " is unable to act this turn.";
        }
        return "";
    }

    public abstract ActionResult execute(BattleContext ctx);
}
