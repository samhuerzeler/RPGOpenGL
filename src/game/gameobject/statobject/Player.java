package game.gameobject.statobject;

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
import game.gameobject.statobject.mob.Enemy;
import java.util.ArrayList;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class Player extends StatObject {

    public static final int MOUSEB_LEFT = 0;
    public static final int MOUSEB_RIGHT = 1;
    private Inventory inventory;
    private Equipment equipment;
    public boolean jumping = false;
    public long jumpingTime = 150;

    public Player(float x, float y, float z) {
        stats = new Stats(0, true);
        name = "Player";
        size = 32;
        init(x, y, z, 0.2f, 0.2f, 1.0f, size, size, size, PLAYER_ID);
        inventory = new Inventory(20);
        equipment = new Equipment(inventory);
        sightRange = 150f;
        attackRange = 43;
        attackDamage = 1;
        attackDelay.terminate();
    }

    @Override
    public void update() {
        ArrayList<GameObject> objects = Game.rectangleCollide(x, z, x + size, z + size);
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
        // Mouse Input
        if (Mouse.isButtonDown(MOUSEB_RIGHT)) {
            rotate();
            Mouse.setGrabbed(true);
        } else {
            Mouse.setGrabbed(false);
        }
        // Keyboard Input
        float movementSpeed = 1.0f;
        float rotationSpeed = 2.0f;

        boolean movingForward = Keyboard.isKeyDown(Keyboard.KEY_W);
        boolean movingBackward = Keyboard.isKeyDown(Keyboard.KEY_S);
        boolean movingLeft = Keyboard.isKeyDown(Keyboard.KEY_Q);
        boolean movingRight = Keyboard.isKeyDown(Keyboard.KEY_E);

        if ((movingForward && (movingLeft || movingRight)) || (movingBackward && (movingLeft || movingRight))) {
            movementSpeed = movementSpeed * 2 - (float) Math.sqrt(movementSpeed * movementSpeed + movementSpeed * movementSpeed);
        }
        if (movingForward) {
            move(-movementSpeed, 1);
        }
        if (movingBackward) {
            move(movementSpeed, 1);
        }
        if (movingLeft) {
            move(-movementSpeed, 0);
        }
        if (movingRight) {
            move(movementSpeed, 0);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            rotateY(-rotationSpeed);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            rotateY(rotationSpeed);
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
        ArrayList<GameObject> objects = Game.sphereCollide(x, z, attackRange);
        removeEnemiesInBack(objects);
        ArrayList<Enemy> enemies = findEnemies(objects);
        attackClosestEnemy(enemies);
        attackDelay.restart();
    }

    private void removeEnemiesInBack(ArrayList<GameObject> objects) {
        /**
         * ToDo
         */
//        for (int i = 0; i < objects.size(); i++) {
//            if (objects.get(i).getZ() + size / 2 < z + size / 2) {
//                objects.remove(objects.get(i--));
//            }
//        }
    }

    private ArrayList<Enemy> findEnemies(ArrayList<GameObject> objects) {
        ArrayList<Enemy> enemies = new ArrayList<Enemy>();
        for (GameObject go : objects) {
            if (go.getType() == ENEMY_ID) {
                enemies.add((Enemy) go);
            }
        }
        return enemies;
    }

    private void attackClosestEnemy(ArrayList<Enemy> enemies) {
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
            if (!target.isResetting()) {
                setInCombat(this, target);
                target.updateThreatMap(this, attackDamage);
                target.extendFleeRange();
                target.damage(attackDamage);
                System.out.println(name + " attacking " + target.getName() + " : " + target.getCurrentHealth() + "/" + target.getMaxHealth());
            } else {
                System.out.println(name + " : Target is resetting");
            }
        } else {
            System.out.println(name + " : No Target");
        }
    }

    private void move(float amt, float dir) {
        x += getSpeed() * Time.getDelta() * amt * Math.cos(Math.toRadians(ry + 90 * dir));
        z += getSpeed() * Time.getDelta() * amt * Math.sin(Math.toRadians(ry + 90 * dir));
    }

    public void rotateY(float amt) {
        ry += amt;
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

    private void rotate() {
        dx = Mouse.getDX();
        dy = Mouse.getDY();
        ry += (dx * 0.1f);
        rx += (dy * 0.1f);
        if (rx > 90) {
            rx = 90;
        }
        if (rx < -90) {
            rx = -90;
        }
    }

    @Override
    public boolean isResetting() {
        // player is never resetting
        return false;
    }

    @Override
    protected void die() {
        remove();
    }

    @Override
    public void updateThreatMap(StatObject so, int amt) {
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
