package combatant;

import status.ArcaneBlastEffect;

public class Wizard extends Player {

    public Wizard(String name) {
        super(name, 200, 50, 10, 20);
    }

    





    public void registerArcaneBlastDefeats(int enemiesDefeated) {
        if (enemiesDefeated <= 0) {
            return;
        }
        ArcaneBlastEffect effect = ArcaneBlastEffect.getOn(this);
        if (effect == null) {
            effect = new ArcaneBlastEffect();
            addStatusEffect(effect);
        }
        effect.addKills(enemiesDefeated);
    }

    


    public int getArcaneBlastBonus() {
        ArcaneBlastEffect effect = ArcaneBlastEffect.getOn(this);
        return effect != null ? effect.getTotalBonus() : 0;
    }

    


    public void resetArcaneBlastBonus() {
        ArcaneBlastEffect effect = ArcaneBlastEffect.getOn(this);
        if (effect != null) {
            effect.clear();
        }
    }
}
