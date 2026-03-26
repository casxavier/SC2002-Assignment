package status;

import combatant.Combatant;







public class ArcaneBlastEffect implements StatusEffect {
    private static final int BONUS_PER_KILL = 10;
    private int totalBonus;

    public ArcaneBlastEffect() {
        this.totalBonus = 0;
    }

    




    public void addKills(int kills) {
        if (kills <= 0) {
            return;
        }
        totalBonus += kills * BONUS_PER_KILL;
    }

    


    public int getTotalBonus() {
        return totalBonus;
    }

    


    public void clear() {
        totalBonus = 0;
    }

    @Override
    public String getName() {
        return "Arcane Blast";
    }

    @Override
    public int getAttackModifier() {
        return totalBonus;
    }

    @Override
    public boolean isExpired() {
        return false;
    }

    






    public static ArcaneBlastEffect getOn(Combatant target) {
        for (StatusEffect effect : target.getStatusEffects()) {
            if (effect instanceof ArcaneBlastEffect) {
                return (ArcaneBlastEffect) effect;
            }
        }
        return null;
    }
}
