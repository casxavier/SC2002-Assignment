package game;

import combatant.Player;
import combatant.Enemy;
import java.util.List;

public class dedOrAliCount {
  private static int playeralive = 0;
  private static int enemyalive = 0;
  
  //pass in a list of players and enemies created (created in gameflow), then set counter
  public void setCounter(List<Player> players, List<Enemy> enemies) {
    playeralive = 0;
    enemyalive = 0;
    for (Player p : players) {
        if (p.isAlive()) {playeralive++;}
    }
    for (Enemy e : enemies) {
        if (e.isAlive()) {enemyalive++;}
    }
  }
  
  //return alive count
  public int PAlive() {return playeralive;}
  
  public int EAlive() {return enemyalive;}
  
}
