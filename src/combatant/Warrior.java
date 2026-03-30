package combatant;

public class Warrior extends Player {

    public Warrior(String name) {
        super(name, 260, 40, 20, 30);
    }

    public void shieldBash(Enemy enemy) {
        enemy.hp -= this.attack;
        addStatusEffect(stun);    //stun for 2 rounds
    }
}
