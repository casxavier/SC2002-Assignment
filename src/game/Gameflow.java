package game;

import combatant.*;
import item.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
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
        gameSettings = new GameSettings(difficulty, (Player) player);
        this.enemies = spawnInitialEnemy();
        this.turnOrderStrategy = turnOrderStrategy;
    }

    private static Item promptItem(Scanner sc, String label) {
        return GameUI.promptItemChoice(sc, label);
    }
        
    public static Gameflow initializeGame(Scanner sc) {
        int choice = 0;
        Player player = null;

        GameUI.showCharacterSelection();
        choice = GameUI.promptCharacterChoice(sc);

        
        player = GameUI.createSelectedPlayer(choice);
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
        player.addItem(promptItem(sc, "First item"));
        player.addItem(promptItem(sc, "Second item"));
        GameUI.printBlankLine();

        Gameflow newGame = new Gameflow(player, gameSettings.getDifficulty(), new OrderBySpeed());
        return newGame;
    }

    
    public void executeGameLoop(Scanner sc) {

        
        List<Combatant> orderedCombatants = getOrder();
        printTurnOrder(orderedCombatants);

        
        
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
            
            if (!backupSpawned && enemies.size() == 0) {
                spawnBackupWave();
                backupSpawned = true;
            }

            
            if (enemies.isEmpty()) {
                won = true;
                break;
            }

            
            printRoundSummary();
            GameUI.waitBetweenRounds();
            turnCount++;
            history.add(currentTurn);
            orderedCombatants = getOrder();
        }

        
        if (gameSettings.getPlayer() instanceof Wizard) {
            ((Wizard) gameSettings.getPlayer()).resetArcaneBlastBonus();
        }
        gameCompletion(sc);
    }

    
    private List<Combatant> getOrder() {
        List<Combatant> orderedCombatants = new ArrayList<>();
        orderedCombatants.add(gameSettings.getPlayer());
        orderedCombatants.addAll(enemies);
        return turnOrderStrategy.getOrder(orderedCombatants);
    }

    
    public void printRoundSummary() {
        Player currPlayer = gameSettings.getPlayer();
        GameUI.printRoundSummary(turnCount, currPlayer, enemies, deadEnemies);
    }

    
    public void printTurnOrder(List<Combatant> oCombatants) {
        GameUI.printTurnOrder(oCombatants);
    }

    
    public void gameCompletion(Scanner sc) {
        
        
        GameUI.printGameCompletion(won, gameSettings.getPlayer(), enemies.size(), turnCount);
        GameUI.showEndGameOptions();

        int choice = GameUI.promptEndGameChoice(sc);

        switch (choice) {
            case 1:
                executeGameLoop(sc);
                break;
            case 2:
                initializeGame(sc);
                executeGameLoop(sc);
                break;
            case 3:
                GameUI.printExitMessage();
                System.exit(0);
                break;
            default:
                break;
        }
        sc.close();
    }

    
    private List<Combatant> spawnInitialEnemy() { 
        List <Combatant> enemyList  = new ArrayList<>();
        switch (gameSettings.getDifficulty()) {

            case EASY: 
                enemyList.add(new Goblin("Goblin A"));
                enemyList.add(new Goblin("Goblin B"));
                enemyList.add(new Goblin("Goblin C"));
                break;
            case MEDIUM:
                enemyList.add(new Goblin("Goblin A"));
                enemyList.add(new Wolf("Wolf A"));
                break;
            case HARD:
                enemyList.add(new Goblin("Goblin A"));
                enemyList.add(new Goblin("Goblin B"));
                break;
            default:
                break;
        }
        return enemyList;
    }

    private void spawnBackupWave() {
        switch (gameSettings.getDifficulty()) {
            case MEDIUM:
                enemies.add(new Wolf("Wolf A"));
                enemies.add(new Wolf("Wolf B"));
                GameUI.printBackupWave(gameSettings.getDifficulty());
                break;
            case HARD:
                enemies.add(new Goblin("Goblin A"));
                enemies.add(new Wolf("Wolf A"));
                enemies.add(new Wolf("Wolf B"));
                GameUI.printBackupWave(gameSettings.getDifficulty());
                break;
            default:
                break;
        }
    }

    private void printBattleState() {
        GameUI.printBattleState(gameSettings.getPlayer(), enemies);
    }
}
