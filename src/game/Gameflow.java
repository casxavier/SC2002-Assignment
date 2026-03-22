package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import combatant.*;

public class Gameflow {
    private final Combatant player;
    private final List<Combatant> enemies;
    private final TurnOrderStrategy turnOrderStrategy;
    private final List<Turn> history = new ArrayList<>();
    private Difficulty difficultly;
    private int turnCount =1;
    private boolean backupSpawn = false;
    private boolean won = false;

    public Gameflow (Combatant player, List<Combatant> enemies, 
        TurnOrderStrategy turnOrderStrategy){
        this.player = player;
        this.enemies = enemies;
        this.turnOrderStrategy = turnOrderStrategy;
    }

    //Show Menu when gameplay initalises
    public void showMenu(){

    }

    public void startGame(){ //Run gameplay after user initialises a new game
        while (player.isAlive() && !enemies.isEmpty()){ // ensures player is alive and there are enemies left
            printRoundSummary();
            //checkBackupSpawn();
            turnCount++;
        }
    }

    public void printRoundHeader(){ // print header at start of each round
        System.out.printf("Round %d%n", turnCount);
        System.out.println("==========");
    }

    public void printRoundSummary(){ // prints summary of each round
        System.out.printf("End of Round %d%n", turnCount);
    }

    public void printGameCompletionScreen(){
        //prints either "Victory" or "Defeat" based on conditions (Pending implementation of player +enemy)
        String gameResult = won ? "Victory":"Defeat";
        System.out.println(gameResult);
        if (won){
            System.out.println("Congratulations, you have defeated all your enemies.");
        }else{
            System.out.println("Defeated. Dont't give up, try again!");
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("\nWhat would you like to do?");
        System.out.println("  1. Replay with same settings");
        System.out.println("  2. Start a new game");
        System.out.println("  3. Exit");
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

            case EASY: // Spawn 3 Goblins
                enemies.add(new Goblin());
                enemies.add(new Goblin());
                enemies.add(new Goblin());
                break;

            case MEDIUM: // Spawn 1 Goblin 1 Wolf
                enemies.add(new Goblin());
                enemies.add(new Wolf());
                break;

            case HARD: // Spawn 2 Goblins
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
                    case MEDIUM: // Backup spawn 2 Wolf
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