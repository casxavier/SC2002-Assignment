package game;

import action.*;
import combatant.*;
import item.Item;
import java.util.List;
import java.util.Scanner;

public class Turn {
    private final Player player;
    private final List<Combatant> enemies;
    private final List<Combatant> turnOrder;


    public Turn(int turnNum, Player player, List<Combatant> enemies, List<Combatant> turnOrder) {
        this.player = player;
        this.enemies = enemies;
        this.turnOrder = turnOrder;
    }

    public void executeTurn(Scanner sc) {
        player.onTurnStart();
        GameUI.printBattleState(player, enemies);
        while (true) {
            int choice = GameUI.promptTurnActionChoice(sc);

            
            Action action = null;
            Combatant target = null;
            switch (choice) {
                case 1:
                    if (enemies.isEmpty()) {
                        GameUI.printNoEnemiesToAttack();
                        continue;
                    }
                    GameUI.printCombatantTargets("\nTargets:", enemies, true);
                    int targetIndex = GameUI.promptSelectTarget(sc, enemies.size());

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
                    if (inventory.get(itemIndex).requiresTarget(player)) {
                        if (enemies.isEmpty()) {
                            GameUI.printNoValidTargets();
                            continue;
                        }
                        GameUI.printCombatantTargets("Targets for Item:", enemies, false);
                        target = enemies.get(GameUI.promptSelectTarget(sc, enemies.size()));
                    }

                    action = new ItemAction(player, itemIndex, target);
                    break;

                case 4:
                    SpecialSkill skill = PlayerSpecialSkills.forPlayer(player);
                    if (skill.requiresTarget()) {
                        if (enemies.isEmpty()) {
                            GameUI.printNoValidTargets();
                            continue;
                        }
                        GameUI.printCombatantTargets("Targets for Special Skill:", enemies, false);
                        target = enemies.get(GameUI.promptSelectTarget(sc, enemies.size()));
                    }
                    action = new SpecialSkillAction(player, target);
                    if (!action.canExecute()) {
                        GameUI.printMessage(action.blockedReason());
                        continue;
                    }
                    break;

                default:
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
