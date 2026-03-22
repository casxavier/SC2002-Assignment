package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import combatant.*;

public class Gameflow {
    public enum Difficulty {EASY, MEDIUM, HARD}
    private final Combatant player;
    private final List<Combatant> enemies;
    private final TurnOrderStrategy turnOrderStrategy;
    private final List<Turn> history = new ArrayList<>();
    private Difficulty difficultly;
    private int turnCount =1;
    private boolean backupSpawn = false;
    private boolean won = false;

    public Gameflow (Combatant player, Difficulty difficulty, TurnOrderStrategy turnOrderStrategy){
        this.player = player;
        this.difficultly = difficulty;
        this.enemies = spawnInitialEnemy();
        this.turnOrderStrategy = turnOrderStrategy;
    }
    
    
    private List<Combatant> getOrder(){
        List <Combatant> orderedCombatants = new ArrayList<>();
        orderedCombatants.add(player);
        orderedCombatants.addAll(enemies);

        return turnOrderStrategy.getOrder(orderedCombatants); 
    }
    
    public void showMenu(){

    }

    
    public void printTurnOrder(List<Combatant> oCombatants){
        System.out.println("Turn Order:");
            for (int i = 1; i <=oCombatants.size(); i++){
                System.out.printf("%d. %s (Speed: %d)%n", i, oCombatants.get(i).getName(), oCombatants.get(i).getSpeed());
            }
    }

    
    public void startGame(){ 
        while (player.isAlive() && !enemies.isEmpty()){ 
            printRoundHeader();
            List <Combatant> orderedCombatants = getOrder();
            printTurnOrder(orderedCombatants);
            printRoundSummary();

            for (Combatant combatant : orderedCombatants){
                if(combatant.isAlive()){
                    
                }
            }
            
            turnCount++;
        }
    }

    public void printRoundHeader(){ 
        System.out.printf("Round %d%n", turnCount);
        System.out.println("==========");
    }

    public void printRoundSummary(){ 
        System.out.printf("End of Round %d%n", turnCount);
    }

    public void printGameCompletionScreen(){
        
        String gameResult = won ? "Victory":"Defeat";
        System.out.println(gameResult);
        if (won){
            System.out.println("Congratulations, you have defeated all your enemies.");
        }else{
            System.out.println("Defeated. Don't give up, try again!");
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("\nWhat would you like to do?");
        System.out.println("1. Replay with same settings");
        System.out.println("2. Start a new game");
        System.out.println("3. Exit");
        System.out.print("Choice: ");

        int choice = sc.nextInt();
        sc.nextLine();

        switch (choice) {
            case 1:
                restartSameSettings();
                break;

            case 2:
                restartGame();
                break;

            case 3:
                System.out.println("Thanks for playing. Goodbye!");
                System.exit(0);
                break;

            default:
                break;
        }
    }
    private void restartSameSettings(){
    }
    private void restartGame(){
    }

    private List<Combatant> spawnInitialEnemy(){ 
        switch (difficultly) {

            case EASY: 
                enemies.add(new Goblin());
                enemies.add(new Goblin());
                enemies.add(new Goblin());
                break;

            case MEDIUM: 
                enemies.add(new Goblin());
                enemies.add(new Wolf());
                break;

            case HARD: 
                enemies.add(new Goblin());
                enemies.add(new Goblin());
                break;

            default:
                break;
        }
        return enemies;
    }

    private void checkBackupSpawn(){ 
        if (!backupSpawn) return;
        else{
                switch (difficultly) {
                    case MEDIUM: 
                        enemies.add(new Wolf());
                        enemies.add(new Wolf());
                        break;

                    case HARD: 
                        enemies.add(new Goblin());
                        enemies.add(new Wolf());
                        enemies.add(new Wolf());
                        break;

                    default:
                        break;
            }
        }
        
    }
}