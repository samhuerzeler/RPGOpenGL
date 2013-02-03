package game.gameobject;

import game.Delay;
import game.Game;
import game.Time;
import game.Util;
import game.gameobject.item.Item;
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
    private int attackRange;
    private Delay attackDelay;
    private int attackDamage;
    private int facingDirection;

    public Player(float x, float y, float z) {
        init(x, y, z, 0.2f, 0.2f, 1.0f, SIZE, SIZE, SIZE, PLAYER_ID);
        stats = new Stats(0, true);
        inventory = new Inventory(20);
        equipment = new Equipment(inventory);
        attackDelay = new Delay(500);
        attackRange = 43;
        attackDamage = 2;
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
    }

    public void attack() {
        System.out.print("attacking");
        // find objects in attack range
        ArrayList<GameObject> objects = new ArrayList<GameObject>();
        if (facingDirection == FORWARD) {
            objects = Game.rectangleCollide(x, z, x + SIZE, z + attackRange);
        } else if (facingDirection == BACKWARD) {
            objects = Game.rectangleCollide(x, z - attackRange + SIZE, x + SIZE, z);
        } else if (facingDirection == LEFT) {
            objects = Game.rectangleCollide(x - attackRange + SIZE, z, x, z + SIZE);
        } else if (facingDirection == RIGHT) {
            objects = Game.rectangleCollide(x, z, x + attackRange, z + SIZE);
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
            System.out.println(" : " + target.getCurrentHealth());
        } else {
            System.out.println(" : No Target");
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

    public void addItem(Item item) {
        inventory.add(item);
    }

    public void addXp(float amt) {
        stats.addXp(amt);
    }

    public class stopJumping implements Runnable {

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
