package status;

import combatant.Combatant;

/**
 * Stun status effect:
 * - Prevents the affected combatant from acting for the current turn and the next turn.
 */
public class Stun implements StatusEffect {
    private int remainingTurns;

    public Stun() {
        // Initialize with 3 because we decrement in onTurnStart BEFORE canAct check
        // Turn 1: 3→2, canAct checks 2>0 → blocked
        // Turn 2: 2→1, canAct checks 1>0 → blocked
        // Turn 3: 1→0, canAct checks 0>0 → free to act
        this.remainingTurns = 3;
    }

    @Override
    public String getName() {
        return "Stun";
    }

    @Override
    public void onTurnStart(Combatant target) {
        // Decrement at turn start so canAct() reflects the correct state
        if (remainingTurns > 0) {
            remainingTurns--;
        }
    }

    @Override
    public boolean canAct() {
        // Can act only if remainingTurns is 0 or less
        return remainingTurns <= 0;
    }

    @Override
    public boolean isExpired() {
        return remainingTurns <= 0;
    }

    public int getRemainingTurns() {
        return remainingTurns;
    }
}