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


    public Turn(int turnNum, Player player, List<Combatant> enemies, List<Combatant> turnOrder) {
        this.turnNum = turnNum;
        this.player = player;
        this.enemies = enemies;
        this.turnOrder = turnOrder;
    }

    public void executeTurn(Scanner sc) {
        player.onTurnStart();
        while (true) {
            int choice = GameUI.promptTurnActionChoice(sc);
            if (choice == -1) {
                continue;
            }

            
            Action action = null;
            Combatant target = null;
            switch (choice) {
                case 1:
                    if (enemies.isEmpty()) {
                        GameUI.printNoEnemiesToAttack();
                        continue;
                    }
                    GameUI.printCombatantTargets("\nTargets:", enemies, true);
                    int targetIndex = GameUI.promptAttackTargetIndex(sc, enemies.size());
                    if (targetIndex < 0 || targetIndex >= enemies.size()) {
                        continue;
                    }

                    target = enemies.get(targetIndex);
                    action = new BasicAttackAction(player, target);
                    break;

                case 2:
                    action = new DefendAction(player);
                    break;

                case 3:
                    List<Item> inventory = player.getInventory();
                    if (inventory.isEmpty()) {
                        GameUI.printNoItemsInInventory();
                        continue;
                    }
                    GameUI.printInventory(inventory);
                    int itemIndex = GameUI.promptInventoryChoice(sc, inventory.size());
                    if (itemIndex < 0 || itemIndex >= inventory.size()) {
                        continue;
                    }
                    if (inventory.get(itemIndex) instanceof PowerStone && player instanceof Warrior) {
                        if (enemies.isEmpty()) {
                            GameUI.printNoValidTargetsForShieldBash();
                            continue;
                        }
                        GameUI.printCombatantTargets("Targets for Shield Bash:", enemies, false);
                        int t = GameUI.promptShieldBashTargetIndex(sc, enemies.size());
                        if (t < 0 || t >= enemies.size()) {
                            continue;
                        }
                        target = enemies.get(t);

                    }

                    action = new ItemAction(player, itemIndex, target);
                    break;

                case 4:
                    if (player instanceof Warrior) {
                        if (enemies.isEmpty()) {
                            GameUI.printNoValidTargetsForShieldBash();
                            continue;
                        }
                        GameUI.printCombatantTargets("Targets for Shield Bash:", enemies, false);
                        int t = GameUI.promptShieldBashTargetIndex(sc, enemies.size());
                        if (t < 0 || t >= enemies.size()) {
                            continue;
                        }
                        target = enemies.get(t);
                    }
                    action = new SpecialSkillAction(player, target);
                    break;

                default:
                    GameUI.printInvalidChoice();
                    continue;
            }

            
            for (int i = 0; i < turnOrder.size(); i++) {
                Combatant c = turnOrder.get(i);
                if (c.equals(player)) {
                    executeAndReport(action);
                } else if (c.isAlive()) {
                    c.onTurnStart();
                    Action enemyAction = new BasicAttackAction(c, player);
                    executeAndReport(enemyAction);
                }
            }
            break;
        }
    }

    private void executeAndReport(Action action) {
        if (!action.canExecute()) {
            GameUI.printMessage(action.blockedReason());
            GameUI.printBlankLine();
            return;
        }

        BattleContext context = new BattleContext(player, enemies);
        boolean playerWasAlive = player.isAlive();
        boolean[] enemiesWereAlive = snapshotEnemyAliveStates();

        GameUI.printMessage(action.execute(context).getMessage());
        printDefeatMessages(playerWasAlive, enemiesWereAlive);

        GameUI.printBlankLine();
        GameUI.waitBetweenTurns();
    }

    private boolean[] snapshotEnemyAliveStates() {
        boolean[] enemiesWereAlive = new boolean[enemies.size()];
        for (int j = 0; j < enemies.size(); j++) {
            enemiesWereAlive[j] = enemies.get(j).isAlive();
        }
        return enemiesWereAlive;
    }

    private void printDefeatMessages(boolean playerWasAlive, boolean[] enemiesWereAlive) {
        if (playerWasAlive && !player.isAlive()) {
            GameUI.printDefeatMessage(player);
        }
        for (int j = 0; j < enemies.size(); j++) {
            Combatant enemy = enemies.get(j);
            if (enemiesWereAlive[j] && !enemy.isAlive()) {
                GameUI.printDefeatMessage(enemy);
            }
        }
    }

}
