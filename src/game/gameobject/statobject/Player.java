package game.gameobject.statobject;

import engine.InputHandler;
import engine.Physics;
import game.Delay;
import game.Equipment;
import game.Game;
import game.GameObject;
import game.Inventory;
import game.Item;
import game.Stats;
import game.Time;
import game.Util;
import game.gameobject.StatObject;
import game.gameobject.statobject.mob.Enemy;
import game.gameobject.statobject.player.Ability;
import game.item.EquippableItem;
import game.item.equippableitem.Weapon;
import java.util.ArrayList;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import util.Log;

public abstract class Player extends StatObject {

    public enum playerClass {

        WARRIOR, PRIEST
    }
    public static final float DAMPING = 0.5f;
    public static final int MOUSEB_LEFT = 0;
    public static final int MOUSEB_RIGHT = 1;
    private Physics physics;
    private float jumpingSpeed = 1.0f;
    private boolean jumping = false;
    private Inventory inventory;
    private Equipment equipment;
    private ArrayList<GameObject> objectsInRange;
    private ArrayList<Enemy> enemiesInRange;
    private boolean autoAttack = false;
    protected Stats.resource resource = Stats.resource.MANA;
    protected InputHandler input = new InputHandler();
    protected playerClass playerCls;

    public Player(float x, float y, float z) {
        physics = new Physics();
        stats = new Stats(100000, true);
        name = "Player";
        size = 5.0f;
        spawnPosition.x = x;
        spawnPosition.y = y;
        spawnPosition.z = z;
        type = PLAYER;
        init(x, y, z, 0.2f, 0.2f, 1.0f, size, size, size);
        loadModel("res/models/monkey.obj");
        inventory = new Inventory(20);
        equipment = new Equipment(inventory);
        sightRange = 150.0f;
        attackRange = size;
        autoAttackDamage = 20;
        autoAttackDelay.start();
        gcdDelay.start();
        nonGcdDelay.start();
        tick.start();
    }

    @Override
    public void update() {
        if (autoAttack) {
            autoAttack();
        }
        if (tick.isOver()) {
            tick.restart();
            replenishHealth();
            replenishResource(playerCls);
        }
        if (jumping) {
            jump();
        }
        if (position.y > currentFloor.getHeight(position.x, position.z)) {
            applyGravity();
        } else {
            position.y = currentFloor.getHeight(position.x, position.z);
            physics.resetFallingVelocity();
            jumping = false;
        }
        objectsInRange = Game.sphereCollide(position.x, position.z, sightRange);
        removeObjectsInBack(objectsInRange);
        enemiesInRange = findEnemies(objectsInRange);
    }

    public void getInput() {
        input.update();
        // Mouse Input
        // TODO mouse grabbed conflicts with mwheel
        if (Mouse.isButtonDown(MOUSEB_RIGHT)) {
            mouseRotate();
            Mouse.setGrabbed(true);
        } else {
            Mouse.setGrabbed(false);
        }

        // Keyboard Input
        float movementSpeed = 5.0f;
        float rotationSpeed = 2.0f;


        boolean movingForward = input.keyPressed(Keyboard.KEY_W, true);
        boolean movingBackward = input.keyPressed(Keyboard.KEY_S, true);
        boolean movingLeft = input.keyPressed(Keyboard.KEY_A, true);
        boolean movingRight = input.keyPressed(Keyboard.KEY_D, true);

        if ((movingForward && (movingLeft || movingRight)) || (movingBackward && (movingLeft || movingRight))) {
            movementSpeed = (float) (movementSpeed / Math.sqrt(2));
        }
        if (movingForward) {
            move(-movementSpeed, 1);
        }
        if (movingBackward) {
            movementSpeed *= DAMPING;
            move(movementSpeed, 1);
        }
        if (movingLeft) {
            move(-movementSpeed, 0);
        }
        if (movingRight) {
            move(movementSpeed, 0);
        }
        if (input.keyPressed(Keyboard.KEY_Q, true)) {
            rotateY(-rotationSpeed);
        }
        if (input.keyPressed(Keyboard.KEY_E, true)) {
            rotateY(rotationSpeed);
        }
        if (input.keyPressed(Keyboard.KEY_F)) {
            autoAttack = !autoAttack;
        }
        if (input.keyPressed(Keyboard.KEY_SPACE)) {
            if (!jumping && position.y < currentFloor.getHeight(position.x, position.z) + 1) {
                position.y += jumpingSpeed;
                jumping = true;
            }
        }
        if (input.keyPressed(Keyboard.KEY_NUMPAD1)) {
            EquippableItem sword = inventory.findByName("Sword");
            equipItem(sword);
        }
        if (input.keyPressed(Keyboard.KEY_NUMPAD2)) {
            EquippableItem bow = inventory.findByName("Bow");
            equipItem(bow);
        }
        if (input.keyPressed(Keyboard.KEY_C)) {
            listEquippedItems();
        }
    }

