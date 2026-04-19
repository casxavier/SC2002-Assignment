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

    @Override
    public boolean shouldDisplay() {
        // Show as long as there's an attack bonus accumulated
        return totalBonus > 0;
    }

}
