package combatant;

import action.SpecialSkill;
import item.Item;
import status.StatusEffect;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public abstract class Player extends Combatant {
    protected int maxHp;

    protected List<Item> inventory = new ArrayList<>();
    private int specialSkillCooldown;
    private final int defaultSpecialSkillCooldown = 3;
    private boolean hasPowerStoneCharge;

    public Player(String name, int hp, int attack, int defense, int speed) {
        super(name, hp, attack, defense, speed);
        this.maxHp = hp;
        this.specialSkillCooldown = 0;
        this.hasPowerStoneCharge = false;
    }

    public int getMaxHp(){
        return maxHp;
    }

    public void heal(int amount) {
        if (amount <= 0) {
            return;
        }
        hp = Math.min(hp + amount, maxHp);
    }

    public int takeDamage(int damage) {
        return super.takeDamage(damage);
    }

    @Override
    public void onTurnStart() {
        super.onTurnStart();

        // Don't tick cooldown if stunned or otherwise blocked
        if (canAct() && specialSkillCooldown > 0) {
            specialSkillCooldown--;
        }
    }

     // Special Skill

    public boolean canUseSpecialSkill() {
        return specialSkillCooldown == 0 || hasPowerStoneCharge;
    }

    public void consumeSpecialSkillUse() {
        if (hasPowerStoneCharge) {
            hasPowerStoneCharge = false;
        } else {
            specialSkillCooldown = defaultSpecialSkillCooldown;
        }
    }

    public int getSpecialSkillCooldown() {
        return specialSkillCooldown;
    }

    public abstract SpecialSkill getSpecialSkill();

    // Inventory

    public List<Item> getInventory() {
        return Collections.unmodifiableList(inventory);
    }

    public Item getItem(int index) {
        if (index < 0 || index >= inventory.size()) {
            System.out.println("ERROR: Invalid item index");
            return null;
        }
        return inventory.get(index);
    }

    public void addItem(Item item) {
        inventory.add(item);
    }

    public void removeItem(Item item) {
        inventory.remove(item);
    }

    // Power Stone Charge

    public void grantPowerStoneCharge() {
        hasPowerStoneCharge = true;
    }
}
