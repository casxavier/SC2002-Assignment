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

    
    public void showMenu(){

    }

    public void startGame(){ 
        while (true){ 
            printRoundHeader();
            printRoundSummary();
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

    public void printResult(){
        
        String gameResult = won ? "Victory":"Defeat";
        System.out.println(gameResult);
        System.out.printf("%nResult: Player %s%n",gameResult);

    }
}