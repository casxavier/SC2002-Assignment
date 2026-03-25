package action;

import combatant.Combatant;

/**
 * Minimal action abstraction.
 *
 * Integration point with status effects:
 * - Before performing any action, call {@link #canExecute()}.
 * - If false, caller can treat it as a skipped turn (e.g., due to Stun).
 */
public abstract class Action {
    protected final Combatant actor;
    protected final Combatant target;

    public Action(Combatant actor, Combatant target) {
        this.actor = actor;
        this.target = target;
    }

    /**
     * Checks if the actor is alive and allowed to act this turn.
     * This is where status effects like Stun are respected.
     */
    public boolean canExecute() {
        return actor != null && actor.isAlive() && actor.canAct();
    }

    /**
     * Human-readable reason for blocked execution.
     */
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
}
