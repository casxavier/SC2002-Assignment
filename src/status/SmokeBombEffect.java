package status;

import combatant.Combatant;


public class SmokeBombEffect implements StatusEffect {
    private int remainingTurns;

    public SmokeBombEffect() {
        
        this.remainingTurns = 2;
    }

    @Override
    public String getName() {
        return "Smoke Bomb";
    }

    @Override
    public void onTurnStart(Combatant target) {
        if (remainingTurns > 0) {
            remainingTurns--;
        }
    }

    @Override
    public boolean isExpired() {
        return remainingTurns <= 0;
    }

    public int getRemainingTurns() {
        return Math.max(0, remainingTurns);
    }
}
