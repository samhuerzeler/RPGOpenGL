package game.gameobject.statobject;

import engine.Animation;
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

public abstract class Player extends StatObject {

    public enum playerClass {

        WARRIOR, PRIEST
    }
    public static final float DAMPING = 0.5f;
    public static final int MOUSEB_LEFT = 0;
    public static final int MOUSEB_RIGHT = 1;
    private Physics physics;
    private float jumpingSpeed = 6.0f;
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
        animation = new Animation();
        keyFrames = animation.loadKeyFrames("res/models/player/", "test_animation");
        loadAnimationModel();
        stats = new Stats(Stats.MAX_XP, true);
        name = "Player";
        size = 50.0f;
        spawnPosition.x = x;
        spawnPosition.y = y;
        spawnPosition.z = z;
        ceilingCollision = false;
        type = PLAYER;
        init(x, y, z, 0.2f, 0.2f, 1.0f, size, size, size);
        inventory = new Inventory(20);
        equipment = new Equipment(inventory);
        sightRange = 150.0f;
        attackRange = size;
        autoAttackDamage = 5;
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
            searchFloor();
            applyGravity();
        } else {
            position.y = currentFloor.getHeight(position.x, position.z);
            physics.resetFallingVelocity();
            jumping = false;
        }
        objectsInRange = Game.sphereCollide(position.x, position.z, sightRange);
        removeObjectsInBack(objectsInRange);
        enemiesInRange = findEnemies(objectsInRange);
        if (!isInCombat()) {
            target = null;
        }
        if (position.y == 0) {
            stats.removeHealth(stats.getMaxHealth());
            System.out.println("You fell out of the world!");
        }
        if (stats.getCurrentHealth() == 0) {
            die();
        }
        ceilingCollision = false;
        searchCeiling();
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
        float movementSpeed = MOVEMENT_SPEED;
        float rotationSpeed = 2.0f;

        boolean movingForward = input.keyPressed(Keyboard.KEY_W, true);
        boolean movingBackward = input.keyPressed(Keyboard.KEY_S, true);
        boolean movingLeft = input.keyPressed(Keyboard.KEY_Q, true);
        boolean movingRight = input.keyPressed(Keyboard.KEY_E, true);

        if (movingForward || movingBackward || movingLeft || movingRight) {
            moving = true;
        } else {
            moving = false;
            if (animation != null) {
                animation.reset();
            }
        }

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
        if (input.keyPressed(Keyboard.KEY_A, true)) {
            rotateY(-rotationSpeed);
        }
        if (input.keyPressed(Keyboard.KEY_D, true)) {
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
                                System.out.println("ability used: " + ability.getName() + " (" + ability.getValue() + "), resource subtracted: " + ability.getResourceConsumption());
                            }
                        } else {
                            System.err.println("no enemies.");
                        }
                    } else if (ability.getAbilityType() == Ability.abilityType.DEFENSIVE) {
                        /**
                         * DEFENSIVE abilities
                         */
                        delay.restart();
                        stats.addHealth(ability.getValue());
                        stats.subtractResource(ability.getResourceConsumption());
                        System.out.println("ability used: " + ability.getName() + " (" + ability.getValue() + "), resource subtracted: " + ability.getResourceConsumption());
                    } else if (ability.getAbilityType() == Ability.abilityType.BUFF) {
                        /**
                         * BUFF abilities
                         */
                    } else {
                    }
                } else {
                    System.err.println("not enough " + resource + ".");
                }
            } else {
                System.err.println("ability not ready yet.");
            }
        }
    }

    protected void autoAttack() {
        if (autoAttackDelay.isOver()) {
            if (!enemiesInRange.isEmpty()) {
                attackClosestEnemy(autoAttackDamage, autoAttackDelay, true);
            } else {
                autoAttack = false;
                System.err.println("no enemies.");
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
        ArrayList<Enemy> enemies = new ArrayList<>();
        for (GameObject go : objects) {
            if (go.getType() == HOSTILE) {
                enemies.add((Enemy) go);
            }
        }
        return enemies;
    }

    protected boolean attackClosestEnemy(int damage, Delay delay, boolean resourceGain) {
        if (enemiesInRange.size() > 0) {
            // find closest target
            Enemy closestTarget = enemiesInRange.get(0);
            if (enemiesInRange.size() > 1) {
                for (Enemy enemy : enemiesInRange) {
                    if (Util.dist(position.x, position.z, enemy.getX(), enemy.getZ()) < Util.dist(position.x, position.z, closestTarget.getX(), closestTarget.getZ())) {
                        closestTarget = enemy;
                    }
                }
            }
            // attack closest target
            if (!closestTarget.isResetting() && Util.dist(position.x, position.z, closestTarget.getX(), closestTarget.getZ()) <= attackRange) {
                if (!isInCombatWith(closestTarget)) {
                    setInCombat(this, closestTarget);
                }
                target = closestTarget;
                closestTarget.addToThreatMap(this, damage);
                closestTarget.extendFleeRange();
                closestTarget.removeHealth(damage);
                if (resourceGain) {
                    stats.addResource(12);
                }
                System.out.println(name + " attacking " + closestTarget.getName() + " for " + damage + " damage " + closestTarget.getCurrentHealth() + "/" + closestTarget.getMaxHealth());
                delay.restart();
                return true;
            } else {
                System.out.println(name + " : Target is resetting or too far away");
            }
        } else {
            System.out.println(name + " : No Target");
        }
        return false;
    }

    protected void move(float amt, float dir) {
        double xAmount = MOVEMENT_SPEED * amt * Math.cos(Math.toRadians(rotation.y + 90 * dir)) * Time.getDelta();
        double zAmount = MOVEMENT_SPEED * amt * Math.sin(Math.toRadians(rotation.y + 90 * dir)) * Time.getDelta();
        position.x += xAmount;
        position.z += zAmount;
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
        boolean aboveFloor = position.y >= currentFloor.getHeight(position.x, position.z);
        boolean belowCeiling = (position.y + jumpingSpeed * Time.getDelta()) < (currentCeiling.getHeight(position.x, position.z));

        if ((currentCeiling.getHeight(position.x, position.z) - jumpingSpeed * Time.getDelta()) < (position.y)) {
            ceilingCollision = true;
            physics.resetFallingVelocity();
        }

        if (aboveFloor && belowCeiling) {
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
            System.out.println(item.getName() + " equipped");
        }
    }

    protected void listEquippedItems() {
        EquippableItem[] equippedItems = equipment.getItems();
        System.out.println("Equipped items:");
        for (EquippableItem equippedItem : equippedItems) {
            if (equippedItem != null) {
                System.out.println(equippedItem.getName());
            }
        }
    }

    public void addItem(Item item) {
        inventory.add(item);
    }

    public void addXp(float amt) {
        stats.addXp(amt);
        System.out.println("experience added: " + amt);
    }
}
