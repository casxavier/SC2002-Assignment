package status;

import combatant.Combatant;

/**
 * Stun status effect:
 * - Prevents the affected combatant from acting for the current turn and the next turn.
 */
public class Stun implements StatusEffect {
    private int remainingTurns;

    public Stun() {
        // Two skipped turns (current + next): internal counter starts at 3 so that
        // after each victim onTurnStart() decrement, canAct is false while
        // remainingTurns is 1 or 2, and true once it reaches 0 (see spec Appendix A).
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