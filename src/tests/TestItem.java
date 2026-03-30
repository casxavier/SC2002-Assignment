import combatant.*;
import item.*;

public class TestItem {

    public static void main(String[] args) {

        Warrior player = new Warrior("Mix");

        
        System.out.println("=== Initial State ===");
        System.out.println(player.getName() + " HP: " + player.getHp());
        System.out.println();

        
        System.out.println("=== Potion Test ===");
        player.takeDamage(200); 
        System.out.println("After damage HP: " + player.getHp());

        Potion potion = new Potion();
        System.out.println(potion.use(player)); 
        System.out.println("After potion HP: " + player.getHp());
        System.out.println();

        
        System.out.println("=== PowerStone Test ===");
        PowerStone ps = new PowerStone();
        System.out.println(ps.use(player));

        System.out.println("Can use special skill? " + player.canUseSpecialSkill());

        
        player.consumeSpecialSkillUse();
        System.out.println("Used special skill (free)");

        System.out.println("Cooldown after free use: " + player.getSpecialSkillCooldown());
        System.out.println("Can still use special skill? " + player.canUseSpecialSkill());
        System.out.println();

        
        player.consumeSpecialSkillUse();
        System.out.println("Used special skill (normal)");

        System.out.println("Cooldown now: " + player.getSpecialSkillCooldown());
        System.out.println();

        
        System.out.println("=== SmokeBomb Test ===");
        SmokeBomb sb = new SmokeBomb();
        System.out.println(sb.use(player));

        int damage = player.takeDamage(50);
        System.out.println("Damage taken with smoke: " + damage);
        System.out.println("HP after attack: " + player.getHp());

        
        player.onTurnStart();
        damage = player.takeDamage(50);
        System.out.println("Damage taken (2nd turn smoke): " + damage);

        
        player.onTurnStart();
        damage = player.takeDamage(50);
        System.out.println("Damage after smoke expired: " + damage);
        System.out.println();

        
        System.out.println("=== Inventory Test ===");
        player.addItem(new Potion());
        player.addItem(new PowerStone());

        System.out.println("Inventory:");
        for (int i = 0; i < player.getInventory().size(); i++) {
            System.out.println(i + ": " + player.getInventory().get(i).getName());
        }

        
        Item item = player.getItem(0);
        System.out.println("Using item: " + item.getName());
        System.out.println(item.use(player));
        player.removeItem(item);

        System.out.println("Inventory after use:");
        for (Item i : player.getInventory()) {
            System.out.println(i.getName());
        }
    }
}
