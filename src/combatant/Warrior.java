package combatant;

public class Warrior extends Player {

    public Warrior(String name) {
        super(name, 260, 40, 20, 30);
    }

    public void shieldBash(Enemy name) {
        Enemy.name.hp -= Warrior.name.attack;
        Enemy.actor.canAct() = 0;
    }
}
