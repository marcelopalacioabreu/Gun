package com.upa.gun.enemy;

import com.badlogic.gdx.math.Vector2;
import com.upa.gun.*;
import com.badlogic.gdx.Gdx;

import java.util.Map;
import static com.upa.gun.Settings.*;

public class Enemy extends Entity {
    private static final float DAMAGE_FLASH_LENGTH = 1/20f;

    public float timeElapsed;

    private int startHealth;
    private int health;

    private float horizontalDifference;
    private float verticalDifference;

    private float directionalUpdateCounter;
    private float directionalUpdateTimer;

    float timeSinceAttack;

    private EnemyState state;

    public boolean damagedFrame;
    private float damagedFrameTime;

    AttackRotation rotation;

    public float opacity;

    public Map<String, String> sprites;
    public String sprite;

    private Hitboxes hitbox;

    private int id;

    Enemy(EnemyInfo info, Vector2 position) {
        super(position, new Vector2(info.width, info.height));

        try {
            createHitbox(info.hitboxType, info.hitboxWidth, info.hitboxHeight);
        } catch (UnrecognizedHitboxTypeException e) {
            e.printStackTrace();
        }

        timeElapsed = 20.0f;

        health = info.health;
        startHealth = health;

        state = new EnemyActiveState();

        damagedFrame = false;
        damagedFrameTime = 0f;

        sprites = info.sprites;
        sprite = "default";
        opacity = 1f;

        rotation = info.rotation.copy();
        rotation.setEnemy(this);

        directionalUpdateCounter = 1.0f;
        directionalUpdateTimer = 1.0f;


        id = info.id;
    }

    public int getID() { return id; }
    public int getStartHealth() { return startHealth; }
    public int getHealth() { return health; }


    @Override
    public Hitboxes getHitbox() {
        return hitbox;
    }

    private void createHitbox(String hitboxType, int width, int height) throws UnrecognizedHitboxTypeException {
        hitbox = new Hitboxes();
        if (hitboxType.equals("rectangular")) {
            RectangularHitbox center = new RectangularHitbox(getPosition(), new Vector2(width, height));
            centerRectangularHitbox(center);
            hitbox.addHitbox("center", center);

        } else {
            Gdx.app.log("Enemy creation:","Failed to load hitbox type.");
            throw new UnrecognizedHitboxTypeException(hitboxType);
        }

        hitbox.setActive(true);


    }

    public void damage(int damage) {
        health -= damage;
        if (health <= 0) {
            state = new EnemyFadingState(this);

            int rand = (int)(Math.random() * 1); //random powerup checker
            if(rand == 0) {
                int type = (int)(Math.random() * PowerupFactory.getInstance().powerups.size());
                World.powerups.add(World.getInstance().getPowerupFactory().createPowerup(type, this.getPosition()));
                Powerup added = World.powerups.get(World.powerups.size()-1);
                //System.out.println(added.info.effectDescription);
            }


            World.spawner.slimesKilled++;
            World.spawner.slimesKilledSinceLastBoss++;
        } else {
            damagedFrame = true;
        }
    }

    /**
     * Change this enemy's currently showing sprite.
     * @param spriteKey Key of the sprite to change to
     */
    private void changeSprite(String spriteKey) {
        if (sprites.containsKey((spriteKey))) {
            sprite = spriteKey;
        }
    }


    public void update(float delta) {
        super.update(delta);
        timeElapsed += delta;
        directionalUpdateCounter += delta;
        rotation.cycle(delta, getPosition());

        changeSprite(rotation.currentAttack().getSprite());

        if (id == 2) { //bandaid boss hitbox fix
            centerRectangularHitbox((RectangularHitbox)getHitbox().getChild("center"));
        }

        if (damagedFrame) {
            damagedFrameTime += delta;
            if (damagedFrameTime >= DAMAGE_FLASH_LENGTH) {
                damagedFrame = false;
                damagedFrameTime = 0f;
            }
        }

        if (state.mobileType() == 1) { //active state
            setVelocity(getVelocity().x, getVelocity().y);
            if (directionalUpdateCounter >= directionalUpdateTimer) {
                directionalUpdateCounter = 0.0f;
                directionalUpdateTimer = generateNewDirectionUpdateTimer();
                updateDirection();
                move();
            }
        } else if(state.mobileType() == 2) { //dying/fading state
            move();
            setVelocity(getVelocity().x/3, getVelocity().y/3);
        } else {
            setVelocity(0, 0);
        }

        state.update(delta);
    }

    private float generateNewDirectionUpdateTimer() {
        int range = MAX_SLIME_UPDATE_TIMER - MIN_SLIME_UPDATE_TIMER;
        int timer = (int)(Math.random() * (double)(range));
        return (float)timer / 100 + (float)MIN_SLIME_UPDATE_TIMER / 100;
    }

    private void updateDirection() {
        Vector2 playerPos = World.player.getPosition();
        float playerX = playerPos.x;
        float playerY = playerPos.y;
        float slimeX = getPosition().x;
        float slimeY = getPosition().y;
        horizontalDifference = introduceOffset(playerX - slimeX);
        verticalDifference = introduceOffset(playerY - slimeY);
    }

    private float introduceOffset(float value) {
        if(value > 50 || value < -50) {

            float minOffset = value/5;


            int addOffset = (int)(Math.random() * (value/2));
            float offset = (float)addOffset + minOffset;
            int direction = (int)(Math.random() * 2);
            if(direction == 0) {
                value += offset;
            }
            else {
                value -= offset;
            }
        }
        return value;
    }

    private void move() {
        float xSquare = horizontalDifference * horizontalDifference;
        float ySquare = verticalDifference * verticalDifference;
        double currentSquare = xSquare + ySquare;
        float currentSpeed = (float)Math.sqrt(currentSquare);
        float speedRatio = Settings.SLIME_SPEED / currentSpeed;
        setVelocity(horizontalDifference * speedRatio, verticalDifference * speedRatio);
    }

    public EnemyState getState() {
        return state;
    }

    public void setState(EnemyState state) {
        this.state = state;
    }

    public void setTimeElapsed(float timeElapsed) {
        this.timeElapsed = timeElapsed;
    }
}