    protected void useAbility(Ability ability) {
        if (ability != null) {
            Delay delay = null;
            if (ability.getCoolDownType() == Ability.coolDownType.GLOBAL) {
                delay = gcdDelay;
            } else if (ability.getCoolDownType() == Ability.coolDownType.NON_GLOBAL) {
                delay = nonGcdDelay;
            }
            if (delay == null) {
                Log.err("delay is null");
                return;
            }
            if (delay.isOver()) {
                if (stats.getCurrentResource() >= ability.getResourceConsumption()) {
                    if (ability.getAbilityType() == Ability.abilityType.OFFENSIVE) {
                        /**
                         * OFFENSIVE abilities
                         */
                        if (!enemiesInRange.isEmpty()) {
                            if (attackClosestEnemy(ability.getValue(), delay, false)) {
                                stats.subtractResource(ability.getResourceConsumption());
                                Log.p("ability used: " + ability.getName() + " (" + ability.getValue() + "), resource subtracted: " + ability.getResourceConsumption());
                            }
                        } else {
                            Log.err("no enemies.");
                        }
                    } else if (ability.getAbilityType() == Ability.abilityType.DEFENSIVE) {
                        /**
                         * DEFENSIVE abilities
                         */
                        delay.restart();
                        stats.addHealth(ability.getValue());
                        stats.subtractResource(ability.getResourceConsumption());
                        Log.p("ability used: " + ability.getName() + " (" + ability.getValue() + "), resource subtracted: " + ability.getResourceConsumption());
                    } else if (ability.getAbilityType() == Ability.abilityType.BUFF) {
                        /**
                         * BUFF abilities
                         */
                    } else {
                    }
                } else {
                    Log.err("not enough " + resource + ".");
                }
            } else {
                Log.err("ability not ready yet.");
            }
        }
    }

    protected void autoAttack() {
        if (autoAttackDelay.isOver()) {
            if (!enemiesInRange.isEmpty()) {
                attackClosestEnemy(autoAttackDamage, autoAttackDelay, true);
            } else {
                autoAttack = false;
            }
        }
        if (!isInCombat()) {
            autoAttack = false;
        }
    }

    protected void removeObjectsInBack(ArrayList<GameObject> objects) {
        // TODO remove enemies in back
    }

    protected ArrayList<Enemy> findEnemies(ArrayList<GameObject> objects) {
        ArrayList<Enemy> e = new ArrayList<Enemy>();
        for (GameObject go : objects) {
            if (go.getType() == ENEMY) {
                e.add((Enemy) go);
            }
        }
        return e;
    }

    protected boolean attackClosestEnemy(int damage, Delay delay, boolean resourceGain) {
        if (enemiesInRange.size() > 0) {
            // find closest target
            Enemy closestTarget = enemiesInRange.get(0);
            if (enemiesInRange.size() > 1) {
                for (Enemy e : enemiesInRange) {
                    if (Util.dist(position.x, position.z, e.getX(), e.getZ()) < Util.dist(position.x, position.z, closestTarget.getX(), closestTarget.getZ())) {
                        closestTarget = e;
                    }
                }
            }
            // attack closest target
            if (!closestTarget.isResetting() && Util.dist(position.x, position.z, closestTarget.getX(), closestTarget.getZ()) <= attackRange) {
                if (!isInCombat()) {
                    setInCombat(this, closestTarget);
                }
                closestTarget.addToThreatMap(this, damage);
                closestTarget.extendFleeRange();
                closestTarget.damage(damage);
                if (resourceGain) {
                    stats.addResource(12);
                }
                Log.p(name + " attacking " + closestTarget.getName() + " for " + damage + " damage " + closestTarget.getCurrentHealth() + "/" + closestTarget.getMaxHealth());
                delay.restart();
                return true;
            } else {
                Log.p(name + " : Target is resetting or too far away");
            }
        } else {
            Log.p(name + " : No Target");
        }
        return false;
    }

    protected void move(float amt, float dir) {
        // TODO add speed based scaling
        position.x += MOVEMENT_SPEED * amt * Math.cos(Math.toRadians(rotation.y + 90 * dir));
        position.z += MOVEMENT_SPEED * amt * Math.sin(Math.toRadians(rotation.y + 90 * dir));
    }

    protected void rotateY(float amt) {
        rotation.y += amt * Time.getDelta();
    }

    protected void mouseRotate() {
        direction.x = Mouse.getDX();
        direction.y = Mouse.getDY();
        rotation.y += (direction.x * 0.1f);
        rotation.x += (direction.y * 0.1f);
        if (rotation.x > 90) {
            rotation.x = 90;
        }
        if (rotation.x < -90) {
            rotation.x = -90;
        }
    }

    protected void jump() {
        if (position.y >= currentFloor.getHeight(position.x, position.z)) {
            position.y += jumpingSpeed * Time.getDelta();
        } else {
            jumping = false;
        }
    }

    protected void applyGravity() {
        position.y -= physics.getFallingDistance();
    }

    protected void equipItem(EquippableItem item) {
        if (item != null) {
            equipment.equip(item);
            if (item.getSlot() == EquippableItem.WEAPON_SLOT) {
                setAttackDamage(((Weapon) item).getDamage());
                setAttackRange(((Weapon) item).getRange());
            }
            Log.p(item.getName() + " equipped");
        }
    }

    protected void listEquippedItems() {
        EquippableItem[] equippedItems = equipment.getItems();
        Log.p("Equipped items:");
        for (EquippableItem equippedItem : equippedItems) {
            if (equippedItem != null) {
                Log.p(equippedItem.getName());
            }
        }
    }

    public void addItem(Item item) {
        inventory.add(item);
    }

    public void addXp(float amt) {
        stats.addXp(amt);
        Log.p("experience added: " + amt);
    }
}
