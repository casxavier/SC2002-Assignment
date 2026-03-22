package game;

import java.util.ArrayList;
import java.util.List;
import combatant.Combatant;

public class Turn {
    private final int turnNum;
    private Combatant characterActed; //character who acted this turn
    private String actionType;
    private String targetName; 
    private final int dmgDealt;
    private boolean isStunned;

    public Turn (int turnNum, Combatant characterActed, String actionType, 
    String targetName, int dmgDealt, boolean isStunned){
        this.turnNum = turnNum;
        this.characterActed = characterActed;
        this.actionType = actionType;
        this.targetName = targetName;
        this.dmgDealt = dmgDealt;
        this.isStunned = isStunned;
    }

    // print quick summary after each turn ends
    public String printSummary(){
        if(isStunned){ // character acting has stunned effect
            return String.format("Turn %d: %s was stunned, not able to act.%n", turnNum, characterActed);
        }
        if(targetName.equals("ALL")){ //arcane blast
            return String.format("Turn %d: %s used arcane blast, all enemies dealt %d damage each.%n", turnNum, characterActed, characterActed.getAttack());
        }
        return String.format("Turn %d: %s dealt %d damage on %s.%n", turnNum, characterActed.getAttack(), targetName); //basic attack
    }
}


