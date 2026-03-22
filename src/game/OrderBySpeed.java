package game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import combatant.Combatant;

public class OrderBySpeed implements TurnOrderStrategy{
    @Override
    public List<Combatant> getOrder(List<Combatant> combatants){ // Order combatants by speed
        List<Combatant> orderedCombatants = new ArrayList<>();
        for (Combatant combatant : combatants){
            if(combatant.isAlive()) orderedCombatants.add(combatant);
        }
        orderedCombatants.sort(Comparator.comparingInt(Combatant::getSpeed).reversed());
        return orderedCombatants;
    }
}
