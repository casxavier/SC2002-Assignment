package game;

import action.ArcaneBlastSkill;
import combatant.*;
import item.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Map;

public class Gameflow {
    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    private static GameSettings gameSettings;
    private final List<Combatant> enemies;
    private final List<Combatant> deadEnemies = new ArrayList<>();
    private final TurnOrderStrategy turnOrderStrategy;
    private final List<Turn> history = new ArrayList<>();
    private Turn currentTurn;
    private int turnCount = 1;
    private boolean backupSpawned = false;
    private boolean won = false;

    
    public Gameflow(Combatant player, Difficulty difficulty, TurnOrderStrategy turnOrderStrategy) {
        List<Item> preservedTemplate = null;
        if (gameSettings != null && !gameSettings.getStartingItemTemplate().isEmpty()) {
            preservedTemplate = new ArrayList<>(gameSettings.getStartingItemTemplate());
        }
        gameSettings = new GameSettings(difficulty, (Player) player);
        if (preservedTemplate != null && !preservedTemplate.isEmpty()) {
            gameSettings.setStartingItemTemplate(preservedTemplate);
        } else {
            gameSettings.setStartingItemTemplate(copyItemsAsNew(((Player) player).getInventory()));
        }
        this.enemies = spawnInitialEnemy();
        this.turnOrderStrategy = turnOrderStrategy;
    }
        
    public static Gameflow initializeGame(Scanner sc) {
        int choice = 0;
        Player player = null;

        GameUI.showCharacterSelection();
        choice = GameUI.promptCharacterChoice(sc);

        
        switch (choice) {
            case 1:
                player = new Warrior("Warrior");
                break;
            case 2:
                player = new Wizard("Wizard");
                break;
        }
        GameUI.printBlankLine();

        
        int chosenDifficulty = 0;
        GameUI.showDifficultySelection();
        chosenDifficulty = GameUI.promptDifficultyChoice(sc);

        gameSettings = new GameSettings(null, player);
        switch (chosenDifficulty) {
            case 1:
                gameSettings.setDifficulty(Difficulty.EASY);
                break;
            case 2:
                gameSettings.setDifficulty(Difficulty.MEDIUM);
                break;
            case 3:
                gameSettings.setDifficulty(Difficulty.HARD);
                break;
            default:
                gameSettings.setDifficulty(Difficulty.EASY);
                break;
        }

        GameUI.showItemSelection();
        player.addItem(GameUI.promptItemChoice(sc, "First item"));
        player.addItem(GameUI.promptItemChoice(sc, "Second item"));
        gameSettings.setStartingItemTemplate(copyItemsAsNew(player.getInventory()));
        GameUI.printBlankLine();

        Gameflow newGame = new Gameflow(player, gameSettings.getDifficulty(), new OrderBySpeed());
        return newGame;
    }

    
    public void executeGameLoop(Scanner sc) {

        
        List<Combatant> orderedCombatants = getOrder();
        GameUI.printTurnOrder(orderedCombatants);

        
        
        Player player = gameSettings.getPlayer();
        while (player.isAlive()) {

            GameUI.printTurnHeader(turnCount);

            printBattleState();

            
            currentTurn = new Turn(turnCount, player, enemies, orderedCombatants);
            currentTurn.executeTurn(sc);

            
            if(!player.isAlive()){
                break;
            }
            
            for (int i = 0; i < enemies.size(); i++) {
                if (!enemies.get(i).isAlive()) {
                    deadEnemies.add(enemies.get(i));
                    enemies.remove(i);
                    i--;
                }
            }

            
            Player currPlayer = gameSettings.getPlayer();
            GameUI.printRoundSummary(turnCount, currPlayer, enemies, deadEnemies);
            GameUI.waitBetweenRounds();
            turnCount++;
            history.add(currentTurn);

            
            if (!backupSpawned && enemies.size() == 0) {
                spawnBackupWave();
                backupSpawned = true;
            }

            
            if (enemies.isEmpty()) {
                won = true;
                break;
            }

            orderedCombatants = getOrder();
        }

        gameCompletion(sc);
    }

    
    private List<Combatant> getOrder() {
        List<Combatant> orderedCombatants = new ArrayList<>();
        orderedCombatants.add(gameSettings.getPlayer());
        orderedCombatants.addAll(enemies);
        return turnOrderStrategy.getOrder(orderedCombatants);
    }

    
    public void gameCompletion(Scanner sc) {
        
        
        GameUI.printGameCompletion(won, gameSettings.getPlayer(), enemies.size(), turnCount);
        GameUI.showEndGameOptions();

        int choice = GameUI.promptEndGameChoice(sc);

        switch (choice) {
            case 1: {
                
                Player currentPlayer = gameSettings.getPlayer();
                Difficulty currentDifficulty = gameSettings.getDifficulty();

                
                Player replayPlayer;
                if (currentPlayer instanceof Warrior) {
                    replayPlayer = new Warrior(currentPlayer.getName());
                } else if (currentPlayer instanceof Wizard) {
                    replayPlayer = new Wizard(currentPlayer.getName());
                } else {
                    replayPlayer = new Warrior(currentPlayer.getName());
                }

                for (Item item : copyItemsAsNew(gameSettings.getStartingItemTemplate())) {
                    replayPlayer.addItem(item);
                }

                Gameflow replayGame = new Gameflow(replayPlayer, currentDifficulty, new OrderBySpeed());
                replayGame.executeGameLoop(sc);
                return;
            }
            case 2: {
                Gameflow newGame = initializeGame(sc);
                newGame.executeGameLoop(sc);
                return;
            }
            case 3:
                GameUI.printExitMessage();
                return;
            default:
                break;
        }
    }

    
    private List<Combatant> spawnInitialEnemy() { 
        List<Combatant> enemyList = new ArrayList<>();
        List<Map<String, String>> waveConfig = gameSettings.getInitialWaveConfig();
        for (Map<String, String> config : waveConfig) {
            String type = config.get("type");
            String name = config.get("name");
            Combatant enemy = createEnemy(type, name);
            if (enemy != null) {
                enemyList.add(enemy);
            }
        }
        return enemyList;
    }

    private void spawnBackupWave() {
        List<Map<String, String>> backupConfig = gameSettings.getBackupWaveConfig();
        for (Map<String, String> config : backupConfig) {
            String type = config.get("type");
            String name = config.get("name");
            Combatant enemy = createEnemy(type, name);
            if (enemy != null) {
                enemies.add(enemy);
            }
        }
        if (!backupConfig.isEmpty()) {
            GameUI.printBackupWave(gameSettings.getDifficulty());
        }
    }

    private Combatant createEnemy(String type, String name) {
        switch (type) {
            case "GOBLIN":
                return new Goblin(name);
            case "WOLF":
                return new Wolf(name);
            default:
                return null;
        }
    }

    private void printBattleState() {
        GameUI.printBattleState(gameSettings.getPlayer(), enemies);
    }

    private static List<Item> copyItemsAsNew(List<Item> inventory) {
        List<Item> out = new ArrayList<>();
        for (Item item : inventory) {
            out.add(item.duplicate());
        }
        return out;
    }
}
