package game;

import java.util.List;

import combatant.Combatant;

public interface TurnOrderStrategy {
    List <Combatant> getOrder(List<Combatant> combatants);
}
