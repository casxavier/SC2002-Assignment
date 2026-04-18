package combatant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import status.StatusEffect;

public abstract class Combatant {
    protected String name;
    protected int hp;
    protected int attack;
    protected int defense;
    protected int speed;

    private final List<StatusEffect> statusEffects = new ArrayList<>();

    public Combatant(String name, int hp, int attack, int defense, int speed) {
        this.name = name;
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public int takeDamage(int rawAttack) {
        int mitigation = getEffectiveDefense();
        int actualDamage = Math.max(0, rawAttack - mitigation);
        hp -= actualDamage;
        if (hp < 0) {
            hp = 0;
        }
        return actualDamage;
    }

    public int getEffectiveDefense() {
        return defense + getDefenseModifierFromStatuses();
    }

    public int getAttack() {
        return attack + getAttackModifierFromStatuses();
    }

    public int getBaseAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getSpeed() {
        return speed;
    }

    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }


    // Status effects

    public void addStatusEffect(StatusEffect effect) {
        if (effect == null) {
            return;
        }
        statusEffects.add(effect);
        effect.onApply(this);
    }

    public boolean hasStatusEffect(Class<? extends StatusEffect> type) {
        for (StatusEffect effect : statusEffects) {
            if (type.isInstance(effect)) {
                return true;
            }
        }
        return false;
    }

    public void removeStatusEffect(Class<? extends StatusEffect> type) {
        Iterator<StatusEffect> iterator = statusEffects.iterator();
        while (iterator.hasNext()) {
            StatusEffect effect = iterator.next();
            if (type.isInstance(effect)) {
                iterator.remove();
                effect.onRemove(this);
            }
        }
    }

    public List<StatusEffect> getStatusEffects() {
        return Collections.unmodifiableList(statusEffects);
    }

    // To be called at the start of each turn
    public void onTurnStart() {
        List<StatusEffect> snapshot = new ArrayList<>(statusEffects);

        for (StatusEffect effect : snapshot) {
            effect.onTurnStart(this);
        }

        cleanupExpiredStatusEffects();
    }


    public boolean canAct() {
        for (StatusEffect effect : statusEffects) {
            if (!effect.canAct()) {
                return false;
            }
        }
        return true;
    }

    private int getAttackModifierFromStatuses() {
        int total = 0;
        for (StatusEffect effect : statusEffects) {
            total += effect.getAttackModifier();
        }
        return total;
    }

    private int getDefenseModifierFromStatuses() {
        int total = 0;
        for (StatusEffect effect : statusEffects) {
            total += effect.getDefenseModifier();
        }
        return total;
    }

    private void cleanupExpiredStatusEffects() {
        Iterator<StatusEffect> iterator = statusEffects.iterator();
        while (iterator.hasNext()) {
            StatusEffect effect = iterator.next();
            if (effect.isExpired()) {
                iterator.remove();
                effect.onRemove(this);
            }
        }
    }
}
