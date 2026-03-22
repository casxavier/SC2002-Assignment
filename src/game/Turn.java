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
}


