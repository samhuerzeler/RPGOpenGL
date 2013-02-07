package game.gameobject.statobject;

import game.Delay;
import game.Equipment;
import game.Game;
import game.GameObject;
import game.Inventory;
import game.Stats;
import game.Time;
import game.Util;
import game.gameobject.Item;
import game.gameobject.StatObject;
import game.gameobject.item.EquippableItem;
import game.gameobject.item.equippableitem.Weapon;
import java.util.ArrayList;
import org.lwjgl.input.Keyboard;

public class Player extends StatObject {

    public static final int SIZE = 32;
    public static final int FORWARD = 0;
    public static final int BACKWARD = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    private Inventory inventory;
    private Equipment equipment;
    private Delay attackDelay;
    private int facingDirection;
    public boolean jumping = false;
    public long jumpingTime = 150;

    public Player(float x, float y, float z) {
        init(x, y, z, 0.2f, 0.2f, 1.0f, SIZE, SIZE, SIZE, PLAYER_ID);
        stats = new Stats(0, true);
        name = "Player";
        inventory = new Inventory(20);
        equipment = new Equipment(inventory);
        attackDelay = new Delay(500);
        attackRange = 43;
        attackDamage = 1;
        facingDirection = 0;
        attackDelay.terminate();
    }

    @Override
    public void update() {
        ArrayList<GameObject> objects = Game.rectangleCollide(x, z, x + SIZE, z + SIZE);
        for (GameObject go : objects) {
            if (go.getType() == ITEM_ID) {
                System.out.println("Picked up " + ((Item) go).getName() + "!");
                go.remove();
                addItem((Item) go);
            }
        }
        if (jumping) {
            jump();
        } else if (!jumping && y > 0) {
            fall();
        }
    }

    public void getInput() {
        if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            move(0, -1);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            move(0, 1);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
            move(-1, 0);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            move(1, 0);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_F) && attackDelay.isOver()) {
            attack();
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            if (!jumping && y <= 0) {
                jumping = true;
                new Thread(new stopJumping()).start();
            }
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_1)) {
            Item sword = inventory.findByName("Sword");
            equipWeapon((Weapon) sword);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_2)) {
            Item bow = inventory.findByName("Bow");
            equipWeapon((Weapon) bow);
        }
    }

    private void equipWeapon(Weapon weapon) {
        if (weapon != null) {
            equipment.equip((EquippableItem) weapon);
            setAttackDamage(((Weapon) weapon).getDamage());
            setAttackRange(((Weapon) weapon).getRange());
            System.out.println(weapon.getName() + " equipped");
        }
    }

    private void attack() {
        // find objects in attack range
        ArrayList<GameObject> objects = Game.sphereCollide(x, z, attackRange);
        // remove enemies not in front of the player
        if (facingDirection == FORWARD) {
            for (int i = 0; i < objects.size(); i++) {
                if (objects.get(i).getZ() + SIZE / 2 < z + SIZE / 2) {
                    objects.remove(objects.get(i));
                }
            }
        } else if (facingDirection == BACKWARD) {
            for (int i = 0; i < objects.size(); i++) {
                if (objects.get(i).getZ() + SIZE / 2 > z + SIZE / 2) {
                    objects.remove(objects.get(i));
                }
            }
        } else if (facingDirection == LEFT) {
            for (int i = 0; i < objects.size(); i++) {
                if (objects.get(i).getX() + SIZE / 2 > x + SIZE / 2) {
                    objects.remove(objects.get(i));
                }
            }
        } else if (facingDirection == RIGHT) {
            for (int i = 0; i < objects.size(); i++) {
                if (objects.get(i).getX() + SIZE / 2 < x + SIZE / 2) {
                    objects.remove(objects.get(i));
                }
            }
        }
        // find which objects are enemies
        ArrayList<Enemy> enemies = new ArrayList<Enemy>();
        for (GameObject go : objects) {
            if (go.getType() == ENEMY_ID) {
                enemies.add((Enemy) go);
            }
        }
        // find closest enemy if one exists
        if (enemies.size() > 0) {
            Enemy target = enemies.get(0);
            if (enemies.size() > 1) {
                for (Enemy e : enemies) {
                    if (Util.dist(x, z, e.getX(), e.getZ()) < Util.dist(x, z, target.getX(), target.getZ())) {
                        target = e;
                    }
                }
            }
            // attack enemy
            target.damage(attackDamage);
            target.extendFleeRange();
            System.out.println(name + " attacking " + target.getName() + " : " + target.getCurrentHealth() + "/" + target.getMaxHealth());
        } else {
            System.out.println(name + " : No Target");
        }
        attackDelay.restart();
    }

    private void move(float magX, float magZ) {
        if (magX == 0 && magZ == 1) {
            facingDirection = FORWARD;
        }
        if (magX == 0 && magZ == -1) {
            facingDirection = BACKWARD;
        }
        if (magX == -1 && magZ == 0) {
            facingDirection = LEFT;
        }
        if (magX == 1 && magZ == 0) {
            facingDirection = RIGHT;
        }

        x += getSpeed() * magX * Time.getDelta();
        z += getSpeed() * magZ * Time.getDelta();
    }

    private void jump() {
        y += getSpeed() * Time.getDelta();
    }

    private void fall() {
        y -= getSpeed() * Time.getDelta();
    }

    private void addItem(Item item) {
        inventory.add(item);
    }

    private void addXp(float amt) {
        stats.addXp(amt);
    }

    private class stopJumping implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(jumpingTime);
                jumping = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
