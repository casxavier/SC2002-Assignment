package combatant;

import item.Item;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public abstract class Player extends Combatant {
    protected int maxHp;
    protected int smokeTurns = 0;
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

    public void heal(int amount) {
        if (amount <= 0) {
            return;
        }
        hp = Math.min(hp + amount, maxHp);
    }

    @Override
    public int takeDamage(int damage) {
        if (isSmokeActive()) {
            damage = 0;
        }

        return super.takeDamage(damage);
    }

    @Override
    public void onTurnStart() {
        if (smokeTurns > 0) {
            smokeTurns--;
        }
        super.onTurnStart();
        // Spec: special cooldown decreases only if a turn by the combatant took place.
        if (canAct() && specialSkillCooldown > 0) {
            specialSkillCooldown--;
        }
    }

    // Special Skills

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

    // Items Inventory

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

    // Smoke Bomb

    public void applySmokeBomb() {
        smokeTurns = 2;
    }

    public boolean isSmokeActive() {
        return smokeTurns > 0;
    }

    // Power Stone

    public void grantPowerStoneCharge() {
        hasPowerStoneCharge = true;
    }

    public boolean hasPowerStoneCharge() {
        return hasPowerStoneCharge;
    }
}
