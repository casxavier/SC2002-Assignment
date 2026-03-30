package status;

import combatant.Combatant;





public class Stun implements StatusEffect {
    private int remainingTurns;

    public Stun() {
        
        
        
        this.remainingTurns = 3;
    }

    @Override
    public String getName() {
        return "Stun";
    }

    @Override
    public void onTurnStart(Combatant target) {
        
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