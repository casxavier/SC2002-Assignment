package game;

import java.util.List;
import combatant.*;

public class Gameflow {
    private int turnCount =1;
    private List<Turn> history;
    private boolean won = false;

    public Gameflow (int turnCount, List<Turn> history){
        this.turnCount = turnCount;
        this.history = history;
    }

    //Show Menu when gameplay initalises
    public void showMenu(){

    }

    public void startGame(){ //Run gameplay after user initialises a new game
        while (true){ // to replace with logical conditions like player.isAlive(), enemy.allAlive() etc.
            printRoundHeader();
            printRoundSummary();
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

    public void printResult(){
        //prints either "Victory" or "Defeat" based on conditions (Pending implementation of player +enemy)
        String gameResult = won ? "Victory":"Defeat";
        System.out.println(gameResult);
        System.out.printf("%nResult: Player %s%n",gameResult);

    }
}