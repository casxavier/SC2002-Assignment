package game;

import action.*;
import combatant.*;
import item.Item;
import item.PowerStone;
import java.util.List;
import java.util.Scanner;

public class Turn {
    private final int turnNum;
    private final Player player;
    private final List<Combatant> enemies;
    private final List<Combatant> turnOrder;
    private String targetName = "";
    private boolean isStunned;

    public Turn(int turnNum, Player player, List<Combatant> enemies, List<Combatant> turnOrder) {
        this.turnNum = turnNum;
        this.player = player;
        this.enemies = enemies;
        this.turnOrder = turnOrder;
    }

    public void executeTurn() {
        while (true) {
            Scanner sc = new Scanner(System.in);
            // Print Actions menu
            System.out.println("Choose action:");
            System.out.println("1. Basic Attack");
            System.out.println("2. Defend");
            System.out.println("3. Use Item");
            System.out.println("4. Special Skill");
            System.out.print("Enter choice (1-4): ");
            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input!");
                continue;
            }

            // Determine action
            Action action = null;
            Combatant target = null;
            switch (choice) {
                case 1:
                    if (enemies.isEmpty()) {
                        System.out.println("No enemies to attack.");
                        continue;
                    }
                    System.out.println("Targets:");
                    for (int i = 0; i < enemies.size(); i++) {
                        System.out.printf("  %d. %s (HP %d)\n", i + 1, enemies.get(i).getName(),
                                enemies.get(i).getHp());
                    }
                    System.out.print("Select target (1-" + enemies.size() + "): ");
                    int targetIndex;
                    try {
                        targetIndex = Integer.parseInt(sc.nextLine().trim()) - 1;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input!");
                        continue;
                    }
                    if (targetIndex < 0 || targetIndex >= enemies.size()) {
                        System.out.println("Invalid target!");
                        continue;
                    }

                    target = enemies.get(targetIndex);
                    targetName = target.getName();
                    action = new BasicAttackAction(player, target);
                    break;

                case 2:
                    action = new DefendAction(player);
                    break;

                case 3:
                    List<Item> inventory = player.getInventory();
                    if (inventory.isEmpty()) {
                        System.out.println("No items in inventory.");
                        continue;
                    }
                    System.out.println("Inventory:");
                    for (int i = 0; i < inventory.size(); i++) {
                        System.out.printf("  %d. %s\n", i + 1, inventory.get(i).getName());
                    }
                    System.out.print("Select item (1-" + inventory.size() + "): ");
                    int itemIndex;
                    try {
                        itemIndex = Integer.parseInt(sc.nextLine().trim()) - 1;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input!");
                        continue;
                    }
                    if (itemIndex < 0 || itemIndex >= inventory.size()) {
                        System.out.println("Invalid item!");
                        continue;
                    }
                    if (inventory.get(itemIndex) instanceof PowerStone && player instanceof Warrior) {
                        if (enemies.isEmpty()) {
                            System.out.println("No valid targets for Shield Bash.");
                            continue;
                        }
                        System.out.println("Targets for Shield Bash:");
                        for (int i = 0; i < enemies.size(); i++) {
                            System.out.printf("  %d. %s\n", i + 1, enemies.get(i).getName());
                        }
                        System.out.print("Select target (1-" + enemies.size() + "): ");
                        try {
                            int t = Integer.parseInt(sc.nextLine().trim()) - 1;
                            if (t < 0 || t >= enemies.size()) {
                                System.out.println("Invalid target.");
                                continue;
                            }
                            target = enemies.get(t);
                            targetName = target.getName();
                        } catch (NumberFormatException ex) {
                            System.out.println("Invalid target.");
                            continue;
                        }
                    }
                    if (inventory.get(itemIndex) instanceof PowerStone && player instanceof Wizard) {
                        targetName = "ALL";
                    }
                    action = new ItemAction(player, itemIndex, target);
                    break;

                case 4:
                    if (player instanceof Warrior) {
                        if (enemies.isEmpty()) {
                            System.out.println("No valid targets for Shield Bash.");
                            continue;
                        }
                        System.out.println("Targets for Shield Bash:");
                        for (int i = 0; i < enemies.size(); i++) {
                            System.out.printf("  %d. %s\n", i + 1, enemies.get(i).getName());
                        }
                        System.out.print("Select target (1-" + enemies.size() + "): ");
                        try {
                            int t = Integer.parseInt(sc.nextLine().trim()) - 1;
                            if (t < 0 || t >= enemies.size()) {
                                System.out.println("Invalid target.");
                                continue;
                            }
                            target = enemies.get(t);
                            targetName = target.getName();
                        } catch (NumberFormatException ex) {
                            System.out.println("Invalid target.");
                            continue;
                        }
                    }
                    if (player instanceof Wizard) {
                        targetName = "ALL";
                    }
                    action = new SpecialSkillAction(player, target);
                    break;

                default:
                    System.out.println("Invalid choice!");
                    continue;
            }

            // Execute all character's actions in turn order
            for (int i = 0; i < turnOrder.size(); i++) {
                Combatant c = turnOrder.get(i);
                if (c.equals(player)) {
                    if (!action.canExecute()) {
                        System.out.println(action.blockedReason());
                    } else {
                        System.out.println(action.execute(new BattleContext(player, enemies)).getMessage());
                        if (target != null) {
                            System.out.println(printSummary(player));
                        }
                    }
                } else {
                    if (c.isAlive()) {
                        Action enemyAction = new BasicAttackAction(c, player);
                        if (!enemyAction.canExecute()) {
                            System.out.println(enemyAction.blockedReason());
                        } else {
                            targetName = player.getName();
                            System.out.println(enemyAction.execute(new BattleContext(player, enemies)).getMessage());
                            System.out.println(printSummary(c));
                        }
                    }
                }
                sc.close();
            }

        }
    }

    // print quick summary after each turn ends
    public String printSummary(Combatant actor) {
        if (isStunned) { // character acting has stunned effect
            return String.format("Turn %d: %s was stunned, not able to act.\n", turnNum, actor.getName());
        }
        if (targetName.equals("ALL")) { // arcane blast
            return String.format("Turn %d: %s used arcane blast, all enemies dealt %d damage each.\n", turnNum,
                    actor.getName(), actor.getAttack());
        }
        return String.format("Turn %d: %s dealt %d damage on %s.\n", turnNum, actor.getName(), actor.getAttack(),
                targetName); // basic attack
    }
}
